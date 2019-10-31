package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.pluskynet.dao.PreviewDao;
import com.pluskynet.domain.Articleyl;
import com.pluskynet.domain.Preview;
import com.pluskynet.domain.StatsDoc;
import com.pluskynet.domain.User;
import com.pluskynet.service.LatitudeService;
import com.pluskynet.util.C3P0connsPollUTIL;
import com.pluskynet.util.JDBCPoolUtil;
import net.sf.json.JSONObject;

/**
* @author HF
* @version 创建时间：2019年4月30日 上午11:26:51
* 类说明
*/
public class PreviewBatch {
	private Logger LOGGER = Logger.getLogger(PreviewBatch.class);
	public List<StatsDoc> getPreviewDocList(User user,Preview preview,String checkId,int type, Integer sampleid,Long count,String batchno){
		List<StatsDoc> statsDocList = new ArrayList<StatsDoc>();
		String username = user.getUsername();
		PreviewProgress previewProgress = new PreviewProgress();
		previewProgress.previewProgress(user, checkId, type);
		Map<String, Object> userMap = BatchCounter.COUNTER_MAP.get(username);
		HashMap<String,Object> innerMap =  (HashMap<String, Object>) userMap.get(checkId);
		String suffix = "%";
		double j = count;
		try {
			Connection conn = C3P0connsPollUTIL.getConnection();
			Statement stmt = conn.createStatement();
			Statement stmt1 = conn.createStatement();
			ResultSet executeQuery = stmt1.executeQuery("select data from sample_rand where sample_id =  "+sampleid+" group by data ");
			while (executeQuery.next()) {
				
//				if (previewProgress.checkTimeOut(username, checkId)) {
//					LOGGER.info("段落预览检测超时   user:"+username+" checkId:"+checkId);
//					return null;
//				}
				
				String data = executeQuery.getString("data");
				ResultSet idResult = stmt.executeQuery("select doc_id from sample_rand where data ='"+data+"' and sample_id =  "+sampleid);
				String sql = "select doc_id,decode_data from article"+data+"_decode where doc_id in ('";
				while (idResult.next()) {
					sql += idResult.getString("doc_id")+"','";
				}
				sql = sql.substring(0,sql.length()-2)+")";
				List<StatsDoc> list = executeThread(sql, preview);
				if (null != list && list.size() >= 1) {
					statsDocList.addAll(list);
				}
				double i = statsDocList.size();
				String updateSQL = "update t_view_his set state = '"+Math.round((i/j)*100)+"%' where batchno = '"+batchno+"'";
				LOGGER.info(updateSQL);
				JDBCPoolUtil.executeSql(updateSQL);
				
				LOGGER.info("user:"+username+"data:"+data+" size:"+statsDocList.size());
				int size = statsDocList.size();
				innerMap.put("completeCount",(long) size);
				if ((long)innerMap.get("totalCount") == size) {
					innerMap.put("completeState","完成");
				}
				BatchCounter.COUNTER_MAP.put(username,userMap);
			}
			executeQuery.close();
			stmt.close();
			stmt1.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statsDocList;
	}
	
	public List<StatsDoc> executeThread(String sql,Preview preview) {
		ClassPathXmlApplicationContext resource = new ClassPathXmlApplicationContext("applicationContext.xml");
		PreviewDao previewDao = (PreviewDao) resource.getBean("previewDao");
		try {
			Connection conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			Statement stmt1 = conn.createStatement();
			ResultSet decodeList = stmt1.executeQuery(sql);
			List<Articleyl> listaArticles = new ArrayList<Articleyl>();
			String decodeData;
			JSONObject jsonObject3;
			JSONObject jsonObject;
			String title = "";
			while (decodeList.next()) {
				decodeData = decodeList.getString("decode_data");
				Articleyl articleyl = new Articleyl();
				articleyl.setDocId(decodeList.getString("doc_id"));
				articleyl.setDecodeData(decodeData);
				jsonObject = new JSONObject().fromObject(decodeData);
				jsonObject3 = new JSONObject().fromObject(jsonObject.getString("htmlData"));
				if (jsonObject3.containsKey("Title")) {
					title = jsonObject3.getString("Title");
				}
				articleyl.setTitle(title);
				articleyl.setDecodeData(decodeData);
				listaArticles.add(articleyl);
				jsonObject.clear();
				jsonObject3.clear();
				title = "";
			}
			decodeList.close();
			stmt1.close();
			conn.close();
			return previewDao.getDocList(preview, listaArticles);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String getDecodeDataByDocId(String docId) {
		String decldeData = "";
		try {
			String sql = "";
			Connection conn = C3P0connsPollUTIL.getConnection();
			Statement stmtWenshu = conn.createStatement();
			ResultSet executeQuery = stmtWenshu.executeQuery("select data from sample_rand where doc_id = '"+docId+"'");
			Connection connReader = C3P0connsPollUTIL.getConnection();
			Statement stmtReader = connReader.createStatement();
			String data = null;
			while (executeQuery.next()) {
				data = executeQuery.getString("data");
				sql = "select decode_data from article"+data+"_decode where doc_id = '"+docId+"'";
			}
			ResultSet executeQuery2 = stmtReader.executeQuery(sql);
			while (executeQuery2.next()) {
				decldeData = executeQuery2.getString("decode_data");
			}
			LOGGER.info("文书预览，docid:"+docId+" data:"+data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decldeData;
	}
	private LatitudeService latitudeService;
	public void setLatitudeService(LatitudeService latitudeService) {
		this.latitudeService = latitudeService;
	}

	/**
	 * 维度规则预览
	 */
//	public List<StatsDoc> getDocsectionList(Latitude latitude,User user,Preview preview,String checkId,int type,String batchno){
//		List<Docsectionandrule> docsectionList = new ArrayList<Docsectionandrule>();
//		List<StatsDoc> statsDocList = new ArrayList<StatsDoc>();
//		String username = user.getUsername();
//		PreviewProgress previewProgress = new PreviewProgress();
//		previewProgress.previewProgress(user, checkId, type);
//		Map<String, Object> userMap = BatchCounter.COUNTER_MAP.get(username);
//		HashMap<String,Object> innerMap = (HashMap<String, Object>) userMap.get(checkId);
//		try {
//			Connection conn = C3P0connsPollUTIL.getConnection();
//			Statement stmt = conn.createStatement();
//			Statement stmt1 = conn.createStatement();
//			ResultSet executeQuery = stmt1.executeQuery("select data from sample_rand where belongid =  "+user.getUserid()+" and type = "+type+" group by data ");
//			while (executeQuery.next()) {
//				if (previewProgress.checkTimeOut(username, checkId)) {
//					LOGGER.info("维度预览检测超时   user:"+username+" checkId:"+checkId);
//					return null;
//				}
//				
//				String data = executeQuery.getString("data");
//				ResultSet idResult = stmt.executeQuery("select id from sample_rand where data ='"+data+"' and belongid =  "+user.getUserid() +" and type = "+type);
//				String sql = "select * from docsectionandrule where data = "+data+" id in ('";
//				while (idResult.next()) {
//					sql += idResult.getString("doc_id")+"','";
//				}
//				sql = sql.substring(0,sql.length()-2)+")";
//				List<StatsDoc> list = latitudeService.getDocList(latitude, user, type, docsectionList,batchno);
//				if (null != list && list.size() >= 1) {
//					statsDocList.addAll(list);
//				}
//				LOGGER.info("user:"+username+"data:"+data+" size:"+docsectionList.size());
//				int size = docsectionList.size();
//				innerMap.put("completeCount",(long) size);
//				if ((long)innerMap.get("totalCount") == size) {
//					innerMap.put("completeState","完成");
//				}
//				BatchCounter.COUNTER_MAP.put(username,userMap);
//			}
//			executeQuery.close();
//			stmt.close();
//			stmt1.close();
//			conn.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return statsDocList;
//	}
}
