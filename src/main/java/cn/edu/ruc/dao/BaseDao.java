package cn.edu.ruc.dao;

import java.util.Date;
import java.util.List;

import cn.edu.ruc.model.BenchmarkPoint;

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
//	boolean insertOnlyPoint();
	
	/**
	 * 2,插入某一个设备，某一采集点，多个时刻的值
	 *  @return 返回插入时间(单位为毫秒)  失败为-1
	 */
	/*===================insert end=============*/
	long insertMultiPoints(List<BenchmarkPoint> points);
	
	/*===================simple query start==================*/
	/**
	 * 3,由开始时间戳和结束时间戳查询，对应时间段，指定设备，指定采集点的数据集
	 */
	long selectPointsByTime(Date beginTime,Date endTime,String device,String sensor);
	
	/*===================simple end start==================*/
	
	/*===================aggregation query start==================*/
	
	/**
	 * 4,聚合分析指定设备，指定采集点的，某一时间段的最大值，分组 一个小时，一天，一个星期，一个月
	 */
	long selectMaxByTimeAndDevice(String sqlId,Date beginTime,Date endTime,String device,List<String> sensors);
	
	/*===================aggregation query end==================*/
	
	/*===================update start ==========================*/
	/**
	 * 5,更新指定设备，指定采集点，指定时间的值
	 */
//	boolean updatePointByTime();
	/*===================update end ==========================*/
	
	/*======================delete start==============================*/
	/**
	 * 6,删除指定设备，指定采集点，指定时间段的值
	 */
//	boolean deletePointsByTime();
	/*======================delete end==============================*/
	/**
	 * 7,删除所有数据
	 */
	boolean deleteAllPoints();
}

