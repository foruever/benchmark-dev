package cn.edu.ruc.dao;
/**
 * 对时序数据库的操作
 * 对时序数据进行增删改查
 * @author Sunxg
 *
 */
public interface BaseDao {
	/*===================insert start=============*/
	/**
	 * 1,插入某一设备，某一采集点，某一时刻的值
	 * @return 插入成功为 true,
	 *         插入失败为false
	 */
	boolean insertOnlyPoint();
	
	/**
	 * 2,插入某一个设备，某一采集点，多个时刻的值
	 */
	/*===================insert end=============*/
	boolean insertMultiPoints();
	
	/*===================simple query start==================*/
	
	
	/*===================simple end start==================*/
	
	/*===================aggregation query start==================*/
	/*===================aggregation query end==================*/
	
	/*===================update start ==========================*/
	/*===================update end ==========================*/
}

