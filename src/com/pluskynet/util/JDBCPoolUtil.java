package com.pluskynet.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pluskynet.action.PreviewAction;
import com.pluskynet.domain.Viewhis;

/**
 * @author HF
 * 类说明 
 * 这个项目  需要 pojo pojo.hbm serviceinterface serviceimpl daointerface daoimpl   这么多太麻烦   jdbc比较省事
 */
public class JDBCPoolUtil {
	private static final Logger LOGGER = Logger.getLogger(PreviewAction.class);
	public static void executeSql(String sql) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			stmt.execute(sql);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			C3P0connsPollUTIL.close(null, stmt, null);
		}
	}

	/** 根据sql查询  return List<Map<String, Object>> **/
	public static List<Map<String, Object>> selectBySql(String sql) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (BatchUtils.isBlank(rs)) {
				return null;
			}
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (rs.next()) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				for (int j = 1; j <= columnCount; j++) {
					resultMap.put(metaData.getColumnName((j)), rs.getObject(metaData.getColumnName(j)));
				}
				list.add(resultMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			C3P0connsPollUTIL.close(null, stmt, rs);
		}
		return list;
	}
	
	
	public static List<T> selectBySql(String sql,Class<?> clazz) {
		List list = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (BatchUtils.isBlank(rs)) {
				return null;
			}
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (rs.next()) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				for (int j = 1; j <= columnCount; j++) {
					resultMap.put(metaData.getColumnName((j)), rs.getObject(metaData.getColumnName(j)));
				}
				list.add(ClassCastUtil.mapToBean(resultMap,clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			C3P0connsPollUTIL.close(null, stmt, rs);
		}
		return list;
	}
	
	
	public static void main(String[] args) {
		executeSql("delete from sample where id = 999");
	}
	
}
