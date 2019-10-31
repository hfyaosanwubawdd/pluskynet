package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Component;
import com.pluskynet.domain.Sample;
import com.pluskynet.domain.User;
import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;
import com.pluskynet.util.DictUtil;
import com.pluskynet.util.JDBCPoolUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
* @author HF
* @version 创建时间：2019年4月26日 下午4:02:15
* 类说明
*/
@Component
public class SampleBatch implements Runnable{
	private User user;
	private Sample sample;
	private Integer type;
	
	public SampleBatch() {
		super();
	}
	public SampleBatch(User user, Sample sample, Integer type) {
		super();
		this.user = user;
		this.sample = sample;
		this.type = type;
	}



	private  Logger LOGGER = Logger.getLogger(SampleBatch.class);
	public  void executeThread(Sample sample,User user,Integer type){
		LOGGER.info("新增段落样本处理中... sampleid -->"+sample.getId());
		Integer id = sample.getId();
		JSONArray jsonArray = JSONArray.fromObject(sample.getRule());
		int count = 100;
		String username = user.getUsername();
		Integer userid = user.getUserid();
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			if (null != user.getUserid()) {
				stmt.executeUpdate("DELETE FROM sample_rand WHERE sample_id = "+id);
				LOGGER.info("删除旧的样本");
			}
			
			if (type == 2 || type == 3) {/**已经转移**/}else {
				String tableName = "article_fiter_penal";
				if (type == 0) {
					tableName = "article_fiter_pjs";
				}
				for (int i = 0; i < jsonArray.size(); i++) {
					String  prefixSql = "select doc_id from "+tableName+" where ";
					JSONObject jsonObject = new JSONObject().fromObject(jsonArray.get(i));
					String year = jsonObject.getString("year");
					Integer valueOf = Integer.valueOf(year);
					if (null == valueOf || valueOf >= 2019 || valueOf <= 2002 || valueOf == 2009) {
						LOGGER.info(username+" 年份:"+valueOf);
						continue;
					}
					count = jsonObject.getInt("count");
					String spcx = jsonObject.getString("trialRound");
					
					prefixSql += " data = "+ year +" and ";
					if (null != spcx) {
						prefixSql += " spcx = '"+spcx+"' ";
					}
					
					prefixSql+=" order by rand() limit "+count;
					executeQuery = stmt.executeQuery(prefixSql);
					String insert = "insert into sample_rand(data,doc_id,belonguser,belongid,type,sample_id) values";
					while (executeQuery.next()) {
						insert += "('"+year+"','"+executeQuery.getString("doc_id")+"','"+username+"',"+userid+","+type+","+id+"),";
					}
					insert = insert.substring(0,insert.length()-1);
					LOGGER.info(username+" 随机抽取段落样本   年份:"+year+" count:"+count);
					stmt.addBatch(insert);
					stmt.executeBatch();
					conn.commit();
					stmt.clearBatch();
					executeQuery = null;
				}
			}
			countSample(id, "sample_rand");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			C3P0connsPollUTIL.close(conn, stmt, executeQuery);
		}
	}
	
	public Long getPreviewState(Integer id) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		Long totalCount = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			stmt = conn.createStatement();
			executeQuery = stmt.executeQuery("select count(1) from sample_rand where belongid = " + id);
			while (executeQuery.next()) {
				 totalCount = executeQuery.getLong("count(1)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != executeQuery) {
					executeQuery.close();
				}
				if (null != stmt) {
					stmt.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return totalCount;
	}
	
	
	
	public String getInsertSQL(String reason,String spcx,String data,String ruleid,String belonguser,Integer type,Integer belongid,Integer count) {
		String insertSQL = "insert into sample_rand(doc_id,belonguser,belongid,type,data) values ";
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		try {
			if (DictUtil.str2IntMap.isEmpty()) {
				DictUtil.init();
			}
			
			conn = C3P0connsPollUTIL.getConnection();
			stmt = conn.createStatement();
			
			if (DictUtil.docId2NameMap.isEmpty()) {
				DictUtil.initDocRule();
			}
			if (DictUtil.sectionName2ruleIdMap.isEmpty()) {
				DictUtil.initRuleId();
			}
			
			int id = DictUtil.sectionName2ruleIdMap.get(DictUtil.docId2NameMap.get(Integer.valueOf(ruleid)));
			String sql ="select docsection_id from docsection_sample where rule_id = "+id+" and data = "+data+" and spcx = "+DictUtil.str2IntMap.get(spcx)+" and reason = "+DictUtil.str2IntMap.get(reason) +" order by rand() limit "+count;
			System.out.println(sql);
			executeQuery = stmt.executeQuery(sql);
			while (executeQuery.next()) {
				insertSQL += "('"+executeQuery.getInt("docsection_id")+"','"+belonguser+"',"+belongid+","+type+",'"+data+"'),";
			}
			insertSQL = insertSQL.substring(0,insertSQL.length()-1);
		} catch (Exception e) {e.printStackTrace();}finally {C3P0connsPollUTIL.close(conn, stmt, executeQuery);}
		return insertSQL;
	}
	
	
	
	
	public void latitudeSample(Sample sample) {
		LOGGER.info("新增维度样本处理中...  sampleid -->"+sample.getId());
		JSONObject jsonObject = null;
		String sql = "", year ="";
		String tableName = "";
		String latitudename2 = "";
		Integer sampleId = sample.getId();
		Integer spcxID = 0;
		String rule = sample.getRule();
		if (null != rule && !"[]".equals(rule)) {
			List<String> sqlList = new ArrayList<String>();
			if (DictUtil.str2IntMap.isEmpty() || DictUtil.str2IntMap.size() < 1) {
				DictUtil.init();
			}
			Connection conn = null;
			Statement stmt = null;
			ResultSet executeQuery = null;
			JSONArray jsonArray = JSONArray.fromObject(sample.getRule());
			String count = null;
			try {
				conn = C3P0connsPollUTIL.getConnection();
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				executeQuery = stmt.executeQuery("select * from sample_rand_la where sample_id = "+sampleId);
				if (!BatchUtils.isBlank(executeQuery)) {
					stmt.execute("delete from sample_rand_la where sample_id = "+sampleId);
				}
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject = null;
					latitudename2 = "";
					year ="";
					sql = "";
					try {
						jsonObject = new JSONObject().fromObject(jsonArray.get(i));
						year = jsonObject.getString("year");
						if (StringUtil.isBlank(year)) {
							continue;
						}
						String spcx = jsonObject.getString("trialRound");
						String latitudename = jsonObject.getString("latitudename");
						if (jsonObject.containsKey("latitudename_except")) {
							latitudename2 = jsonObject.getString("latitudename_except");
						}
						count = jsonObject.getString("count");
						String reason = "";
						if (StringUtil.isBlank(jsonObject.getString("reason"))) {
							if (StringUtil.isBlank(jsonObject.getString("causet"))) {
								reason = jsonObject.getString("case");
							}else {
								reason = jsonObject.getString("causet");
							}
						}else {
							reason = jsonObject.getString("reason");
						}
						Integer reasonID = DictUtil.str2IntMap.get(reason);
						if (reasonID == null ) {
							reasonID = 0;
						}
						spcxID = DictUtil.str2IntMap.get(spcx);
						if (StringUtil.isBlank(latitudename2)) {
							sql = "insert into sample_rand_la(section_id,sample_id) SELECT DISTINCT(sectionid),"+sampleId
									+"  FROM latitudedoc_key WHERE ";
							if (!StringUtil.isBlank(latitudename)) {
								sql +=" latitudeid = "+latitudename+" and ";
							}
							sql += " data = "+year+" and reason = "+reasonID+" and spcx = "+spcxID
									+" ORDER BY rand() limit "+count;
							if (sqlList.contains(sql)) {
								continue;
							}
							sqlList.add(sql);
							stmt.addBatch(sql);
						}else {
							
							if (i == 0) {
								tableName = "temp_"+System.currentTimeMillis();
								String createTableSal = "CREATE TABLE `"+tableName+"`  (" + 
										"  `id` int(11) NOT NULL AUTO_INCREMENT," + 
										"  `latitude_id` int(11) DEFAULT NULL," + 
										"  `section_id` int(11) DEFAULT NULL," + 
										"  `data` int(11) DEFAULT NULL," + 
										"  PRIMARY KEY (`id`) USING BTREE" + 
										") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;";
								stmt.execute(createTableSal);
							}

							
							sql = "insert into "+tableName+"(section_id,latitude_id,data) SELECT sectionid,latitudeid,"+year
									+"  FROM latitudedoc_key WHERE  data = "+year+" and reason = "+reasonID+" and spcx = "+spcxID;
							if (!sqlList.contains(sql)) {
								sqlList.add(sql);
								stmt.execute(sql);
								LOGGER.info(sql);
							}
							
							/**排除**/
							if (!sqlList.contains(latitudename2)) {
								sqlList.add(latitudename2);
								String rmSql = "select distinct(section_id) from "+tableName+" where data = "+year+" and latitude_id = "+latitudename2;
								String sectionIdIn = "(";
								ResultSet executeQuery2 = stmt.executeQuery(rmSql);
								if (!BatchUtils.isBlank(executeQuery2)) {
									while (executeQuery2.next()) {
										sectionIdIn += executeQuery2.getInt("section_id")+",";
									}
									sectionIdIn = sectionIdIn.substring(0,sectionIdIn.length()-1)+")";
								}
								if (sectionIdIn.length() > 1) {
									String delete = "delete from "+tableName+" where section_id in"+sectionIdIn;
									LOGGER.info(delete);
									stmt.execute(delete);
								}
							}
							
							/**写入**/
							String insertSql = "insert into sample_rand_la(section_id,sample_id) SELECT DISTINCT(section_id),"+sampleId
									+"  FROM "+tableName+" where data = "+year;
							if (!StringUtil.isBlank(latitudename)) {
								insertSql +=" and latitude_id = "+latitudename;
							}
							insertSql += " ORDER BY rand() limit "+count;
							if (!sqlList.contains(insertSql)) {
								LOGGER.info(insertSql);
								sqlList.add(insertSql);
								stmt.addBatch(insertSql);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
					stmt.executeBatch();
					conn.commit();
					stmt.clearBatch();
					countSample(sampleId,"sample_rand_la");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {C3P0connsPollUTIL.close(conn, stmt, executeQuery);}
		}
		if (!StringUtil.isBlank(tableName)) {
			JDBCPoolUtil.executeSql("drop table "+tableName);
		}
	}
	
	public void countSample(int sampleId,String tableName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			executeQuery = stmt.executeQuery("select count(1) from "+tableName+" where sample_id = "+sampleId);
			int total = 0;
			while (executeQuery.next()) {
				total = executeQuery.getInt("count(1)");
			}
			stmt.execute("update sample set state = '完成,共"+total+"条' where id = "+sampleId);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, executeQuery);}
			
	}
	
	
	
	public boolean getStateByid(int id) {
		String sql = "select * from sample where id = " + id;
		ResultSet executequery = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			executequery = stmt.executeQuery(sql);
			if (BatchUtils.isBlank(executequery)) {
				return false;
			}
			while (executequery.next()) {
				System.out.println(executequery.getString("state"));
				if (!"完成".equals(executequery.getString("state").substring(0,2))) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			C3P0connsPollUTIL.close(conn, stmt, executequery);
		}
		return false;
	}
	@Override
	public void run() {
		if (type ==0 || type ==1) {
			executeThread(sample, user, type);
		}else {
			latitudeSample(sample);
		}
	}
}
