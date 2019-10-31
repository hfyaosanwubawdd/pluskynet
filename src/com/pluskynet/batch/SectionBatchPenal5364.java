package com.pluskynet.batch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pluskynet.data.KeywordAppellor;
import com.pluskynet.domain.Articleyl;
import com.pluskynet.domain.Docsectionandrule;
import com.pluskynet.domain.Preview;
import com.pluskynet.otherdomain.Otherdocrule;
import com.pluskynet.parsing.Parsing;
import com.pluskynet.rule.DocRule;
import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SectionBatchPenal5364 {
	private static final Logger LOGGER = Logger.getLogger(SectionBatchPenal5364.class);
	public static void docsectionandruleBatch() {
		KeywordAppellor.initPreviewList("一审");
		
		String[] datas = BatchConstant.DATA;
			for (String data : datas) {
				Thread t = new Thread() {
					@Override
					public void run() {
						sectionBatchExecuteThread("一审", data,KeywordAppellor.previewMap.get("一审"));
					}
				};
				t.start();		 
			}
//			sectionBatchExecuteThread("一审", "2016",KeywordAppellor.previewMap.get("一审"));
	}
	public static void sectionBatchExecuteThread(String spcx,String data,List<Preview> previewList) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Statement wenshuStmt = null;
		ResultSet wenshuRs = null;
		Statement batchdataStmt = null;
		Statement docsectionandruleStmt = null;
		Statement docidandruleidStmt = null;
		int lastId = 0;
		try {
			while(true) {
				int index = 1;
				conn = C3P0connsPollUTIL.getConnection();
				conn.setAutoCommit(false);
				wenshuStmt = conn.createStatement();
				wenshuRs = wenshuStmt.executeQuery("select doc_id from article_fiter_penal_pjs where data = '" + data
						+ "' and spcx = '" + spcx + "' and type = 4 limit 1	");
				String idIn = "(";
				if (BatchUtils.isBlank(wenshuRs)) {
					return;
				}

				while (wenshuRs.next()) {
					idIn += "'" + wenshuRs.getString("doc_id") + "',";
				}
				idIn = idIn.substring(0, idIn.length() - 1) + ")";
				batchdataStmt = conn.createStatement();
				docsectionandruleStmt = conn.createStatement();
				docidandruleidStmt = conn.createStatement();

				pstmt = conn.prepareStatement("select * from article" + data + "_decode where doc_id in " + idIn);
				rs = pstmt.executeQuery();
				rs.last();
				int row = rs.getRow();
				rs.beforeFirst();
				String decodeData;
				String title = "";
				String reason = "";
				String doc_id = "";
				JSONObject jsonObject3;
				JSONObject js;
				JSONObject jsonObject;
				JSONObject reasonJsonObj;
				JSONArray reasonJsonArray = null;
				List<Articleyl> listaArticles = new ArrayList<Articleyl>();
				String insertIntoDocsectionandrule = "insert into docsectionandrule(ruleid,sectionname,documentsid,title,sectiontext,startword,endword,type,start_index,causename,data,end_index,index_list) values ";
				while (rs.next()) {
					decodeData = rs.getString("decode_data");
					doc_id = rs.getString("doc_id");
					jsonObject = new JSONObject().fromObject(decodeData);
					jsonObject3 = new JSONObject().fromObject(jsonObject.getString("htmlData"));
					// casename相关start
					if (jsonObject.containsKey("dirData")) {
						reasonJsonObj = new JSONObject().fromObject(jsonObject.getString("dirData"));
						if (reasonJsonObj.containsKey("RelateInfo")) {
							reasonJsonArray = new JSONArray().fromObject(reasonJsonObj.getString("RelateInfo"));
							for (int j = 0; j < reasonJsonArray.size(); j++) {
								js = new JSONObject().fromObject(reasonJsonArray.get(j));
								// 判断当前元素的key值 如果是需要的就记录这个变量
								if (js.getString("key").equals("reason")) {
									reason = js.getString("value");
								}
							}
						}
						if (reason.length() > 100) {
							reason = "data too long";
						}
					} // casename相关end

					if (jsonObject3.containsKey("Title")) {
						title = jsonObject3.getString("Title");
					}

					JSONArray jsonArray = new JSONArray();
					for (Preview preview : previewList) {
						DocRule docRule = new DocRule();
						jsonArray = jsonArray.fromObject(preview.getRule());
						List<Otherdocrule> list = docRule.ruleFormat(jsonArray);
						if (!jsonObject3.containsKey("Html")) {
							continue;
						}
						String doc = jsonObject3.getString("Html");
						Map<String, String> resultMap = BatchUtils.docIndexList(doc);
						String docold = resultMap.get("result");
						List<String> intlist = new ArrayList<String>();
						intlist.add(0, "-1");
						intlist.add(1, "-1");
						docRule.doclist(docold, intlist, list, spcx, "判决书");
						int start = Integer.valueOf(intlist.get(0));
						int end = Integer.valueOf(intlist.get(1));
						String ruleId = preview.getCheckId();
						String sectionName = preview.getDocName();
						String sectiontext = "";
						if (-1 == end) {
							sectiontext  = docold.substring(start);
							end = start + sectiontext.length();
						}else {
							sectiontext = docold.substring(start, end);
						}// 拼三个insert into
//							if (start >= end) {
//								LOGGER.info(intlist.toString());
//								LOGGER.info("doc_id-->" + doc_id + "  ruleId-->" + ruleId);
//							}
								if (sectiontext.startsWith("。") || sectiontext.startsWith("）")
										|| sectiontext.startsWith(")")) {
									sectiontext = sectiontext.substring(1, sectiontext.length());
								}
								String startWord = intlist.get(2);
								String endWord = intlist.get(3);
								if (endWord.length() > 80) {
									endWord = endWord.substring(0, 80);
								}
								startWord = startWord.replace("'", "''");
								endWord = endWord.replace("'", "''");
								sectiontext = sectiontext.replace("'", "''");
								title = title.replace("'", "''");
								String indexList = BatchUtils.docsectionandruleIndexList(resultMap.get("indexList"));
								insertIntoDocsectionandrule += "(" + ruleId + ",'" + sectionName + "','" + doc_id
										+ "','" + title + "','" + sectiontext + "','" + startWord + "','" + endWord
										+ "',0," + start + ",'" + reason + "'," + data + "," + end + ",'" + indexList
										+ "'),";
//						}
					}
					LOGGER.info(data + " "+spcx+" index " + index+" "+doc_id);
					index++;
					jsonObject.clear();
					jsonObject3.clear();
					title = "";
				}

				wenshuStmt.executeUpdate("update article_fiter_penal_pjs set type = 5 where doc_id in " + idIn);
				docsectionandruleStmt.addBatch(insertIntoDocsectionandrule.substring(0, insertIntoDocsectionandrule.length() - 1));
				docsectionandruleStmt.executeBatch();
				conn.commit();
				docsectionandruleStmt.clearBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			C3P0connsPollUTIL.close(conn, pstmt, rs);
		}
	}
	public static void main(String[] args) {
		SectionBatchPenal5364.docsectionandruleBatch();
	}
	
	
	public List<Docsectionandrule> getDocsectionanruleList(){
		List<Docsectionandrule> list = new ArrayList<Docsectionandrule>();
		Connection conn = null;
		Statement pstmt = null;
		ResultSet rs = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			pstmt = conn.createStatement();
			rs = pstmt.executeQuery("");
			while (rs.next()) {
				Docsectionandrule docsectionandrule = new Docsectionandrule();
				docsectionandrule.setId(rs.getInt("id"));
				docsectionandrule.setRuleid(rs.getInt("ruleid"));
				docsectionandrule.setDocumentsid(rs.getString("documentsid"));
				docsectionandrule.setSectionname(rs.getString("sectionname"));
				docsectionandrule.setTitle(rs.getString("title"));
				docsectionandrule.setSectiontext(rs.getString("sectiontext"));
				list.add(docsectionandrule);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
