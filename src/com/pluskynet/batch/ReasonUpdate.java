package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pluskynet.util.C3P0connsPollUTIL;


/**
* @author HF
* @version 创建时间：2019年4月26日 下午1:08:55
* 类说明   补充案由id
*/
public class ReasonUpdate {
	private  final Logger LOGGER = LoggerFactory.getLogger(ReasonUpdate.class);
	public  void updatePidR2(int page){
		Map<String,Long> reasonIdMpa = new HashMap<String, Long>();
		Map<String,Long> reasonFIdMpa = new HashMap<String, Long>();
		Map<Long,String> idReasonMap = new HashMap<Long,String>();
		List<Long> ids2 = new ArrayList<Long>();
		try {
			Connection conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
//			Statement stmt2 = conn.createStatement();
//			ResultSet executeQuery = stmt2.executeQuery("select id from cause where fid = 1");
//			while (executeQuery.next()) {
//				ids2.add(executeQuery.getLong("id"));
//			}
			ResultSet executeQuery2 = stmt.executeQuery("select id,fid,causename from cause");
			while (executeQuery2.next()) {
				reasonIdMpa.put(executeQuery2.getString("causename"),executeQuery2.getLong("id"));
				reasonFIdMpa.put(executeQuery2.getString("causename"),executeQuery2.getLong("fid"));
				idReasonMap.put(executeQuery2.getLong("id"),executeQuery2.getString("causename"));
			}
			Long pid0 = null;
			Long pid1 = null;
			Long pid2 = null;
			Long pid3 = null;
//			String sql = "select a.doc_id doc_id,b.reason reason FROM article_fiter_pjs a left join article_fiter_2017 b on a.doc_id = b.doc_id WHERE " + 
//					"a.date is null and b.reason != '' and b.reason is not null and a.doc_id = b.doc_id";
			String sql = "select * from article_fiter_penal where date = 6 ";
			while (true) {
				ResultSet executeQuery3 = stmt.executeQuery(sql+" limit "+(page*3333)+",3333");
				executeQuery3.last();
				int row = executeQuery3.getRow();
				System.out.println("row:"+row);
				if (row == 0 ) {
					break;
				}
				executeQuery3.beforeFirst();
				String reason3;
				while (executeQuery3.next()) {//107459
					StringBuilder sbSql = new StringBuilder("update article_fiter_penal set date = 5,pid_r2=");
					reason3 = executeQuery3.getString("reason");
					String[] split = reason3.split(",");
					if (null == split || split.length < 1) {
						sbSql.append("null");
//						sbSql.append("null , pid_r1 = null ");
					}else {
						pid3 = reasonIdMpa.get(split[0]);//reason的id
						if (null == pid3) {//无此案由记录
							sbSql.append("null");
//							sbSql.append("null , pid_r1 = null ");
						}else {
//							sbSql.append(pid3+",pid_r1=");
							
							pid2 = reasonFIdMpa.get(idReasonMap.get(pid3));
							while (true) {
								if (null == pid2) {
									sbSql.append("null");
									break;
								}else if( 1 == pid2){
									sbSql.append(pid0);
//									sbSql.append("1");
									break;
								}else if ( 801 == pid2) {
									sbSql.append(pid0);
//									sbSql.append("801");
									break;
								}else {
									pid0 = pid2;
									pid2 = reasonFIdMpa.get(idReasonMap.get(pid2));
								}
							}
						}
					}
					sbSql.append(" where doc_id = '"+executeQuery3.getString("doc_id")+"'");
					System.out.println(sbSql.toString());
					stmt.addBatch(sbSql.toString());
				}
				stmt.executeBatch();
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void updatePidR1(int page){
		Map<String,Long> reasonIdMpa = new HashMap<String, Long>();
		Map<String,Long> reasonFIdMpa = new HashMap<String, Long>();
		Map<Long,String> idReasonMap = new HashMap<Long,String>();
		try {
			Connection conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			ResultSet executeQuery2 = stmt.executeQuery("select id,fid,causename from cause");
			while (executeQuery2.next()) {
				reasonIdMpa.put(executeQuery2.getString("causename"),executeQuery2.getLong("id"));
				reasonFIdMpa.put(executeQuery2.getString("causename"),executeQuery2.getLong("fid"));
				idReasonMap.put(executeQuery2.getLong("id"),executeQuery2.getString("causename"));
			}
			Long pid0 = null;
			Long pid1 = null;
			Long pid2 = null;
			Long pid3 = null;
			while (true) {
				ResultSet executeQuery3 = stmt.executeQuery("select id,reason from article_fiter_pjs_7 where states is null  order by id limit "+(page*10000)+",10000");
				executeQuery3.last();
				int row = executeQuery3.getRow();
				System.out.println("row:"+row);
				if (row == 0 ) {
					break;
				}
				executeQuery3.beforeFirst();
				String reason3;
				while (executeQuery3.next()) {//107459
					StringBuilder sbSql = new StringBuilder("update article_fiter_pjs_7 set states = 2,pid_r3=");
					reason3 = executeQuery3.getString("reason");
					String[] split = reason3.split(",");
					if (null == split || split.length < 1) {
//						sbSql.append("null");
						sbSql.append("null , pid_r1 = null ");
					}else {
						pid3 = reasonIdMpa.get(split[0]);//reason的id
						if (null == pid3) {//无此案由记录
//							sbSql.append("null");
							sbSql.append("null , pid_r1 = null ");
						}else {
							sbSql.append(pid3+",pid_r1=");
							
							pid2 = reasonFIdMpa.get(idReasonMap.get(pid3));
							while (true) {
								if (null == pid2) {
									sbSql.append("null");
									break;
								}else if( 1 == pid2){
//									sbSql.append(pid0);
									sbSql.append("1");
									break;
								}else if ( 801 == pid2) {
//									sbSql.append(pid0);
									sbSql.append("801");
									break;
								}else {
									pid0 = pid2;
									pid2 = reasonFIdMpa.get(idReasonMap.get(pid2));
								}
							}
						}
					}
					sbSql.append(" where id ="+executeQuery3.getLong("id"));
					System.out.println(sbSql.toString());
					stmt.addBatch(sbSql.toString());
				}
				stmt.executeBatch();
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public  void updatePidR3(int page,String table){
		Map<String,Long> reasonIdMpa = new HashMap<String, Long>();
		Map<String,Long> reasonFIdMpa = new HashMap<String, Long>();
		Map<Long,String> idReasonMap = new HashMap<Long,String>();
		List<Long> ids2 = new ArrayList<Long>();
		try {
			Connection conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			
			Statement stmt2 = conn.createStatement();
			ResultSet executeQuery = stmt2.executeQuery("select id from cause where fid = 1");
			while (executeQuery.next()) {
				ids2.add(executeQuery.getLong("id"));
			}
			ResultSet executeQuery2 = stmt.executeQuery("select id,fid,causename from cause");
			while (executeQuery2.next()) {
				reasonIdMpa.put(executeQuery2.getString("causename"),executeQuery2.getLong("id"));
				reasonFIdMpa.put(executeQuery2.getString("causename"),executeQuery2.getLong("fid"));
				idReasonMap.put(executeQuery2.getLong("id"),executeQuery2.getString("causename"));
			}
			Long pid0 = null;
			Long pid1 = null;
			Long pid2 = null;
			Long pid3 = null;
//			String sql = "select a.doc_id doc_id,b.reason reason FROM article_fiter_pjs a left join "+table+" b on a.doc_id = b.doc_id WHERE " + 
//					"a.date = '5' and b.reason != '' and b.reason is not null and a.doc_id = b.doc_id";
			String sql = "select * from article_fiter_penal where date is null ";
			while (true) {
				ResultSet executeQuery3 = stmt.executeQuery(sql+" limit "+(page*3333)+",3333");
				executeQuery3.last();
				int row = executeQuery3.getRow();
				System.out.println("row:"+row);
				if (row == 0 ) {
					break;
				}
				executeQuery3.beforeFirst();
				String reason3;
				while (executeQuery3.next()) {//107459
					StringBuilder sbSql = new StringBuilder("update article_fiter_penal set date = 6,pid_r3=");
					reason3 = executeQuery3.getString("reason");
					String[] split = reason3.split(",");
					if (null == split || split.length < 1) {
						sbSql.append("null");
					}else {
						sbSql.append(reasonIdMpa.get(split[0]));
					}
					sbSql.append(" where doc_id = '"+executeQuery3.getString("doc_id")+"'");
					System.out.println(sbSql.toString());
					stmt.addBatch(sbSql.toString());
				}
				stmt.executeBatch();
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
