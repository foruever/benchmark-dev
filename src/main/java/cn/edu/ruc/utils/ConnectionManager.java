package cn.edu.ruc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

/**
 * 连接管理
 * @author Sunxg
 *
 */
public class ConnectionManager {
	private static String PROP_NAME="db";
	private static ResourceBundle R_B=ResourceBundle.getBundle(PROP_NAME);
	private static String DRIVER_CLASS=R_B.getString("driver");
	private static String URL=R_B.getString("jdbcUrl");
	private static String USERNAME=R_B.getString("username");
	private static String PASSWORD=R_B.getString("password");
	private static void initParam() {
		if(DRIVER_CLASS==null&&URL==null&&USERNAME==null&&PASSWORD==null){
			ResourceBundle bundle = ResourceBundle.getBundle("db");
			DRIVER_CLASS=bundle.getString("driver");
			URL=bundle.getString("jdbcUrl");
			USERNAME=bundle.getString("username");
			PASSWORD=bundle.getString("password");
		}
	}
	/**
	 * 获取连接
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() {
//		initParam();
		Connection conn=null;
		try {
			Class.forName(DRIVER_CLASS);
			conn=DriverManager.getConnection(URL,USERNAME,PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return conn;
	}
	/**
	 * 关闭连接
	 * @param conn
	 */
	public static void closeConn(Connection conn){
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

