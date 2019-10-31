package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONObject;

/**
 * @author HF
 * @version 创建时间：2019年7月2日 下午4:23:46 类说明
 */
public class SupplyLevel {
	public void supplyLevel() {
		Connection wenshuConn = null;
		Statement wenshuStmt = null;
		Statement wenshuStmt1 = null;
		ResultSet wenshuRs = null;
		String insertSql = "";
		String updateSql = "";
		String docId = "";
		String yeardate = "";
		String level = "";
		String docInfo = "";
		String idIn = "";
		JSONObject docInfoJSON = new JSONObject(); 
		try {
			wenshuConn = C3P0connsPollUTIL.getConnection();
			wenshuConn.setAutoCommit(false);
			wenshuStmt = wenshuConn.createStatement();
			wenshuStmt1 = wenshuConn.createStatement();
			while (true) {
				wenshuRs = wenshuStmt.executeQuery("select ed_id,ed_docInfo,ed_docId from t_export_doc_etc where ed_type is null limit 5000");
				if (BatchUtils.isBlank(wenshuRs)) {
					break;
				}
				insertSql = "insert into t_export_level(doc_id,level,yeardate) values";
				idIn = "(";
				while(wenshuRs.next()) {
					docInfoJSON = new JSONObject().fromObject(wenshuRs.getString("ed_docInfo"));
					yeardate = docInfoJSON.getString("yeardate");
					level = BatchUtils.getCourtLevel(docInfoJSON.getString("court"));
					docId = wenshuRs.getString("ed_docId");
					insertSql += "('"+docId+"','"+level+"','"+yeardate+"'),";
					idIn += ""+wenshuRs.getLong("ed_id")+",";
				}
				idIn = idIn.substring(0,idIn.length()-1)+")";
				insertSql = insertSql.substring(0,insertSql.length()-1);
				wenshuStmt.addBatch(insertSql);
				wenshuStmt.executeBatch();
				wenshuStmt1.executeUpdate("update t_export_doc_etc set ed_type = 3 where ed_id in "+idIn);
				wenshuConn.commit();
				wenshuStmt.clearBatch();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(wenshuConn, wenshuStmt, wenshuRs);}
	}
}
