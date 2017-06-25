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
		//FIXME 每一个条件设置，都要考虑到实际的场景
		//查询某一时间段，某一设备，某一传感器的数据集  15 minute的数据   ,可以计算出，每秒的处理是的事务数TPS，每种情况下，执行一条查询10000数据点的sql，每条数据的延迟时间
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
	public void singleSimpleAndAggrMutiPerform(double simple, double aggregate) {
		//从N=1开始，不断加压
		//计算N=n时，需要加多少简单查询，加多少分析查询
		//进行加压测试10s
	}
	@Override
	public void multiStableInsertAndQueryPerform() {
		//固定一个写入的压力，不断添加读取压力,测试混合负载下读取压力(包含简单查询和分析查询)
		startStableInsertLoad(0.2);//施加固定的写入压力
		singleSimpleQueryPerform();//测试简单读取
		singleAggregatePerform();//测试分析读取
		endStableInsertLoad();//结束固定的写入压力   每次调用的都带一个唯一的名字，可以结束该线程
	}
	/**
	 * 结束固定的写入压力
	 */
	private void endStableInsertLoad() {
		
	}
	/**
	 * 
	 * @param ratio 写入压力最大比例
	 * 如 0.2 则压力为最大压力的0.2倍
	 */
	private void startStableInsertLoad(final double ratio) {
		//获取最大压力
		int maxLoad=getMaxInsertLoad();//TODO 
		BaseDao dao = DaoFactory.getDao("influxdb");//FIXME influxdb
		//施加压力并记录   FIXME 最好设计为与实际类似的场景，ps,但是具体实际场景是未知的，先做出来再说吧
		int loadLine=(int) (ratio*maxLoad);
		while(true){//每秒钟插入 maxLoad*ratio条记录
			List<BenchmarkPoint> points=new ArrayList<BenchmarkPoint>();//FIXME 生成loadLine条points
			long startTime = System.currentTimeMillis();
			dao.insertMultiPoints(points);//插入数据
			long endTime = System.currentTimeMillis();
			long costTime=endTime-startTime;
			if(costTime<1000){
				try {
					Thread.currentThread().sleep(1000-costTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//FIXME 在性能调用该方法的性能开始下降是否停止该线程  加一个全局变量，判断是否已经调用该方法的测试性能的线程是否已经结束
		}
	}
	private int getMaxInsertLoad() {
		return 0;
	}
	@Override
	public void multiStableQueryAndInsertPerform() {
		//固定一个读取的压力，不断添加写入压力，测试混合负载下写入性能
		startStableQueryLoad(0.2,99,1);
		insertPerform();
		endStableQueryLoad(0.2);
	}
	/**
	 * 开始稳定的查询负载
	 * 
	 * 在数据加压程序运行过程中，普通查询:分析查询的比例是一定的，后期参数进行优化
	 * @param ratio
	 * @param simple
	 * @param aggregate
	 * TODO 与前面的混合加压测试方法类似，做完后，设法将方法合并，此次先完成功能，功能完成后进行优化
	 */
	private void startStableQueryLoad(double ratio,double simple,double aggregate) {
		int  maxLoad=getMaxQueryLoad(simple,aggregate);
		BaseDao dao = DaoFactory.getDao("influxdb");//FIXME influxdb
	}
	private int getMaxQueryLoad(double simple, double aggregate) {
		return 0;
	}
	private void endStableQueryLoad(double d) {
		
	}
}
