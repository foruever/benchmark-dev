package cn.edu.ruc.dao;

public class DaoFactory {
	private static final BaseDao INFLUX_DAO=new InfluxdbDao();
	private static final BaseDao TSFLIE_DAO=new TsFileDao();
	private static final BaseDao MYSQL_DAO=new MysqlDao();
	private static final BaseDao OPENTSDB_DAO=new OpentsdbDao();
	/**
	 * 获取数据库操作类 
	 */
	public static BaseDao getDao(Integer databaseType){
		
		return null;
	}

	public static BaseDao getDao(String type) {
		if("influxdb".equals(type)){
			return INFLUX_DAO;
		}
		if("opentsdb".equals(type)){
			return OPENTSDB_DAO;
		}
		if("tsfile".equals(type)){
			return TSFLIE_DAO;
		}
		if("mysql".equals(type)){
			return MYSQL_DAO;
		}
		return null;
	}
}

