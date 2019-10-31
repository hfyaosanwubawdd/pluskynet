package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pluskynet.domain.Preview;
import com.pluskynet.domain.User;
import com.pluskynet.util.C3P0connsPollUTIL;

/**
 * @author HF
 * @version 创建时间：2019年7月1日 下午2:03:04 
 * 类说明 预览进度
 */
public class PreviewProgress {
	private Logger LOGGER = Logger.getLogger(PreviewProgress.class);
	public void previewProgress(User user, String checkId, int type) {
		String username = user.getUsername();
		Map<String, Object> userMap = BatchCounter.COUNTER_MAP.get(username);
		Map<String, Object> checkMap = new HashMap<String, Object>();
		HashMap<String, Object> innerMap = new HashMap<String, Object>();
		HashMap<String, Object> innerMapCheck = new HashMap<String, Object>();
		Connection conn;
		ResultSet executeQuery2;
		Statement stmt;
		Statement stmt1;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			stmt1 = conn.createStatement();
			executeQuery2 = stmt.executeQuery(
					"select count(1) from sample_rand where belongid = " + user.getUserid() + " and type = " + type);
			Long totalCount = 0L;
			while (executeQuery2.next()) {
				totalCount = executeQuery2.getLong("count(1)");
			}
			innerMap.put("totalCount",totalCount);
			innerMap.put("completeCount",0L);
			innerMap.put("completeState","");
			checkMap.put(checkId,innerMap);
			innerMapCheck.put("check",System.currentTimeMillis());
			checkMap.put(checkId+"check",innerMapCheck);
			BatchCounter.COUNTER_MAP.put(username,checkMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	public boolean checkTimeOut(String userName,String checkId) {
		Map<String, Object> userMap = BatchCounter.COUNTER_MAP.get(userName);
		HashMap<String,Object> innerMap = (HashMap<String, Object>) userMap.get(checkId);
		Map<String, Object> checkMap = (Map<String, Object>) userMap.get(checkId);
		HashMap<String,Object> innerMapCheck =  (HashMap<String, Object>) userMap.get(checkId+"check");
		if (( System.currentTimeMillis() - (Long)innerMapCheck.get("check") ) > 10000) {//超时
			return true;
		}
		return false;
	}
}
