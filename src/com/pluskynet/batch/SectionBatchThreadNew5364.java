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
import com.pluskynet.data.thread.SectionBatchThread;
import com.pluskynet.domain.Articleyl;
import com.pluskynet.domain.Preview;
import com.pluskynet.otherdomain.Otherdocrule;
import com.pluskynet.parsing.Parsing;
import com.pluskynet.rule.DocRule;
import com.pluskynet.rule.DocRuleNew;
import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SectionBatchThreadNew5364 {
	private static final Logger LOGGER = Logger.getLogger(SectionBatchThreadNew5364.class);
	public static void sectionBatchExecuteThread(String data,String spcx) {
		Connection readerConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection wenshuConn = null;
		Statement wenshuStmt = null;
		ResultSet wenshuRs = null;
		
		Statement batchdataStmt = null;
		Statement docsectionandruleStmt = null;
		Statement docidandruleidStmt = null;
		int lastId = 0;
		try {
//			while (true) {
				wenshuConn = C3P0connsPollUTIL.getConnection();
				wenshuConn.setAutoCommit(false);
				wenshuStmt = wenshuConn.createStatement();
				wenshuRs = wenshuStmt.executeQuery("select doc_id from article_fiter_penal_pjs where data = '"+data+"' and spcx = '"+spcx+"' and type =4 limit 100");
				String idIn = "(";
				wenshuRs.last();
				int row2 = wenshuRs.getRow();
				if (0 == row2 ) {
					LOGGER.info(data+" "+spcx+" 无数据");
					return;
				}
				wenshuRs.beforeFirst();
				
				while (wenshuRs.next()) {
					idIn += "'"+wenshuRs.getString("doc_id")+"',";
				}
				idIn = idIn.substring(0,idIn.length()-1)+")";
//				System.out.println(idIn);
				batchdataStmt = wenshuConn.createStatement();
				docsectionandruleStmt = wenshuConn.createStatement();
				docidandruleidStmt = wenshuConn.createStatement();
				
				readerConn = C3P0connsPollUTIL.getConnection();
				pstmt = readerConn.prepareStatement("select * from article"+data+"_decode where doc_id in "+idIn);
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
				String insertIntoDocsectionandrule = "insert into docsectionandrule(ruleid,sectionname,documentsid,title,sectiontext,startword,endword,type,start_index,causename,data) values ";
				while (rs.next()) {
					lastId = rs.getInt("id");
					decodeData = rs.getString("decode_data");
					doc_id = rs.getString("doc_id");
					jsonObject = new JSONObject().fromObject(decodeData);
					jsonObject3 = new JSONObject().fromObject(jsonObject.getString("htmlData"));
					//casename相关start
					if (jsonObject.containsKey("dirData")) {
						reasonJsonObj = new JSONObject().fromObject(jsonObject.getString("dirData"));
						if (reasonJsonObj.containsKey("RelateInfo")) {
							reasonJsonArray = new JSONArray().fromObject(reasonJsonObj.getString("RelateInfo"));
							for (int j = 0; j < reasonJsonArray.size(); j++) {
								js = new JSONObject().fromObject(reasonJsonArray.get(j));
								//判断当前元素的key值  如果是需要的就记录这个变量
								if (js.getString("key").equals("reason")) {
									reason = js.getString("value");
								}
							}
						}
						if (reason.length() > 100) {
							reason = "data too long";
						}
					}//casename相关end

					
					if (jsonObject3.containsKey("Title")) {
						title = jsonObject3.getString("Title");
					}
					
					JSONArray jsonArray = new JSONArray();
					
					List<Preview> previewList = KeywordAppellor.previewMap.get(spcx);
					for (Preview preview : previewList) {
						DocRuleNew docRule = new DocRuleNew();
						jsonArray = jsonArray.fromObject(preview.getRule());
						List<Otherdocrule> list = docRule.ruleFormat(jsonArray);
						if (!jsonObject3.containsKey("Html")) {
							continue;
						}
						String doc = jsonObject3.getString("Html");
						Map<String, String> resultMap = BatchUtils.docIndexList(doc);
						String docIndexList = resultMap.get("indexList");
						String docold = resultMap.get("result");
//						System.out.println(docIndexList);
//						System.out.println(docold);
//						System.out.println(doc);
//						String docold = Parsing.getTextFromHtml(jsonObject3.getString("Html"));
						List<String> intlist = new ArrayList<String>();
						intlist.add(0,"-1");
						intlist.add(1,"-1");
						docRule.doclist(docold,intlist,list,"一审","判决书");
						System.out.println(intlist.toString());
						System.out.println(list.toString());
						int start = Integer.valueOf(intlist.get(0));
						int end = Integer.valueOf(intlist.get(1));
						String ruleId = preview.getCheckId();
						String sectionName = preview.getDocName();
						if (-1 != end) {//拼三个insert into 
							if (start >= end) {
								LOGGER.info(intlist.toString());
								LOGGER.info("doc_id-->"+doc_id+"  ruleId-->"+ ruleId);
							}
						if (start < end) {
							String sectiontext = docold.substring(start, end);
							if (sectiontext.startsWith("。") || sectiontext.startsWith("）")
									|| sectiontext.startsWith(")")) {
								sectiontext = sectiontext.substring(1, sectiontext.length());
							}
							String startWord = intlist.get(2);
							System.out.println(startWord);
							String endWord = intlist.get(3);
							startWord = startWord.replace("'", "''");
							endWord = endWord.replace("'", "''");
							sectiontext = sectiontext.replace("'", "''");
							title = title.replace("'", "''");
							System.out.println(sectiontext);
							insertIntoDocsectionandrule += "(" + ruleId + ",'" + sectionName + "','" + doc_id + "','"
									+ title + "','" + sectiontext + "','" + startWord + "','" + endWord + "',0," + start
									+ ",'" + reason + "','" + data + "'),";
						}
						}
					}
					jsonObject.clear();
					jsonObject3.clear();
					title = "";
				}	    
				System.out.println(insertIntoDocsectionandrule);
				System.out.println(insertIntoDocsectionandrule.length());
				wenshuStmt.executeUpdate("update article_fiter_penal_pjs set type = 5 where doc_id in "+idIn);
				if (insertIntoDocsectionandrule.length() > 137) {
					docsectionandruleStmt.addBatch(insertIntoDocsectionandrule.substring(0,insertIntoDocsectionandrule.length()-1));
					docsectionandruleStmt.executeBatch();
				}
				wenshuConn.commit();
				docsectionandruleStmt.clearBatch();
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			C3P0connsPollUTIL.close(readerConn, pstmt, rs);
			C3P0connsPollUTIL.close(wenshuConn, wenshuStmt, rs);
			C3P0connsPollUTIL.close(null, docsectionandruleStmt, null);
		}
	}
	
	public static void main(String[] args) {
		String[] years = KeywordAppellor.DATA_YEARS;
		KeywordAppellor.initPreviewList("一审");
		for (int j = 0; j < years.length; j++) {
			String data = years[j];
			Thread t = new Thread() {
				@Override
				public void run() {
					SectionBatchThreadNew5364 secThread = new SectionBatchThreadNew5364();
					secThread.sectionBatchExecuteThread(data, "一审");
				}
			};
			t.start();
		}
	}
}
