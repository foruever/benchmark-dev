package cn.edu.ruc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.edu.ruc.conf.init.InitManager;
import cn.edu.ruc.conf.sys.Database;
import cn.edu.ruc.conf.sys.Device;
import cn.edu.ruc.conf.sys.OnlineConfig;
import cn.edu.ruc.conf.sys.Sensor;
import cn.edu.ruc.dao.BaseDao;
import cn.edu.ruc.dao.DaoFactory;
import cn.edu.ruc.function.Function;
import cn.edu.ruc.model.BenchmarkPoint;

public class OnlineService implements BaseOnlineService{
	private static ExecutorService pool;
	@Override
	public void insertPerform() {
		OnlineConfig config = InitManager.getOnlineConfig();
		List<Database> dbs = config.getDatabases();
		for(Database db:dbs){
			String type = db.getType();
			System.out.println("type:"+type);
			List<BenchmarkPoint> points=new ArrayList<BenchmarkPoint>();
			List<Device> devices = db.getDevices();
			for(int i=0;i<devices.size();i++){
				Device device=devices.get(i);
				List<Sensor> sensors = device.getSensors();
				for(Sensor sensor:sensors){
					long startTime = sensor.getStartTime().getTime();
					long endTime =sensor.getEndTime().getTime();
					Double max = sensor.getMax();
					Double min = sensor.getMin();
					Long cycle = sensor.getCycle();
					String functionId = sensor.getFuctionRefId();
					Long step = sensor.getStep();
					while(startTime<=endTime){
						BenchmarkPoint point=new BenchmarkPoint();
						point.setDeviceName(device.getName()+(i+1));
						point.setDeviceTags(device.getNameTags());
						point.setSensorName(sensor.getName());
						point.setSensorTags(sensor.getTags());
						point.setTimestamp(startTime);
						point.setValue(Function.getValueByFuntionidAndParam(functionId, max, min, cycle, startTime));
						points.add(point);
						startTime+=step;
//						System.out.println(point);
					}
				}
				BaseDao dao = DaoFactory.getDao(type);
				long costTime = dao.insertMultiPoints(points);
				dao.deleteAllPoints();
				if(costTime<1000L){
					costTime=1000L;
				}
				System.out.println(points.size()+":"+(points.size()*1000/costTime)+" points/s");
			}
		}
	}
	static int successCount=0;
	@Override
	public void singleSimpleQueryPerform() {
		//查询某一时间段，某一设备，某一传感器的数据集  1 minute的数据   ,可以计算出，每秒的处理是的事务数TPS，每种情况下，执行一条查询10000数据点的sql，每条数据的延迟时间
		//可以考虑，不断加压时候，每段负载的时间加长一段时间，每个线程多持续一段时间，测试结果更科学
		OnlineConfig config = InitManager.getOnlineConfig();
		List<Database> dbs = config.getDatabases();
		for(Database db:dbs){
			String type = db.getType();
			if(!"influxdb".equals(type)){
				continue;
			}
			BaseDao dao = DaoFactory.getDao(type);
			for(int i=50;i<1000;i++){
				successCount=0;
				pool=Executors.newFixedThreadPool(i+1);
				for(int j=0;j<i;j++){
					pool.execute(new Runnable() {
						@Override
						public void run() {
							long sumcost=0;
							for(int k=0;k<10;k++){
								long beginTime=System.currentTimeMillis();
								dao.selectPointsByTime(null, null, null, null);
								long endTime=System.currentTimeMillis();
								sumcost+=(endTime-beginTime);
								if(endTime-beginTime<1000){
									try {
										Thread.currentThread().sleep(1000L-(endTime-beginTime));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
//							System.out.println("cost:"+sumcost/10+"ms");
							if(sumcost<=1000*10){
								successCount++;
							}
						}
					});
				}
				pool.shutdown();
				while(true){
					if(pool.isTerminated()){
						System.out.println("i="+i+" success count="+successCount);
						break;
					}
					try {
						Thread.currentThread().sleep(200L);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		//通过增加客户端数目进行增加压力
	}
	@Override
	public void singleAggregatePerform() {
		//查询某一时间段，某一设备，所有传感器的max,分时间段聚合 1hour,1day,1week,1month
		OnlineConfig config = InitManager.getOnlineConfig();
		List<Database> dbs = config.getDatabases();
		for(Database db:dbs){
			String type = db.getType();
			if(!"influxdb".equals(type)){
				continue;
			}
			BaseDao dao = DaoFactory.getDao(type);
			for(int i=0;i<1000;i++){
				successCount=0;//FIXME 有并发问题，正式的需要修改
				pool=Executors.newFixedThreadPool(i+1);
				for(int j=0;j<i;j++){
					pool.execute(new Runnable() {
						@Override
						public void run() {
							List<String> sensors=new ArrayList<String>();
							sensors.add("s1");
							sensors.add("s2");
							sensors.add("s3");
							long sumcost=0;
							for(int k=0;k<10;k++){
								long beginTime=System.currentTimeMillis();
								dao.selectMaxByTimeAndDevice("10", null,null,"fengche1",sensors);
								long endTime=System.currentTimeMillis();
								sumcost+=(endTime-beginTime);
								if(endTime-beginTime<1000){
									try {
										Thread.currentThread().sleep(1000L-(endTime-beginTime));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							if(sumcost<=1000*10){
								successCount++;
							}
						}
					});
				}
				pool.shutdown();
				while(true){
					if(pool.isTerminated()){
						System.out.println("i="+i+" aggrate success count="+successCount);
						break;
					}
					try {
						Thread.currentThread().sleep(200L);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		//通过增加客户端数目进行增加压力
		
	}
	@Override
	public void multiStableInsertAndQueryPerform() {
		//固定一个写入的压力，不断添加读取压力,测试混合负载下读取压力(包含简单查询和分析查询)
		
	}
	@Override
	public void multiStableQueryAndInsertPerform() {
		//固定一个读取的压力，不断添加写入压力，测试混合负载下写入性能
		
	}
}
