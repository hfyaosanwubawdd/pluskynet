package com.pluskynet.util;
/**
* @author HF
* @version 创建时间：2019年8月21日 下午2:40:19
* 类说明
*/

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class DictUtil {

	public static HashMap<String, Integer> str2IntMap = new HashMap<String, Integer>();
	public static HashMap<Integer, String> int2StrMap = new HashMap<Integer, String>();
	
	public static HashMap<Integer, String> docId2NameMap = new HashMap<Integer, String>();
	public static HashMap<String, Integer> sectionName2ruleIdMap = new HashMap<String, Integer>();
	
	public static void init() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			executeQuery = stmt.executeQuery("select * from article_dict");
			while (executeQuery.next()) {
				str2IntMap.put(executeQuery.getString("dict_name"),executeQuery.getInt("id"));
				int2StrMap.put(executeQuery.getInt("id"),executeQuery.getString("dict_name"));
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, executeQuery);}
	}
	
	
	public static void initDocRule() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			executeQuery = stmt.executeQuery("select * from docrule");
			while (executeQuery.next()) {
				docId2NameMap.put(executeQuery.getInt("ruleid"),executeQuery.getString("sectionName"));
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, executeQuery);}
	}
	
	
	public static void initRuleId() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			executeQuery = stmt.executeQuery("SELECT * FROM latitudeaudit WHERE rule 	is not null and rule != '[]' and rule != '' and latitudetype= 0 and casetype = 1 ");
			while (executeQuery.next()) {
				sectionName2ruleIdMap.put(executeQuery.getString("latitudename"),executeQuery.getInt("latitudeid"));
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, executeQuery);}
	}
	
	
	public static void clear() {
		if (!str2IntMap.isEmpty()) {
			str2IntMap.clear();
		}
		if (!int2StrMap.isEmpty()) {
			int2StrMap.clear();
		}
		if (!docId2NameMap.isEmpty()) {
			docId2NameMap.clear();
		}
		if (!sectionName2ruleIdMap.isEmpty()) {
			sectionName2ruleIdMap.clear();
		}
	}
}
