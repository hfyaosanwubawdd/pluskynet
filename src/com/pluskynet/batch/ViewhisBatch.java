package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.jsoup.helper.StringUtil;

import com.pluskynet.action.PreviewAction;
import com.pluskynet.domain.DocidAndDoc;
import com.pluskynet.domain.Latitude;
import com.pluskynet.domain.PreParent;
import com.pluskynet.domain.Preview;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.domain.Viewhis;
import com.pluskynet.util.BatchUtils;
import com.pluskynet.util.C3P0connsPollUTIL;
import com.pluskynet.util.ClassCastUtil;
import com.pluskynet.util.JDBCPoolUtil;

/**
* @author HF
* @version 创建时间：2019年10月8日 上午10:21:02
* 类说明	 预览历史相关(原项目框架需要太多繁琐的步骤,为了节省开发时间就这么搞吧)
*/
public class ViewhisBatch {
	private final Logger LOGGER = Logger.getLogger(ViewhisBatch.class);
	public Viewhis saveHis(PreParent preParent,boolean isla,int ruleid,String batch_no,Integer sampleid) {
		int id = 0;
		Viewhis vie = new Viewhis();
		Connection conn = null;
		Statement stmt = null;
		ResultSet executequery = null;
		String rule = "";
		String reserved = "";
		String sql1 = "";
		if (isla) {
			sql1 = "select reserved,rule from latitude where latitudeid = "+ruleid;
		}else {
			sql1 =  "select reserved,rule from docrule where ruleid = "+ruleid;
		}
		LOGGER.info(sql1);
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			executequery = stmt.executeQuery(sql1);
			while (executequery.next()) {
				rule = executequery.getString("rule");
				reserved = executequery.getString("reserved");
			}
			String sql = "insert into t_view_his(ruleid,batchno,rule,reserved,sampleid) values ("+ruleid+",'"+batch_no+"','"+rule+"','"+reserved+"',"+sampleid+")";
			LOGGER.info(sql);
			id = stmt.executeUpdate(sql);
			conn.commit();
			ResultSet executeQuery2 = stmt.executeQuery("select * from t_view_his  order by id desc limit 1");
			while (executeQuery2.next()) {
				vie.setId(executeQuery2.getInt("id"));
				vie.setBatchno(executeQuery2.getString("batchno"));
				vie.setReserved(executeQuery2.getString("reserved"));
				vie.setRule(executeQuery2.getString("rule"));
				vie.setSampleid(executeQuery2.getInt("sampleid"));
				vie.setRuleid(executeQuery2.getInt("ruleid"));
				vie.setState(executeQuery2.getString("state"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vie;
	}
	
	public void saveViewhis(List<StatsDoc> list, PreParent preParent,boolean isla,String batch_no,Integer sampleid) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executequery = null;
		int ruleid = 0;
		String rule = "";
		String reserved = "";
		String sql1 = "";
		if (isla) {
			Latitude la = (Latitude) preParent;
			rule = la.getRule();
			sql1 = "select reserved,latitudeid as id from latitude where rule = '"+rule+"'";
		}else {
			Preview pr = (Preview) preParent;
			rule = pr.getRule();
			rule = pr.getRule();
			sql1 =  "select reserved,ruleid as id from docrule where rule = '"+rule+"'";
		}
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String sql = "insert into t_view_doc(batchno,docid,title,stats,num,doc,sampleid) values ";
			for (StatsDoc statsDoc : list) {
				sql += "('"+batch_no+"','"+statsDoc.getDocidAndDoc().getDocid()+"','"+statsDoc.getDocidAndDoc().getTitle()+
						"','"+statsDoc.getStats()+"',"+statsDoc.getNum()+",'"+statsDoc.getDocidAndDoc().getDoc()+"',"+sampleid+"),";
			}
			sql = sql.substring(0,sql.length()-1);
			stmt.addBatch(sql);
			stmt.executeBatch();
			conn.commit();
			stmt.clearBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, executequery);}
	}
	public List<StatsDoc> getDocListByBatchno(String batchno) {
		List<StatsDoc> list = new ArrayList<StatsDoc>();
		ResultSet executequery = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String sql = "select * from t_view_doc where batchno = '"+batchno+"'";
			LOGGER.info(sql);
			executequery = stmt.executeQuery(sql);
			while (executequery.next()) {
				StatsDoc statsDoc = new StatsDoc();
				DocidAndDoc docidAndDoc = new DocidAndDoc();
				docidAndDoc.setDoc(executequery.getString("doc"));
				docidAndDoc.setDocid(executequery.getString("docid"));
				docidAndDoc.setTitle(executequery.getString("title"));
				statsDoc.setDocidAndDoc(docidAndDoc);
				statsDoc.setNum(executequery.getInt("num"));
				statsDoc.setStats(executequery.getString("stats"));
				list.add(statsDoc);
			}
			executequery.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			C3P0connsPollUTIL.close(conn, stmt, executequery);
		}
		return list;
	}
	
	
	public void deleteHisByBatchno(String batchno) {
		JDBCPoolUtil.executeSql("delete from t_view_doc where batchno = '"+batchno+"'");
		JDBCPoolUtil.executeSql("delete from t_view_his where batchno = '"+batchno+"'");
	}
	
	
	
	public List<Viewhis> getViewhisListByRuleid(Integer ruleid) throws Exception{
		List list = new ArrayList<Viewhis>();
		String sql = "select id,batchno,sampleid,ruleid,state from t_view_his where ruleid = "+ruleid+" order by id desc";
		list = JDBCPoolUtil.selectBySql(sql,Viewhis.class);
		return list;
	}
	
	public List<Integer> getSampleids(Integer type,Integer userId){
		List<Integer> list = new ArrayList<Integer>();
		String sql = "select id from sample where type = "+type+" and belongid = "+userId+" and  SUBSTR(state,5,2) != '0条'";
		List<Map<String, Object>> selectBySql = JDBCPoolUtil.selectBySql(sql);
		for (int i = 0; i < selectBySql.size(); i++) {
			list.add((Integer) selectBySql.get(i).get("id"));
		}
		return list;
	}
	
	public Long countByBatchno(Integer sampleid,String suffix) {
		return (Long) JDBCPoolUtil.selectBySql("select count(*) as count from sample_rand"+suffix+" where sample_id = "+sampleid).get(0).get("count");
	}
	
	
	
	public Integer getTypeByRuleid(Integer ruleid) {
		return (int)JDBCPoolUtil.selectBySql("select type from docrule where ruleid = "+ruleid).get(0).get("type");
	}
	public Integer getTypeByLatitudeid(Integer latitudeid) {
		return (int)JDBCPoolUtil.selectBySql("select type from latitude where latitudeid = "+latitudeid).get(0).get("type");
	}
}
