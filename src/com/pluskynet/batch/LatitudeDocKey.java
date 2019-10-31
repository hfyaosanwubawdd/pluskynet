package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pluskynet.domain.Batchdata;
import com.pluskynet.domain.Docidandruleid;
import com.pluskynet.domain.LatitudedocKey;
import com.pluskynet.otherdomain.Otherrule;
import com.pluskynet.test.OtherRule;
import com.pluskynet.test.OtherRuleSave;
import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONObject;

/**
 * @author HF
 * @version 创建时间：2019年7月5日 上午9:39:48 类说明 docsectionandrule ---> latitudedoc_key
 */
public class LatitudeDocKey {
	private static final Logger LOGGER = Logger.getLogger(LatitudeDocKey.class);
	public static int flag = 9;
	public static void run() {
		for (int i = 0; i < 10; i++) {
			final int flag = i;
			Thread t = new Thread() {
				@Override
				public void run() {
					latitudeDocKey(flag);
				}
			};
			t.start();
		}
	}
	public static void latitudeDocKey(int flag) {
		Map<Integer, List<Otherrule>> latitudeOtherRuleMap = new HashMap<Integer, List<Otherrule>>();
		Map<Integer, String> latitudeNameMap = new HashMap<Integer, String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			ResultSet executeQuery = stmt.executeQuery("select * from latitude where latitudeid in("
					+ "	select latitudeid from latitudeaudit where stats = '0' and rule is not null and rule != "
					+ "'[]' and rule != '' and latitudetype=1)");
			while (executeQuery.next()) {
				Integer latitudeId = executeQuery.getInt("latitudeid");
				String rule = executeQuery.getString("rule");
				List<Otherrule> list = OtherRule.ruleFormat(rule, executeQuery.getInt("ruletype"));// 规则整理
				latitudeOtherRuleMap.put(latitudeId, list);
				latitudeNameMap.put(latitudeId, executeQuery.getString("latitudename"));
			}
			while (true) {
				LOGGER.info("start line_60");
				rs = stmt.executeQuery("select * from docsectionandrule where (id % 10 = "+flag+") and state = 2 limit 1000");
				if (BatchUtils.isBlank(rs)) {
					System.out.println("ok");
					return;
				}
				String idIn = "(";
				String insertSql = "insert into latitudedoc_key(documentid,latitudename,latitudeid,location,sectionid) values ";
				String documentid = "";
				while (rs.next()) {
					boolean a = false;
					documentid = rs.getString("documentsid");
					String sectionname = rs.getString("sectionname");
					String oldsectiontext = rs.getString("sectiontext");
					Integer id = rs.getInt("id");
					Integer ruleid = rs.getInt("ruleid");
					idIn += id + ",";
					Iterator<Entry<Integer, List<Otherrule>>> iterator = latitudeOtherRuleMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<Integer, List<Otherrule>> next = iterator.next();
						Integer key = next.getKey();
						List<Otherrule> list = next.getValue();
						if (null == list || list.size() < 1) {
							continue;
						}
						for (Otherrule otherrule : list) {
							String rulesec = otherrule.getSectionname();
							if (null == rulesec && "[]".equals(rulesec) && "".equals(rulesec)) {
								continue;
							}
							try {// getSectionname 可能是中文也可能是int串
								if (ruleid != Integer.valueOf(rulesec)) {
									continue;
								}
							} catch (Exception e) {
								continue;
							}
							
							JSONObject jsonObject = JSONObject.fromObject(otherrule);
							String location = "";
							String contains = jsonObject.getString("contains");
							if (contains.equals("")) {
								a = true;
								location = "0,0;";
							} else {
								if (contains.contains("*")) {
									Pattern containp = OtherRuleSave.startRuleFomat(contains);// 开始的规则格式化
									Matcher matcher = containp.matcher(oldsectiontext);
									if (matcher.find()) {
										String beginIndex = matcher.group();
										a = true;
										location = String.valueOf(oldsectiontext.indexOf(beginIndex)) + ","
												+ beginIndex.length() + ";";
									}
								} else if (contains.contains("&")) {
									String[] contain = contains.split("\\&");// 包含
									for (int x = 0; x < contain.length; x++) {
										if (oldsectiontext.contains(contain[x].toString())) {
											a = true;
											if (location.equals("")) {
												location = String.valueOf(oldsectiontext.indexOf(contain[x].toString()))
														+ "," + contain[x].toString().length() + ";";
											} else {
												location = location
														+ String.valueOf(oldsectiontext.indexOf(contain[x].toString()))
														+ "," + contain[x].toString().length() + ";";
											}
										} else {
											a = false;
											location = "";
											continue;
										}
									}
								} else {
									if (oldsectiontext.contains(contains)) {
										a = true;
										location = String.valueOf(oldsectiontext.indexOf(contains)) + ","
												+ contains.length() + ";";
									} else {
										a = false;
										continue;
									}
								}
							}
							if (a) {// 以上陪配到任意一种
								String[] notcon = jsonObject.getString("notcon").split(";;");
								for (int k = 0; k < notcon.length; k++) {
									if (notcon[k].contains("*")) {
										Pattern containp = OtherRuleSave.endRuleFomat(notcon[k]);
										Matcher matcher = containp.matcher(oldsectiontext);
										if (!matcher.find()) {
											a = true;
										} else {
											a = false;
											break;
										}
									} else if (!oldsectiontext.contains(notcon[k])) {
										a = true;
									} else if (notcon[k].equals("")) {
										a = true;
									} else {
										a = false;
										break;
									}
								}
							}
							if (a) {
								if ("0,0;".equals(location)) {
//									if (!StringUtils.isBlank(location) && !"0,0;".equals(location)) {
									insertSql += "('" + documentid + "','" + latitudeNameMap.get(key) + "'," + key + ",'" + location + "',"+id+"),";
									break;
								}
							}
							a = false;
						}
					}
				}
				insertSql = insertSql.substring(0, insertSql.length() - 1);
				if (!insertSql.endsWith("values")) {
					stmt.addBatch(insertSql);
					stmt.executeBatch();
				}
				idIn = idIn.substring(0, idIn.length() - 1) + ")";
				stmt.executeUpdate("update docsectionandrule set state = 3 where id in " + idIn);
				conn.commit();
				stmt.clearBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			C3P0connsPollUTIL.close(conn, stmt, rs);
		}
	}
	
	
	public static void addKey() {
		if (flag != 9) {
			LOGGER.info("定时任务正在执行");
			return;
		}
		flag = 10;
		Connection conn = null;
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			stmt1 = conn.createStatement();
			String  doc_id = "";
			while(true) {
//				if (Integer.valueOf(sdf.format(new Date())) == 8) {
//					LOGGER.info("定时任务自动停止");
//					flag = 9;
//					return;
//				}
				rs = stmt.executeQuery("select documentid from latitudedoc_key where data is null limit 1");
				if (BatchUtils.isBlank(rs)) {
					return;
				}
				while (rs.next()) {
					doc_id = rs.getString("documentid");
				
				}
				int executeUpdate = stmt1.executeUpdate("update latitudedoc_key a left join article_fiter_all b on a.documentid = b.doc_id set a.spcx = "
						+ "b.spcx,a.reason=b.reason,a.data=b.data,a.doc_type = b.doctype where b.doc_id = '"+doc_id+"'");
				conn.commit();
				LOGGER.info(doc_id+"-------"+executeUpdate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void addKeyCountDownLath() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			while(true) {
				rs = stmt.executeQuery("select DISTINCT documentid from latitudedoc_key where data is null limit 700");
				rs.last();
				int row = rs.getRow();
				if (row < 1) {
					return;
				}
				final CountDownLatch countDownLatch = new CountDownLatch(row);
				LOGGER.info(row);
				rs.beforeFirst();
				ExecutorService t = Executors.newCachedThreadPool();
				while (rs.next()) {
					String doc_id = rs.getString("documentid");
					t.execute(new Runnable() {
						@Override
						public void run() {
							Connection conn = null;
							Statement stmt = null;
							ResultSet rs = null;
							try {
								conn = C3P0connsPollUTIL.getConnection();
								conn.setAutoCommit(false);
								stmt = conn.createStatement();
								int executeUpdate = stmt.executeUpdate("update latitudedoc_key a left join article_fiter_all b on a.documentid = b.doc_id set a.spcx = "
										+ "b.spcx,a.reason=b.reason,a.data=b.data,a.doc_type = b.doctype where b.doc_id = '"+doc_id+"'");
								conn.commit();
								LOGGER.info(doc_id+"-------"+executeUpdate);
							} catch (Exception e) {
								e.printStackTrace();
							}finally {
								countDownLatch.countDown();
								C3P0connsPollUTIL.close(conn, stmt, rs);
							}
						}
					});
				}
				conn.commit();
				t.shutdown();
				countDownLatch.await();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, rs);}
	}
	
	public static void main(String[] args) {
		addKeyCountDownLath();
	}
}
