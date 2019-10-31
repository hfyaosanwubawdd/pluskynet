package com.pluskynet.batch;

import java.awt.print.Printable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.pluskynet.util.C3P0connsPollUTIL;

/**
* @author HF
* @version 创建时间：2019年4月26日 下午6:24:32
* 类说明  被排除的案由
*/
public class RMReason {
	 List<String> reasonRmResult = new ArrayList<String>();
	public  void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery2 = null;
		try {
			conn = C3P0connsPollUTIL.getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			executeQuery2 = stmt.executeQuery("select reason_rm from a_reason_rm");
			String string;
			while (executeQuery2.next()) {
				reasonRmResult.add(executeQuery2.getString(1));
			}
			for (int i = 0; i < reasonRmResult.size(); i++) {
				stmt.execute("update article_fiter99 set states = 9 where reason = '"+reasonRmResult.get(i)+"'");
				System.out.println(reasonRmResult.get(i));
				conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {C3P0connsPollUTIL.close(conn, stmt, executeQuery2);}
	}
}
