package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jsoup.helper.StringUtil;

import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONObject;

/**
* @author HF
* @version 创建时间：2019年6月24日 下午1:55:19
* 类说明	demo06项目刑事案由  跑t_export_doc 
*/
public class Demo6TExportAll {

	public static Map<String,String> dictMap = new HashMap<String, String>();
	public static Map<String,String> idMap = new HashMap<String, String>();
	public static void main(String[] args) {
		executeThread();
	}
	
	public static void executeThread() {
		String level = "";
		String insertSql = "";
		String uuid = "";
		String court = "";
		String doc_id = "";
		String doc_info = "";
		Connection readerConn = null;
		Statement readerStmt = null;
		Statement readerStmt2 = null;
		ResultSet readerRs = null;
//		Connection wenshuConn = null;
//		Statement wenshuStmt = null;
//		Statement wenshuStmt2 = null;
//		ResultSet wenshuRs = null;
		int ruleid = 0;
		try {
			readerConn = C3P0connsPollUTIL.getConnection();
			readerConn.setAutoCommit(false);
			readerStmt = readerConn.createStatement();
			readerStmt2 = readerConn.createStatement();

//			wenshuConn = JDBCwenshu.getConnection();
//			wenshuConn.setAutoCommit(false);
//			wenshuStmt = wenshuConn.createStatement();
//			wenshuStmt2 = wenshuConn.createStatement();

			readerRs = readerStmt.executeQuery("select * from article_dict");
			while (readerRs.next()) {
				dictMap.put(readerRs.getString("dict_name"), readerRs.getLong("id") + "");
				idMap.put(readerRs.getLong("id") + "",readerRs.getString("dict_name"));
			}
			String reason = "";
			String idIn = "(";
			while (true) {
				ResultSet executeQuery2 = readerStmt.executeQuery("select * from article_fiter_penal_pjs where type = 5  limit 1000");
				executeQuery2.last();
				if (executeQuery2.getRow() == 0) {
					System.out.println(" article_fiter_penal 无数据");
					break;
				}
				executeQuery2.beforeFirst();
				idIn = "(";
				while (executeQuery2.next()) {
					idIn += "'" + executeQuery2.getString("doc_id") + "',";
				}
				idIn = idIn.substring(0, idIn.length() - 1) + ")";
				insertSql = "insert into t_export_doc(ed_docInfo,ed_docId,ed_typeId,ed_reasonId,ed_reason) values ";
				String sql = "select * from article_fiter_all where doc_id in " + idIn;
				ResultSet executeQuery = readerStmt2.executeQuery(sql);
				executeQuery.last();
				if (executeQuery.getRow() == 0) {
					readerStmt.execute("update article_fiter_penal_pjs set type = 6 where doc_id in " + idIn);
					readerConn.commit();
					continue;
				}
				executeQuery.beforeFirst();
				
				while (executeQuery.next()) {
					Map<String, String> map = new HashMap<String, String>();
					court = "";
					doc_id = executeQuery.getString("doc_id");
					court = idMap.get(executeQuery.getLong("court")+"");
					map.put("court", court);
					
					map.put("step", idMap.get(executeQuery.getLong("spcx")+""));
					map.put("action",idMap.get(executeQuery.getLong("doctype")+""));
					map.put("coid", executeQuery.getString("caseno"));
					map.put("date", executeQuery.getString("trialdate"));
					map.put("yeardate", executeQuery.getInt("data")+"");
					map.put("casetype", executeQuery.getInt("casetype") == 0 ? "民事案件":"刑事案件");
					String reasonSplit = BatchUtils.reasonSplit(idMap.get(executeQuery.getLong("reason")+""));
					map.put("reason", reasonSplit);
					map.put("appellor", executeQuery.getString("appellor"));
					map.put("ccourtid", executeQuery.getInt("ccourtid")+"");
					map.put("courtcities", idMap.get(executeQuery.getLong("courtcities")+""));
					map.put("courtprovinces", idMap.get(executeQuery.getLong("courtprovinces")+""));
					if (!StringUtil.isBlank(court)) {
						if ("最高人民法院".equals(court)) {
							level = "最高";
						} else if (court.contains("高级人民法院")) {
							level = "高级";
						} else if (court.contains("中级人民法院") || court.contains("知识产权法院") || court.contains("海事法院")
								|| "天津铁路运输法院".equals(court) || "福州铁路运输法院".equals(court)) {
							level = "中级";
						} else {
							level = "基层";
						}
					}
					map.put("level", level);
					map.put("doc_id", doc_id);
					ResultSet executeQuery3 = readerStmt.executeQuery("select * from docsectionandrule where documentsid = '" + doc_id + "' ORDER BY id DESC");
					while (executeQuery3.next()) {
						ruleid = executeQuery3.getInt("ruleid");
						map.put("name", executeQuery3.getString("title"));
						map.put("title", executeQuery3.getString("title"));
						map.put("sec_" + ruleid, "true");
						map.put("sec_" + ruleid + "_text", executeQuery3.getString("sectiontext"));
						map.put("sec_" + ruleid + "_index",executeQuery3.getInt("start_index") + "," + executeQuery3.getInt("end_index"));
						map.put("sec_index_list",executeQuery3.getString("index_list"));
					}

					ResultSet executeQuery4 = readerStmt.executeQuery("select latitudeid from latitudedoc_key where documentid = '" + doc_id + "' group by latitudeid");
					while (executeQuery4.next()) {
						map.put("vec_" + executeQuery4.getInt("latitudeid"), "true");
					}
//					uuid = UUID.randomUUID().toString();
					doc_info = JSONObject.fromObject(map).toString().replace("'", "''").replace("\\", "\\\\");
					if (doc_info.contains("vec_") || doc_info.contains("sec_")) {
//						reasonSplit = BatchUtils.reasonSplit(idMap.get(executeQuery.getLong("reason")+""));
//						insertSql = "insert into t_export_doc(ed_docInfo,ed_docId,ed_typeId,ed_reasonId,ed_reason) values ";
						insertSql += "('" + doc_info.replace("'", "''") + "','" + doc_id + "',"
								+ dictMap.get(reasonSplit) + ","+executeQuery.getLong("reason")+",'"+reasonSplit+"'),";
					}
				}
				insertSql = insertSql.substring(0, insertSql.length() - 1);
				if (!insertSql.endsWith("values")) {
					readerStmt.addBatch(insertSql);
					readerStmt.executeBatch();
					readerConn.commit();
				}
				String updateSQL = "update article_fiter_penal_pjs set type = 6 where doc_id in " + idIn;
				readerStmt.execute(updateSQL);
				readerConn.commit();
				readerStmt.clearBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(readerConn, readerStmt, null);}
	}
}
