package com.pluskynet.batch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 民事判决书跑批    t_export_xxxxx   需要在ed_docInfo 字段有所有属性
 * @author HF
 */
public class CivilJudgmentThread {

	private final Logger LOGGER = Logger.getLogger(CivilJudgmentThread.class);
	private String idIn = "";
	private String title = "";
	private String court = "";
	private String casetype = "";
	private String reason = "";
	private String trialRound = "";
	private String trialDate = "";
	private String appellor = "";
	private String LegalBase = "";
	private String ccourtid = "";
	private String caseno = "";
	private String courtcities = "";
	private String courtprovinces = "";
	private String casename = "";
	private String doctype = "";
	private String decodeData ="";
	private String doc_id ="";
	private JSONObject docinfo = new JSONObject();
	private JSONObject jsonObject = new JSONObject();
	private JSONObject jsonObject2 = new JSONObject();
	private JSONObject jsonObject3 = new JSONObject();
	private JSONObject jsonObject4 = new JSONObject();
	private JSONArray jsonArray = new JSONArray();
	private int data = 2003;
	public void civilJudgment() {
		Connection readerConn = null;
		Statement readerStmt = null;
		ResultSet readerRs = null;
		Connection wenshuConn = null;
		Statement wenshuStmt = null;
		ResultSet wenshuRs = null;
		try {
			while (data <= 2018) {
				readerConn = C3P0connsPollUTIL.getConnection();
				readerStmt = readerConn.createStatement();
				wenshuConn = C3P0connsPollUTIL.getConnection();
				wenshuConn.setAutoCommit(false);
				wenshuStmt = wenshuConn.createStatement();
				wenshuRs = wenshuStmt.executeQuery("select doc_id from article_fiter_pjs where (date != 7 or date is null) and data = "+data+" limit 2000");
				wenshuRs.last();
				int row2 = wenshuRs.getRow();
				LOGGER.info(data+"  "+row2);
				if (0 == row2 ) {
					LOGGER.info(data+"  无数据");
					data++;
					if (data == 2009) {
						data++;
					}
					continue;
				}
				wenshuRs.beforeFirst();
				idIn = "(";
				while (wenshuRs.next()) {
					idIn+="'"+wenshuRs.getString("doc_id")+"',";
				}
				idIn = idIn.substring(0,idIn.length()-1)+")";
				String sql = "select * from article"+data+"_decode where doc_id in "+idIn;
				readerRs = readerStmt.executeQuery(sql);
				String insertSQL = "insert into t_export_doc(ed_docInfo,ed_reason,ed_docId,ed_uuid) values ";
				while (readerRs.next()) {
					doc_id =  readerRs.getString("doc_id");
					decodeData = readerRs.getString("decode_data");
					getDocInfoByDecodeData();
					insertSQL += "('"+docinfo.toString().replace("'", "''").replace("\\", "\\\\")+"','"+reason+"','"+doc_id+"','"+UUID.randomUUID()+"'),";
				}
				insertSQL = insertSQL.substring(0,insertSQL.length()-1);
				wenshuStmt.execute(insertSQL);
				wenshuStmt.execute("update article_fiter_pjs set date = 7 where doc_id in "+idIn);
				wenshuConn.commit();
				idIn = "(";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			C3P0connsPollUTIL.close(readerConn, readerStmt, readerRs);
			C3P0connsPollUTIL.close(readerConn, wenshuStmt, wenshuRs);
		}
		
	}
	
	
	public void getDocInfoByDecodeData() {
		jsonObject  = new JSONObject().fromObject(decodeData);
		jsonObject2 = new JSONObject().fromObject(jsonObject.getString("dirData"));
		jsonObject3 = new JSONObject().fromObject(jsonObject.getString("htmlData"));
		jsonObject4 = new JSONObject().fromObject(jsonObject.getString("caseinfo"));
		if (jsonObject3.containsKey("Title")) {
			title = jsonObject3.getString("Title");
		}
		if (title.contains("判决书")) {
			doctype = "判决书";
		}else if (title.contains("裁定书")) {
			doctype = "裁定书";
		}else if (title.contains("调解书")) {
			doctype = "调解书";
		}else if (title.contains("决定书")) {
			doctype = "决定书";
		}else if (title.contains("通知书")) {
			doctype = "通知书";
		}else if (title.contains("批复")) {
			doctype = "批复";
		}else if (title.contains("答复")) {
			doctype = "答复";
		}else if (title.contains("函")) {
			doctype = "函";
		}else if (title.contains("令")) {
			doctype = "令";
		}
		if (jsonObject2.containsKey("RelateInfo")) {
			jsonArray = new JSONArray().fromObject(jsonObject2.getString("RelateInfo"));
			for (int j = 0; j < jsonArray.size(); j++) {
				
				JSONObject js = new JSONObject().fromObject(jsonArray.get(j));
				if (js.getString("key").equals("caseType")) {
					casetype = js.getString("value");
				}
				if (js.getString("key").equals("reason")) {
					reason = js.getString("value");
				}
				if (js.getString("key").equals("court")) {
					court = js.getString("value");
				}
				if (js.getString("key").equals("trialRound")) {
					trialRound = js.getString("value");
				}
				if (js.getString("key").equals("trialDate")) {
					trialDate = js.getString("value");
				}
				if (js.getString("key").equals("appellor")) {
					appellor = js.getString("value");
				}
			}
		}
		for (int j = 0; j < jsonObject4.size(); j++) {
			if (jsonObject4.containsKey("法院ID")) {
				ccourtid = jsonObject4.getString("法院ID").toString();
			}
			if (jsonObject4.containsKey("案号")) {
				caseno = jsonObject4.getString("案号").toString();
			}
			if (jsonObject4.containsKey("法院地市")) {
				courtcities = jsonObject4.getString("法院地市").toString();
			}
			if (jsonObject4.containsKey("法院省份")) {
				courtprovinces = jsonObject4.getString("法院省份").toString();
			}
			if (jsonObject4.containsKey("案件名称")) {
				casename = jsonObject4.getString("案件名称").toString();
			}
		}
		docinfo.put("reason", reason);
		docinfo.put("ccourtid", ccourtid);
		if (doctype.length()>500) {
			doctype = "data too lang";
		}
		docinfo.put("action", doctype);
		docinfo.put("appellor", appellor);
		docinfo.put("court", court);
		docinfo.put("casetype", casetype);
		docinfo.put("yeardate", data+"");
		docinfo.put("courtprovinces", courtprovinces);
		docinfo.put("date", trialDate);
		docinfo.put("courtcities", courtcities);
		docinfo.put("coid", caseno);
		docinfo.put("name", title);
		docinfo.put("title", title);
		docinfo.put("docId", doc_id);
		docinfo.put("step", trialRound);
		docinfo.put("level", BatchUtils.getCourtLevel(court));
		if (jsonObject3.containsKey("Html")) {
			docinfo.put("text", jsonObject3.get("Html"));
		}else {
			docinfo.put("text", "");
		}
	}
}

