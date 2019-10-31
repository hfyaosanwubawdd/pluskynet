package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONObject;

/**
* @author HF
* @version 创建时间：2019年6月24日 下午1:55:19
* 类说明	demo06项目刑事案由  跑t_export_doc 
*/
public class Demo6TExport {

	public static Map<String,String> causeTableMap = new HashMap<String, String>();
	public static Map<String,String> docAndSectionMap = new HashMap<String, String>();
//	public static Map<String,String> paraCriMap = new HashMap<String, String>();
//	public static List<String> latitudeList = new ArrayList<String>();
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
		Connection wenshuConn = null;
		Statement wenshuStmt = null;
		Statement wenshuStmt2 = null;
		ResultSet wenshuRs = null;
		int ruleid = 0;
		try {
			readerConn = C3P0connsPollUTIL.getConnection();
			readerConn.setAutoCommit(false);
			readerStmt = readerConn.createStatement();
			readerStmt2 = readerConn.createStatement();
			
			wenshuConn = C3P0connsPollUTIL.getConnection();
			wenshuConn.setAutoCommit(false);
			wenshuStmt = wenshuConn.createStatement();
			wenshuStmt2 = wenshuConn.createStatement();
			
			readerRs = readerStmt.executeQuery("select * from cause");
			while (readerRs.next()) {
				causeTableMap.put(readerRs.getString("causename"),readerRs.getString("causetable"));
				docAndSectionMap.put(readerRs.getString("causename"),readerRs.getString("doctable"));
			}
			wenshuRs = wenshuStmt2.executeQuery("select reason from article_fiter_penal group by reason");
			String tableName = "";
			String reason = "";
			String docAndSectionTableName = "";
			while (wenshuRs.next()) {
				String idIn = "(";
				reason = wenshuRs.getString("reason");
				tableName = causeTableMap.get(reason);
				System.out.println(reason+" "+tableName);
				docAndSectionTableName = docAndSectionMap.get(reason);
				
				while(true) {
					ResultSet executeQuery2 = wenshuStmt.executeQuery("select * from article_fiter_penal where title is null  and reason = '"+reason+"' limit 111");
					executeQuery2.last();
					if (executeQuery2.getRow() == 0) {
						System.out.println(" article_fiter_penal 无数据");
						break;
					}
					executeQuery2.beforeFirst();
					 idIn = "(";
					while (executeQuery2.next()) {
						idIn += "'"+executeQuery2.getString("doc_id")+"',";
					}
					idIn = idIn.substring(0,idIn.length()-1)+")";
					System.out.println(idIn);

					insertSql = "insert into t_export_doc(ed_docInfo,ed_docId,ed_uuid) values ";
					String sql = "select * from "+tableName+" where doc_id in "+idIn;
					ResultSet executeQuery = readerStmt2.executeQuery(sql);
					executeQuery.last();
					if (executeQuery.getRow() == 0) {
						wenshuStmt.execute("update article_fiter_penal set title = '444' where doc_id in "+idIn);
						wenshuConn.commit();
						continue;
					}
					executeQuery.beforeFirst();
					while (executeQuery.next()) {
						court = "";
						doc_id = executeQuery.getString("doc_id");
						court = executeQuery.getString("court");
						Map<String,String> map = new HashMap<String, String>();
						map.put("name",executeQuery.getString("casename"));
						map.put("court",court);
						map.put("step",executeQuery.getString("spcx"));
						map.put("action",executeQuery.getString("doctype"));
						map.put("coid",executeQuery.getString("caseno"));
						map.put("date",executeQuery.getString("trialdate"));
						map.put("yeardate",executeQuery.getString("date"));
						map.put("title",executeQuery.getString("title"));
						map.put("casetype",executeQuery.getString("casetype"));
						map.put("reason",executeQuery.getString("reason"));
						map.put("appellor",executeQuery.getString("appellor"));
						map.put("ccourtid",executeQuery.getString("ccourtid"));
						map.put("courtcities",executeQuery.getString("courtcities"));
						map.put("courtprovinces",executeQuery.getString("courtprovinces"));
						if ("最高人民法院".equals(court)) {
							level = "最高";
						}else if (court.contains("高级人民法院")) {
							level = "高级";
						}else if (court.contains("中级人民法院") || court.contains("知识产权法院") || court.contains("海事法院") 
								|| "天津铁路运输法院".equals(court) || "福州铁路运输法院".equals(court)) {
							level = "中级";
						}else {
							level = "基层";
						}
						map.put("level",level);
						map.put("doc_id",doc_id);
						 ResultSet executeQuery3 = readerStmt.executeQuery("select * from "+docAndSectionTableName+" where documentsid = '"+doc_id+"'");
						 while (executeQuery3.next()) {
							 ruleid = executeQuery3.getInt("ruleid");
							 map.put("sec_" + ruleid, "true");
							 map.put("sec_" + ruleid + "_text", executeQuery3.getString("sectiontext"));
							 map.put("sec_" + ruleid + "_index",executeQuery3.getInt("start_index")+","+executeQuery3.getInt("end_index"));
							 map.put("sec_index_list",BatchUtils.docsectionandruleIndexList(executeQuery3.getString("index_list")));
						}
						 
						ResultSet executeQuery4 = readerStmt.executeQuery("select * from latitudedoc_key where documentid = '"+doc_id+"'");
						while (executeQuery4.next()) {
							map.put("vec_" + executeQuery4.getInt("latitudeid"), "true");
						}
						uuid = UUID.randomUUID().toString();
						doc_info = JSONObject.fromObject(map).toString();
						if (doc_info.contains("vec_") || doc_info.contains("sec_")) {
							insertSql += "('"+doc_info.replace("'", "''")+"','"+doc_id+"','"+uuid+"'),";
						}
					}
					insertSql = insertSql.substring(0,insertSql.length()-1);
					if (!insertSql.endsWith("values")) {
						readerStmt.addBatch(insertSql);
						readerStmt.executeBatch();
						readerConn.commit();
					}
					String updateSQL = "update article_fiter_penal set title = '444' where doc_id in "+idIn;
					wenshuStmt.execute(updateSQL);
					wenshuConn.commit();
					readerStmt.clearBatch();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
