package cn.edu.ruc.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import cn.edu.ruc.Bootstrap;

/**
 * 数据库中插入数据
 * @author Sunxg
 *
 */
public class BizUtils {
	/**
	 * 通过sql和参数向数据库中插入数据
	 * @param sql
	 * @param params
	 */
	public static void insertBySqlAndParam(String sql,Object[] params){
		Connection conn = ConnectionManager.getConnection();
		QueryRunner runner=new QueryRunner();
		ResultSetHandler<Integer> rsh=new ResultSetHandler<Integer>() {
			@Override
			public Integer handle(ResultSet rs) throws SQLException {
				return 1;
			}
		};
		try {
			runner.insert(conn, sql,rsh,params );
		} catch (Exception e) {
			e.printStackTrace();
		}
		ConnectionManager.closeConn(conn);
	}
	/**
	 * 获取批次id
	 */
	public static int getBatchId(){
		return Bootstrap.BATCH_ID;
	}
}

