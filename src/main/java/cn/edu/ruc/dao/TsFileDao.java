package cn.edu.ruc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import cn.edu.ruc.model.BenchmarkPoint;

public class TsFileDao implements BaseDao {
	public static void main(String[] args) {
		BaseDao dao=new TsFileDao();
		dao.insertMultiPoints(null);
	}

	@Override
	public long insertMultiPoints(List<BenchmarkPoint> points) {
		Connection connection = null;
		Statement statement = null;
		  try {
		    Class.forName("com.corp.tsfile.jdbc.TsfileDriver");
		    connection = DriverManager.getConnection("jdbc:tsfile://127.0.0.1:6667/", "root", "root");
		    statement = connection.createStatement();
		    boolean hasResultSet = statement.execute("select s1 from root.excavator.Beijing.d1");
		        if (hasResultSet) {
		            ResultSet res = statement.getResultSet();
		      while (res.next()) {
		        System.out.println(res.getString("Timestamp") + " | " + res.getString("s1"));
		      }
		        }
		  } catch (Exception e) {
		  } finally {
		    if(statement != null){
		      try {
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		    }
		    if(connection != null){
		      try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		    }
		  }
		return 0;
	}

	@Override
	public boolean deleteAllPoints() {
		return false;
	}

	@Override
	public List<Object> selectPointsByTime(Date beginTime, Date endTime,
			String device, String sensor) {
		return null;
	}

	@Override
	public Object selectMaxByTimeAndDevice(String sqlId, Date beginTime, Date endTime, String device,
			List<String> sensors) {
		// TODO Auto-generated method stub
		return null;
	}

}

