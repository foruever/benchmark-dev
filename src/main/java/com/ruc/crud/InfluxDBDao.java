package com.ruc.crud;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.BatchPoints.Builder;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import com.ruc.CommonUtils;
import com.ruc.constant.TypeEnum;
import com.ruc.model.Config;
import com.ruc.model.RecordTime;
import com.ruc.model.influxdb.InfluxdbField;
import com.ruc.model.influxdb.InfluxdbRecord;
import com.ruc.model.influxdb.InfluxdbTag;
import com.ruc.model.influxdb.InfluxdbValue;
/**
 * influxdb httpApi操作类
 * @author Sunxg
 *
 */
public class InfluxDBDao {
	private static Logger logger=Logger.getLogger(InfluxDBDao.class);
	public static void main(String[] args) throws Exception {
		Long startTime=System.currentTimeMillis();
//		InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.241.128:8086"); 
		InfluxDB influxDB = InfluxDBFactory.connect("http://influx-api.10step.top"); 
//		String dbName = "aTimeSeries";  
		String dbName = "mydb";  
		influxDB.createDatabase(dbName);  
		BatchPoints batchPoints = BatchPoints  
		                .database(dbName)  
		                .tag("async", "true")  
		                .consistency(ConsistencyLevel.ALL)  
		                .build();  
		Point point1 = Point.measurement("cpu")  
		                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)  
		                    .field("idle", 90L).field("system", 9L)  
		                    .field("system", 1L)  
		                    .build();  
		Point point2 = Point.measurement("disk")  
		                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)  
		                    .field("used", 80L)  
		                    .field("free", 1L)  
		                    .build();  
		batchPoints.point(point1);  
		batchPoints.point(point2);  
		influxDB.write(batchPoints);  
		Query query = new Query("SELECT idle FROM cpu", dbName);  
		QueryResult query2 = influxDB.query(query);  
//		influxDB.deleteDatabase(dbName);
	}
	public static void onlineInsert(Config config){
		final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String serverUrl=config.getServerUrl();
		List<InfluxdbRecord> influxdbs = config.getInfluxdbs();
		if(influxdbs.size()==0){
			return ;
		}
		final InfluxDB influxDB = InfluxDBFactory.connect(serverUrl); 
		for(final InfluxdbRecord record:influxdbs){
			CommonUtils.ONLINE_THREADS.execute(new Runnable() {
				@Override
				public void run() {
//					final InfluxDB influxDB = InfluxDBFactory.connect(serverUrl);
					long endTime=CommonUtils.data2timestamp(record.getEndTime());
					RecordTime time=new RecordTime(endTime,record.getStep(), record.getCacheLine());
					String dbName = record.getDbName();
					String measurement = record.getMeasurement();
					influxDB.createDatabase(dbName);  
					List<InfluxdbTag> tags = record.getTags();
					List<InfluxdbField> fields = record.getFields();
					if(fields==null||fields.size()==0){
						logger.error("measurement:"+measurement+"的field属性为空，请设置");
						return;
					}
					Builder builder = BatchPoints  
							.database(dbName)  
							.consistency(ConsistencyLevel.ALL);
					if(tags!=null&&tags.size()>0){
						for(InfluxdbTag tag:tags){
							builder.tag(tag.getKey(), tag.getValue());
						}
					}
					while(time.getCurrentTime()<=time.getEndTime()){//当当前时间小于结束时间时，继续生成
						Date startDate=new Date(time.getCurrentTime());
						BatchPoints batchPoints = builder.build();
						for(int i=0;i<record.getCacheLine();i++){
							if(time.getCurrentTime()>time.getEndTime()){
								break;
							}
							org.influxdb.dto.Point.Builder pointBuilder = Point.measurement(measurement)  
		                    .time(time.getCurrentTime(), TimeUnit.MILLISECONDS); 
							for(InfluxdbField field:fields){
								InfluxdbValue valueObj = field.getValue();
								String type = valueObj.getType();
								double value = CommonUtils.initBaseRecordInfo(valueObj, time);
								if(TypeEnum.INT.getName().equals(type)){
									pointBuilder.addField(field.getKey(), (long)value);
								}
								if(TypeEnum.FLOAT.getName().equals(type)){
									pointBuilder.addField(field.getKey(),value);
								}
							}
							Point point = pointBuilder.build();
							time.addStep();
							batchPoints.point(point);
						}
						Date endDate=new Date(time.getCurrentTime());
						try {
							if(time.getCurrentTime()-System.currentTimeMillis()>1000){//大于一秒情况下开始睡觉
								Thread.currentThread().sleep(time.getCurrentTime()-System.currentTimeMillis());
							}
							influxDB.write(batchPoints); 
							logger.info(sdf.format(startDate)+" to "+sdf.format(endDate)+"["+measurement+"] data insert success");
						} catch (Exception e) {
							logger.error(sdf.format(startDate)+" to "+sdf.format(endDate)+"["+measurement+"] data insert failed", e);
						}
					}
					logger.warn("dbName["+dbName+"],measurement["+measurement+"]data insert finished");
//					influxDB.close();
				}
			});
		}
			CommonUtils.ONLINE_THREADS.shutdown();
			while(true){
				if(CommonUtils.ONLINE_THREADS.isTerminated()){
					logger.warn("all finished");
					influxDB.close();
					break;
				}
				try {
					Thread.currentThread().sleep(200L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
	}
}

