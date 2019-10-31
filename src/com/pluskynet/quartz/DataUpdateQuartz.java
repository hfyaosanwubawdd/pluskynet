package com.pluskynet.quartz;
import org.hibernate.mapping.Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pluskynet.util.C3P0connsPollUTIL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author HF
* @version 创建时间：2019年8月16日 上午10:07:58
* 类说明
*/
public class DataUpdateQuartz {

	public static void main(String[] args) {
		dataUpdate();
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(DataUpdateQuartz.class);
	public static void dataUpdate() {
		LOGGER.info("定时任务开始");
		Map<String,String>  map = new HashMap<String, String>();
		Connection readerConn = null;
		Statement readerStmt = null;
		ResultSet executeQuery = null;
		ResultSet executeQuery2 = null;
		ResultSet executeQuery3 = null;
		ResultSet executeQuery4 = null;
		ResultSet executeQuery5 = null;
		ResultSet executeQuery6 = null;
		ResultSet executeQuery7 = null;
		try {
			readerConn = C3P0connsPollUTIL.getConnection();
			readerConn.setAutoCommit(false);
			readerStmt = readerConn.createStatement();
			executeQuery = readerStmt.executeQuery("SELECT count(1),casetype  FROM article_fiter_all GROUP BY casetype");
			while (executeQuery.next()) {
				if (0 == executeQuery.getInt("casetype")) {//民事total  已存储
					map.put("allCivillTotal",String.valueOf(executeQuery.getInt("count(1)")));
				}
				if (1 == executeQuery.getInt("casetype")) {//刑事total  已存储
					map.put("allPenalTotal",String.valueOf(executeQuery.getInt("count(1)")));
				}
			}
//			909	决定书
//			960	判决书
//			5138	裁定书
			executeQuery2 = readerStmt.executeQuery("SELECT count(1),doctype,casetype FROM article_fiter_all where doctype in (909,960,5138) GROUP BY doctype,casetype");
			while (executeQuery2.next()) {
				if (909 == executeQuery2.getInt("doctype")) {//决定书total  已存储
					if (0 == executeQuery2.getInt("casetype")) {
						map.put("allResolveCililTotal",String.valueOf(executeQuery2.getInt("count(1)")));
					}else {
						map.put("allResolvePenalTotal",String.valueOf(executeQuery2.getInt("count(1)")));
					}
				}
				if (960 == executeQuery2.getInt("doctype")) {//判决书total  已存储
					if (0 == executeQuery2.getInt("casetype")) {
						map.put("allJudgmentCililTotal",String.valueOf(executeQuery2.getInt("count(1)")));
					}else {
						map.put("allJudgmentPenalTotal",String.valueOf(executeQuery2.getInt("count(1)")));
					}
				}
				if (5138 == executeQuery2.getInt("doctype")) {//裁定书total  已存储
					if (0 == executeQuery2.getInt("casetype")) {
						map.put("allVerdictCililTotal",String.valueOf(executeQuery2.getInt("count(1)")));
					}else {
						map.put("allVerdictPenalTotal",String.valueOf(executeQuery2.getInt("count(1)")));
					}
				}
			}
			//已存储 已处理 中小刑事判决书  一审 二审 再审
			executeQuery3 = readerStmt.executeQuery("SELECT count(1) FROM t_export_doc");
			while (executeQuery3.next()) {//1399376
				String valueOf = String.valueOf(executeQuery3.getInt("count(1)"));
				map.put("allPenalHandle",valueOf);
				map.put("allJudgmentHandle",valueOf);
				
				map.put("smallPenalHandle",valueOf);
				map.put("smallJudgmentHandle",valueOf);
			}
			
			executeQuery4 = readerStmt.executeQuery("SELECT count(1) FROM article_fiter_penal");
			while (executeQuery4.next()) {
				map.put("smallPenalTotal",String.valueOf(executeQuery4.getInt("count(1)")));
			}
			
			executeQuery5 = readerStmt.executeQuery("SELECT count(1) FROM article_filter WHERE reason != '' and reason is not null");
			while (executeQuery5.next()) {
				map.put("smallCivillTotal",String.valueOf(executeQuery5.getInt("count(1)")));
			}
			
			executeQuery7 = readerStmt.executeQuery("SELECT count(1),doctype FROM article_fiter_penal GROUP BY doctype");
			while (executeQuery7.next()) {
				String string = executeQuery7.getString("doctype");
				if ("判决书".equals(string)) {
					map.put("smallJudgmentPenalTotal",String.valueOf(executeQuery7.getInt("count(1)")));
				}
				if ("决定书".equals(string)) {
					map.put("smallResolvePenalTotal",String.valueOf(executeQuery7.getInt("count(1)")));
				}
				if ("裁定书".equals(string)) {
					map.put("smallVerdictPenalTotal",String.valueOf(executeQuery7.getInt("count(1)")));
				}
			}
			
			executeQuery6 = readerStmt.executeQuery("SELECT count(1),doctype FROM article_filter WHERE reason != '' and reason is not null  GROUP BY doctype");
			while (executeQuery6.next()) {
				String string = executeQuery6.getString("doctype");
				if ("判决书".equals(string)) {
					map.put("smallJudgmentCililTotal",String.valueOf(executeQuery6.getInt("count(1)")));
				}
				if ("决定书".equals(string)) {
					map.put("smallResolveCililTotal",String.valueOf(executeQuery6.getInt("count(1)")));
				}
				if ("裁定书".equals(string)) {
					map.put("smallVerdictCililTotal",String.valueOf(executeQuery6.getInt("count(1)")));
				}
			}
			LOGGER.info(map.toString());
			String sql = "insert into t_data_info(part_name,part_type,total,finish,handle,create_time) values "+
						  "('刑事',0,"+map.get("allPenalTotal")+","+map.get("allPenalHandle")+","+map.get("allPenalHandle")+",now()),"+
						  "('民事',0,"+map.get("allCivillTotal")+",3501980,0,now()),"+
						  "('刑事判决书',0,"+map.get("allJudgmentPenalTotal")+","+map.get("allPenalHandle")+","+map.get("allPenalHandle")+",now()),"+
						  "('民事判决书',0,"+map.get("allJudgmentCililTotal")+",3501980,0,now()),"+
						  "('刑事裁定书',0,"+map.get("allVerdictPenalTotal")+",0,0,now()),"+
						  "('民事裁定书',0,"+map.get("allVerdictCililTotal")+",0,0,now()),"+
						  "('刑事决定书',0,"+map.get("allResolvePenalTotal")+",0,0,now()),"+
						  "('民事决定书',0,"+map.get("allResolveCililTotal")+",0,0,now()),"+
						  
						  "('刑事',1,"+map.get("smallPenalTotal")+","+map.get("allPenalHandle")+","+map.get("allPenalHandle")+",now()),"+
						  "('民事',1,"+map.get("smallCivillTotal")+",0,0,now()),"+
						  "('刑事判决书',1,"+map.get("smallJudgmentPenalTotal")+","+map.get("allPenalHandle")+","+map.get("allPenalHandle")+",now()),"+
						  "('民事判决书',1,"+map.get("smallJudgmentCililTotal")+","+3501980+",0,now()),"+
						  "('刑事裁定书',1,"+map.get("smallVerdictPenalTotal")+",0,0,now()),"+
						  "('民事裁定书',1,"+map.get("smallVerdictCililTotal")+",0,0,now()),"+
						  "('刑事决定书',1,"+map.get("smallResolvePenalTotal")+",0,0,now()),"+
						  "('民事决定书',1,"+map.get("smallResolveCililTotal")+",0,0,now())";
			readerStmt.executeUpdate(sql);
			readerConn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				executeQuery7.close();
				executeQuery6.close();
				executeQuery5.close();
				executeQuery4.close();
				executeQuery3.close();
				executeQuery2.close();
				C3P0connsPollUTIL.close(readerConn, readerStmt, executeQuery);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static List<HashMap<String, String>> getDataInfo(){
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Connection readerConn = null;
		Statement readerStmt = null;
		ResultSet executeQuery = null;
		try {
			readerConn = C3P0connsPollUTIL.getConnection();
			readerStmt = readerConn.createStatement();
			executeQuery = readerStmt.executeQuery("SELECT * FROM t_data_info ORDER BY id desc  LIMIT 16");
			while (executeQuery.next()) {
				HashMap<String, String> innerMap = new HashMap<String, String>();
				innerMap.put("partName",executeQuery.getString("part_name"));
				innerMap.put("partType",executeQuery.getInt("part_type") == 0 ? "全部文书":"中小企业文书");
				innerMap.put("total",executeQuery.getInt("total")+"");
				innerMap.put("handle",executeQuery.getInt("handle")+"");
				innerMap.put("finish",executeQuery.getInt("finish")+"");
				list.add(innerMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(readerConn, readerStmt, executeQuery);}
		Collections.reverse(list);
		return list;
	}
}
