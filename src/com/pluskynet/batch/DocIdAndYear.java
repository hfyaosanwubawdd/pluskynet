package com.pluskynet.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pluskynet.util.C3P0connsPollUTIL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DocIdAndYear {
	private final Logger LOGGER = LoggerFactory.getLogger(DocIdAndYear.class);
	 JSONObject js;
	 Integer state;// 用来区分文书 是属于哪一步排除掉的 或者是哪一张我们需要的文书
	 String doc_id = null;
	 String decode_data = null;
	 String doctype;// 文书类型 0民事1刑事
	 String spcx;// 审判程序
	 List<String> reasonRmResult = new ArrayList<String>();
	 List<String> reasonPenalResult = new ArrayList<String>();
	 Long id = 0L;
	 Long decodeid = 0L;
	 Long maxId = 0L;
	 Long endId = 0L;
	 Long decodeMaxId = 0L;

	public void reasonBatch(int data) {
		String reason = null;
		String appellor = "";
		String appellor1 = "";
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);// 设置事务手动提交
			stmt = conn.createStatement();// 获取操作数据库对象
			Statement stmt2 = conn.createStatement();
			while (data <= 2018) {
				System.out.println("从 article_filter 表获取上一次id");
				ResultSet executeQuery5 = stmt.executeQuery("select last from article_i where year = " + data + " order by id desc limit 1");
				executeQuery5.last();
				if (executeQuery5.getRow() == 0) {
					id = 0L;
				}else {
					executeQuery5.beforeFirst();
					while (executeQuery5.next()) {
						id = executeQuery5.getLong("last");
					}
				}
				String sql = "select id,doc_id,decode_data from article" + data + "_decode where id > " + id
						+ " order by id limit 1000";
				ResultSet executeQuery = stmt2.executeQuery(sql);
				executeQuery.last();
				int row = executeQuery.getRow();
				if (row == 0) {
					System.out.println(data + " 无数据");
					data++;
					if (data == 2009) {
						data++;
					}
					continue;
				}
				executeQuery.beforeFirst();
				JSONArray jsonArray = null;
				StringBuilder insertsb = new StringBuilder("insert into article_i(doc_id,year,last,type) values ");
				int type = 0;
				while (executeQuery.next()) {
					type = 0;
					decodeid = executeQuery.getLong("id");
					doc_id = executeQuery.getString("doc_id");
					decode_data = executeQuery.getString("decode_data");
					JSONObject jsonObject = new JSONObject().fromObject(decode_data);
					if (null == jsonObject || "{}".equals(decode_data)) {
						type = 1;
					}
					JSONObject jsonObject2 = new JSONObject().fromObject(jsonObject.getString("dirData"));
					if (null == jsonObject2 || "null".equals(jsonObject.getString("dirData"))) {
						type = 1;
					}
					JSONObject jsonObject3 = new JSONObject().fromObject(jsonObject.getString("htmlData"));
					if (null == jsonObject3 || "null".equals(jsonObject.getString("htmlData"))) {
						type = 1;
					}
					JSONObject jsonObject4 = new JSONObject().fromObject(jsonObject.getString("caseinfo"));
					if (null == jsonObject4 || "null".equals(jsonObject.getString("caseinfo"))) {
						type = 1;
					}
					// 如果包含RelateInfo这个键
					if (jsonObject2.containsKey("RelateInfo")) {
						// 获取RelateInfo 对应的 json串
						jsonArray = new JSONArray().fromObject(jsonObject2.getString("RelateInfo"));
						if(null == jsonArray ||  "null".equals(jsonArray.toString())) {type = 1;}
					}else {
						type = 1;
					}
					insertsb.append("('" + doc_id + "','" + data + "'," + decodeid + "," + type +"),");
				}
				String insertSql = insertsb.toString().substring(0, insertsb.toString().length() - 1);
				stmt.addBatch(insertSql);
				stmt.executeBatch();
				conn.commit();
				stmt.clearBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, null);}
	}
}
