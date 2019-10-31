package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
* @author HF
* @version 创建时间：2019年7月2日 上午10:25:07
* 类说明   创建文书固定词汇字典表数据  data为article2003_decode  中间的年份    state为article2003_decode.state
*/
public class CreateDictData {

	public void createData(int data,int state) {
		Connection readerConn = null;
		Statement readerStmt = null;
		Statement readerStmt1 = null;
		ResultSet rs = null;
		try {
			readerConn = C3P0connsPollUTIL.getConnection();
			readerConn.setAutoCommit(false);
			readerStmt = readerConn.createStatement();
			readerStmt1 = readerConn.createStatement();
			String idIn = "";
			String insertSql = "";
			String updateSql = "";
			String decodeData = "";
			String doctype = "";
			String trialRound = "";
			String reason = "";
			String court = "";
			String courtcities = "";
			String courtprovinces = "";
			while (true) {
				rs = readerStmt.executeQuery("select id,decode_data from article"+data+"_decode where state = "+state+" limit 1000");	
				rs.last();
				if (rs.getRow() == 0) {
					return;
				}
				rs.beforeFirst();
				idIn = "(";
				while(rs.next()) {
					insertSql = "insert into article_dict(dict_name) values ";
					idIn += rs.getLong("id")+",";
					decodeData = rs.getString("decode_data");
					JSONObject jsonObject = new JSONObject().fromObject(decodeData);
					JSONObject jsonObject2 = new JSONObject().fromObject(jsonObject.getString("dirData"));
					JSONObject jsonObject3 = new JSONObject().fromObject(jsonObject.getString("htmlData"));
					JSONObject jsonObject4 = new JSONObject().fromObject(jsonObject.getString("caseinfo"));
					if (jsonObject3.containsKey("Title")) {
						doctype = BatchUtils.getDoctypeByTitle(jsonObject3.getString("Title"));
					}
					if (jsonObject2.containsKey("RelateInfo")) {
						JSONArray jsonArray = new JSONArray().fromObject(jsonObject2.getString("RelateInfo"));
						for (int j = 0; j < jsonArray.size(); j++) {
							JSONObject js = new JSONObject().fromObject(jsonArray.get(j));
							if (js.getString("key").equals("reason")) {
								reason = js.getString("value");
							}
							if (js.getString("key").equals("court")) {
								court = js.getString("value");
							}
							if (js.getString("key").equals("trialRound")) {
								trialRound = js.getString("value");//审判程序
							}
						}
					}
					for (int j = 0; j < jsonObject4.size(); j++) {
						courtcities = jsonObject4.getString("法院地市").toString();
						courtprovinces = jsonObject4.getString("法院省份").toString();
					}
					insertSql += "('"+reason+"'),('"+doctype+"'),('"+court+"'),('"+trialRound+"'),('"+courtcities+"'),('"+courtprovinces+"') ON DUPLICATE KEY UPDATE dict_name=values(dict_name)";	
					readerStmt.addBatch(insertSql);
				}
				idIn = idIn.substring(0,idIn.length()-1)+")";
				System.out.println(insertSql);
				updateSql = "update article"+data+"_decode set state = 2 where id in"+idIn;
				readerStmt1.executeUpdate(updateSql);
				readerStmt.executeBatch();
				readerConn.commit();
				readerStmt.clearBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(readerConn, readerStmt, rs);}
	}
}
