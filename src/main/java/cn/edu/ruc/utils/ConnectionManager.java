package cn.edu.ruc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 连接管理
 * @author Sunxg
 *
 */
public class ConnectionManager {
	private static String PROP_NAME="db";
	private static ResourceBundle R_B=ResourceBundle.getBundle(PROP_NAME);
	private static String DRIVER_CLASS;
	private static String URL;
	private static String USERNAME;
	private static String PASSWORD;
	private static void initParam() {
		if(DRIVER_CLASS==null&&URL==null&&USERNAME==null&&PASSWORD==null){
			String path = System.getProperty("user.dir");
			File file=new File(path+"/conf/db.properties");
			Properties prop=new Properties();
			try {
				prop.load(new FileInputStream(file));
				DRIVER_CLASS=prop.getProperty("driver");
				URL=prop.getProperty("jdbcUrl");
				USERNAME=prop.getProperty("username");
				PASSWORD=prop.getProperty("password");
				System.out.println(URL+":"+PASSWORD);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取连接
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() {
		initParam();
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

