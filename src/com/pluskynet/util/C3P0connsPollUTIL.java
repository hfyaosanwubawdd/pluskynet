package com.pluskynet.util;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.pluskynet.batch.BatchConstant;

/**
 * @author HF
 * @version 创建时间：2019年10月17日 上午9:34:50 类说明
 */
public class C3P0connsPollUTIL {

	private static ComboPooledDataSource ds = new ComboPooledDataSource();
	static {
		try {
			ds.setDriverClass(BatchConstant.DRIVER1);
			ds.setJdbcUrl(BatchConstant.URL1);
			ds.setUser(BatchConstant.USER1);
			ds.setPassword(BatchConstant.PWD1);
			ds.setInitialPoolSize(5);
			ds.setMinPoolSize(5);
			ds.setMaxPoolSize(5000);
			ds.setMaxIdleTime(1800);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
	public static DataSource getDataSource() {
		return ds;
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public static void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (null != rs) {
				rs.close();
			}
			if (null != stmt) {
				stmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
