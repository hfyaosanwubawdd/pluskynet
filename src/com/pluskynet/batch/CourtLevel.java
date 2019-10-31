package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pluskynet.util.C3P0connsPollUTIL;

import freemarker.template.utility.StringUtil;

/**
* @author HF
* @version 创建时间：2019年5月21日 下午2:28:01
* 类说明  补全法院层级数据
 * @param <V>
*/
public class CourtLevel<V> {
	 String table = "6";
	 int size = 1;
	 String selectSql = "select * from t_export_doc_"+table+" where state is null  limit ";
	public void main(String[] args) {
		for (int i = 0; i <100; i++) {
			final Integer page = i;
			Thread t = new Thread() {
				@Override
				public void run() {
					executeThread(page);
				}
			};
			t.start();
		}
	}
	
	public void executeThread(Integer page) {
		Connection conn = null;
		Statement st = null;
		ResultSet executeQuery = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			st = conn.createStatement();
			executeQuery = st.executeQuery(selectSql+(page*size)+","+size);
			System.out.println(selectSql+(page*size)+","+size);
			executeQuery.last();
			int row = executeQuery.getRow();
			if (row < 1) {
				return;
			}
			executeQuery.beforeFirst();
			
			String docInfoStr = "";
			String court = "";
			String level = "";
			while (executeQuery.next()) {
				String updateSql = "update t_export_doc_"+table+" set state = 2,";
				docInfoStr = executeQuery.getString("ed_docInfo");
				JSONObject docInfoJsonObj = (JSONObject) JSONObject.parse(docInfoStr);
				court = docInfoJsonObj.getString("court");
				if ("最高人民法院".equals(court)) {
					level = "最高";
				}else if (court.contains("高级人民法院")) {
					level = "高级";
				}else if (court.contains("中级人民法院") || court.contains("知识产权法院") || court.contains("海事法院") 
						|| "天津铁路运输法院".equals(court) || "福州铁路运输法院".equals(court)) {
					level = "中级";
				}else {
					level = "基层";
				}
				docInfoJsonObj.put("level", level);
				updateSql += "ed_docInfo = '"+docInfoJsonObj.toString()+"' where ed_id = "+executeQuery.getInt("ed_id");
				
//				System.out.println(updateSql);
				st.addBatch(updateSql);
			}
			st.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			C3P0connsPollUTIL.close(conn, st, executeQuery);
		}
	}
}