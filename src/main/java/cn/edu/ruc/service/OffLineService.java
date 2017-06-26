package cn.edu.ruc.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.edu.ruc.conf.base.device.Tag;
import cn.edu.ruc.conf.init.InitManager;
import cn.edu.ruc.conf.sys.Database;
import cn.edu.ruc.conf.sys.Device;
import cn.edu.ruc.conf.sys.OfflineConfig;
import cn.edu.ruc.conf.sys.Sensor;
import cn.edu.ruc.function.Function;
import cn.edu.ruc.utils.DateUtil;

import com.ruc.BigDataGenerateMain;
import com.ruc.CommonUtils;

public class OffLineService implements BaseOffLineService {
	private static long count=0;
	private static ExecutorService pool;
	@Override
	public boolean generateData() {
		OfflineConfig offLineConfig = InitManager.getOfflineConfig();
		String path = getClass().getClassLoader().getResource("").getPath();
		String baseConfPath=path+"/data";
		return generateData(baseConfPath);
	}
	@Override
	public boolean generateData(String path) {
		try {
			long startTime=System.currentTimeMillis();
			OfflineConfig config = InitManager.getOfflineConfig();
			List<Database> dbs = config.getDatabases();
			initThreadPool(dbs);
			for(int dbseq=0;dbseq<dbs.size();dbseq++){
				Database db = dbs.get(dbseq);
				String type = db.getType();
				if("influxdb".equals(type)){//FIXME influxdb
					generateInfluxdbData(path,db);
				}
				if("opentsdb".equals(type)){//FIXME opentsdb
					generateOpentsdbData(path,db);
				}
				if("tsfile".equals(type)){//FIXME tsfile
					generateTsfileData(path,db);
				}
				if("mysql".equals(type)){//FIXME tsfile
					generateMysqlData(path,db);
				}
			}
			pool.shutdown();
			while(true){
				if(pool.isTerminated()){
					long end=System.currentTimeMillis();
					long costMs=end-startTime;
					System.out.println("共消耗时间["+costMs+"]ms");
					System.out.println("速率["+(count*1000.0/costMs)+" points/s]");
					break;
				}
				try {
					Thread.currentThread().sleep(200L);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private void initThreadPool(List<Database> dbs) {
		int sum=0;
		for(Database db:dbs){
			List<Device> devices = db.getDevices();
			sum+=devices.size();
		}
		pool=Executors.newFixedThreadPool(sum+1);
	}
	/**
	 * 生成influxdb的数据
	 * FIXME influxdb 保存的数据表写死，所有的设备都在一张表中，方便聚合查询 ,设备名用 device区分
	 * @param confUrl
	 */
	private void generateInfluxdbData(final String path,Database db) {
		List<Device> devices = db.getDevices();
		for(int deviceSeq=0;deviceSeq<devices.size();deviceSeq++){
			final Device device = devices.get(deviceSeq);
			pool.execute(new Runnable() {
				@Override
				public void run() {
					String deviceName = device.getName();
					File dir=new File(path);
					if(!dir.exists()){
						dir.mkdir();
					}
					File file = new File(path+"/"+db.getType()+"_"+deviceName+"_"+System.currentTimeMillis());
					if (file.exists()){
						file.delete();
					}
					String measurements="point";
					try {
						FileWriter fw = new FileWriter(file,true);
						StringBuilder sc=new StringBuilder();
						sc.append("# DDL");
						sc.append("\r\n");
						sc.append("CREATE DATABASE ruc_benchmark");
						sc.append("\r\n");
						sc.append("# DML");
						sc.append("\r\n");
						sc.append("# CONTEXT-DATABASE: ruc_benchmark");
						sc.append("\r\n\r\n\r\n\r\n\r\n");
						fw.write(sc.toString());
						sc.setLength(0);
						sc.append(measurements);
						sc.append(",");
						sc.append("device=");
						sc.append(deviceName);
						List<Tag> nameTags = device.getNameTags();
						for(Tag tag:nameTags){
							String key = tag.getKey();
							String value=tag.getValue();
							sc.append(",");
							sc.append(key);
							sc.append("=");
							sc.append(value);
						}
						String measumentInfo=sc.toString();
						sc.setLength(0);
						List<Sensor> sensors = device.getSensors();
						for(Sensor sensor:sensors){
							sc=new StringBuilder();
							String name = sensor.getName();
							long startTime = sensor.getStartTime().getTime();
							long endTime =sensor.getEndTime().getTime();
							Double max = sensor.getMax();
							Double min = sensor.getMin();
							Long cycle = sensor.getCycle();
							String functionId = sensor.getFuctionRefId();
							Long step = sensor.getStep();
							while(startTime<=endTime){
								sc.append(measumentInfo+" ");
								sc.append(name+"="+Function.getValueByFuntionidAndParam(functionId, max, min, cycle, startTime));
								sc.append(" ");
								sc.append(" "+startTime);
								sc.append("\r\n");
								fw.write(sc.toString());
								sc.setLength(0);
								startTime+=step;
								count++;
								if(count%100000==0){
									System.out.println("has generate ["+count+"]points");
								}
							}
						}
						fw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	private void generateOpentsdbData(String path, Database db) {
		List<Device> devices = db.getDevices();
		for(int deviceSeq=0;deviceSeq<devices.size();deviceSeq++){
			final Device device = devices.get(deviceSeq);
			pool.execute(new Runnable() {
				@Override
				public void run() {
					String measurements = device.getName();
					File dir=new File(path);
					if(!dir.exists()){
						dir.mkdir();
					}
					File file = new File(path+"/"+db.getType()+"_"+measurements+"_"+System.currentTimeMillis());
					if (file.exists()){
						file.delete();
					}
					try {
						FileWriter fw = new FileWriter(file,true);
						StringBuilder sc=new StringBuilder();
						List<Tag> nameTags = device.getNameTags();
						for(Tag tag:nameTags){
							String value=tag.getValue();
							sc.append(value);
							sc.append(".");
						}
						sc.append(measurements);
						String measumentInfo=sc.toString();
						sc.setLength(0);
						List<Sensor> sensors = device.getSensors();
						for(Sensor sensor:sensors){
							sc=new StringBuilder();
							long startTime = sensor.getStartTime().getTime();
							long endTime =sensor.getEndTime().getTime();
							Double max = sensor.getMax();
							Double min = sensor.getMin();
							Long cycle = sensor.getCycle();
							String functionId = sensor.getFuctionRefId();
							Long step = sensor.getStep();
							while(startTime<=endTime){
								sc.append(measumentInfo);
								sc.append(" ");
								sc.append(startTime);
								sc.append(" ");
								sc.append(Function.getValueByFuntionidAndParam(functionId, max, min, cycle, startTime));
								List<Tag> tags = sensor.getTags();
								for(Tag tag:tags){
									sc.append(" ");
									sc.append(tag.getKey());
									sc.append("=");
									sc.append(tag.getValue());
								}
								sc.append("\r\n");
								fw.write(sc.toString());
								sc.setLength(0);
								startTime+=step;
								count++;
								if(count%100000==0){
									System.out.println("has generate ["+count+"]points");
								}
							}
						}
						fw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	private void generateTsfileData(String path, Database db) {
		List<Device> devices = db.getDevices();
		for(int deviceSeq=0;deviceSeq<devices.size();deviceSeq++){
			final Device device = devices.get(deviceSeq);
			pool.execute(new Runnable() {
				@Override
				public void run() {
					String measurements = device.getName();
					File dir=new File(path);
					if(!dir.exists()){
						dir.mkdir();
					}
					File file = new File(path+"/"+db.getType()+"_"+measurements+"_"+System.currentTimeMillis());
					if (file.exists()){
						file.delete();
					}
					try {
						FileWriter fw = new FileWriter(file,true);
						StringBuilder sc=new StringBuilder();
						List<Tag> nameTags = device.getNameTags();
						for(Tag tag:nameTags){
							String value=tag.getValue();
							sc.append(value);
							sc.append(".");
						}
						sc.append(measurements);
						String measumentInfo=sc.toString();
						sc.setLength(0);
						List<Sensor> sensors = device.getSensors();
						for(Sensor sensor:sensors){
							sc=new StringBuilder();
							long startTime = sensor.getStartTime().getTime();
							long endTime =sensor.getEndTime().getTime();
							String sensorName = sensor.getName();
							Double max = sensor.getMax();
							Double min = sensor.getMin();
							Long cycle = sensor.getCycle();
							String functionId = sensor.getFuctionRefId();
							Long step = sensor.getStep();
							while(startTime<=endTime){
								sc.append(measumentInfo);
								sc.append(",");
								sc.append(startTime);
								sc.append(",");
								sc.append(sensorName);
								sc.append(",");
								sc.append(Function.getValueByFuntionidAndParam(functionId, max, min, cycle, startTime));
								//FIXME tsFile的tag如何表达  a.b.c可以吗
//								List<Tag> tags = sensor.getTags();
//								for(Tag tag:tags){
//									sc.append(" ");
//									sc.append(tag.getKey());
//									sc.append("=");
//									sc.append(tag.getValue());
//								}
								sc.append("\r\n");
								fw.write(sc.toString());
								sc.setLength(0);
								startTime+=step;
								count++;
								if(count%100000==0){
									System.out.println("has generate ["+count+"]points");
								}
							}
						}
						fw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	/**
	 * mysql 数据生成，导入时，首先要创建好数据表
	 * 
	 * 
	 * select date(from_unixtime(time_ms/1000)) ,avg(value)from sensor_value group by date(from_unixtime(time_ms/1000)) 
	 * mysql 查询
	 * @param path
	 * @param db
	 */
	private void generateMysqlData(String path, Database db) {
		List<Device> devices = db.getDevices();
		for(int deviceSeq=0;deviceSeq<devices.size();deviceSeq++){
			final Device device = devices.get(deviceSeq);
			pool.execute(new Runnable() {
				@Override
				public void run() {
					String measurements = device.getName();
					File dir=new File(path);
					if(!dir.exists()){
						dir.mkdir();
					}
					File file = new File(path+"/"+db.getType()+"_"+measurements+"_"+System.currentTimeMillis());
					if (file.exists()){
						file.delete();
					}
					try {
						FileWriter fw = new FileWriter(file,true);
						StringBuilder sc=new StringBuilder();
						List<Tag> nameTags = device.getNameTags();
						for(Tag tag:nameTags){
							String value=tag.getValue();
							sc.append(value);
							sc.append(".");
						}
						sc.append(measurements);
						String measumentInfo=sc.toString();
						sc.setLength(0);
						List<Sensor> sensors = device.getSensors();
						for(Sensor sensor:sensors){
							sc=new StringBuilder();
							long startTime = sensor.getStartTime().getTime();
							long endTime =sensor.getEndTime().getTime();
							String sensorName = sensor.getName();
							Double max = sensor.getMax();
							Double min = sensor.getMin();
							Long cycle = sensor.getCycle();
							String functionId = sensor.getFuctionRefId();
							Long step = sensor.getStep();
							while(startTime<=endTime){
								sc.append(measumentInfo);
//								sc.append(",");
//								sc.append(DateUtil.time2Str(startTime) );
//								sc.append(startTime/1000);
//								sc.append(",");
//								sc.append(startTime%1000);
								sc.append(startTime);
								sc.append(",");
								sc.append(sensorName);
								sc.append(",");
								sc.append(Function.getValueByFuntionidAndParam(functionId, max, min, cycle, startTime));
								//FIXME tsFile的tag如何表达  a.b.c可以吗
//								List<Tag> tags = sensor.getTags();
//								for(Tag tag:tags){
//									sc.append(" ");
//									sc.append(tag.getKey());
//									sc.append("=");
//									sc.append(tag.getValue());
//								}
								sc.append("\r\n");
								fw.write(sc.toString());
								sc.setLength(0);
								startTime+=step;
								count++;
								if(count%100000==0){
									System.out.println("has generate ["+count+"]points");
								}
							}
						}
						fw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
