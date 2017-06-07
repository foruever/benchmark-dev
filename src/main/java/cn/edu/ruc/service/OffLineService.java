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

import com.ruc.BigDataGenerateMain;
import com.ruc.CommonUtils;

public class OffLineService implements BaseOffLineService {
	private static long count=0;
	private static ExecutorService pool;
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
	 * @param confUrl
	 */
	private void generateInfluxdbData(final String path,Database db) {
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
					File file = new File(path+"/"+measurements);
					if (file.exists()){
						file.delete();
					}
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

	@Override
	public boolean generateData() {
		OfflineConfig offLineConfig = InitManager.getOfflineConfig();
		String path = BigDataGenerateMain.class.getClassLoader().getResource("").getPath();
		String baseConfPath=path+"/data";
		return generateData(baseConfPath);
	}
}
