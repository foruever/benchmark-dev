package cn.edu.ruc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import cn.edu.ruc.utils.BizUtils;

public class OnlineService implements BaseOnlineService{
	private static ExecutorService pool;
	@Override
	public void insertPerform() {
		//初始化已经插入上亿条数据了，所以增加完是否删除的意义已经不大
		OnlineConfig config = InitManager.getOnlineConfig();
		List<Database> dbs = config.getDatabases();
		for(Database db:dbs){
			String type = db.getType();
			List<Device> devices = db.getDevices();
			//写入性能测试时候，传感器的开始时间，结束时间不重要，知道测试到数据库到瓶颈位置
			//开始时间和结束时间也有意义，但当前时间不在这个时间范围内，则不生成数据
			for(int i=0;i<devices.size();i++){//FIXME 2017-07-02 目前只设置一个类型的设备，不断增加设备进行增压
				for(int time=0;time<1500;time++){//FIXME 2017-07-02 具体执行多少次，设置被可配置的
					List<BenchmarkPoint> points=new ArrayList<BenchmarkPoint>();
					for(int j=0;j<=time;j++){
						Device device=devices.get(i);
						List<Sensor> sensors = device.getSensors();
						for(Sensor sensor:sensors){
							//原来版本是生成该传感器的所有数据，然后插入数据
							long currentTime=System.currentTimeMillis();
							long startTime = sensor.getStartTime().getTime();
							long endTime =sensor.getEndTime().getTime();
							Long step = sensor.getStep();
							//FIXME 以上这三个参数可进行优化，如果时间不满足这三个参数，则不生成数据
							Double max = sensor.getMax();
							Double min = sensor.getMin();
							Long cycle = sensor.getCycle();
							String functionId = sensor.getFuctionRefId();
							BenchmarkPoint point=new BenchmarkPoint();
							point.setDeviceName(device.getName()+(j+1));
							point.setDeviceTags(device.getNameTags());
							point.setSensorName(sensor.getName());
							point.setSensorTags(sensor.getTags());
							point.setTimestamp(currentTime);
							point.setValue(Function.getValueByFuntionidAndParam(functionId, max, min, cycle, currentTime));
							points.add(point);
//						startTime+=step;
						}
					}
					BaseDao dao = DaoFactory.getDao(type);
					long beginTime=System.currentTimeMillis();
					long costTime = dao.insertMultiPoints(points);
					if(costTime<1000L){
						costTime=1000L;
					}
					String sql="insert into perform_only_write"
							+ "(db_type,batch_id,target_devices_per,"
							+ "target_lines_per,actual_lines_per,operate_time) values(?,?,?,?,?,?)";
					Object[] params={1,BizUtils.getBatchId(),time+1,points.size(),points.size()*1000/costTime,new Timestamp(beginTime)};
					BizUtils.insertBySqlAndParam(sql, params);
					System.out.println(points.size()+":"+(points.size()*1000/costTime)+" points/s");
					try {
						Thread.currentThread().sleep(1000L);//线程休息一秒
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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
			Map<Integer,Integer> successMap=new HashMap<Integer,Integer>();
			for(int i=0;i<1000;i++){
//				successCount=0;
				Integer currentSeq=i;
				successMap.put(currentSeq, 0);
				pool=Executors.newFixedThreadPool(i+1);
				long beginTime=System.currentTimeMillis();
				for(int j=0;j<i;j++){
					pool.execute(new Runnable() {
						@Override
						public void run() {
//							long sumcost=0;
//							for(int k=0;k<10;k++){
//								int costTime = dao.selectPointsByTime(null, null, null, null);
//								sumcost+=costTime;
//								if(costTime<=1000){
//									try {
//										synchronized (successMap) {
//											Integer count = successMap.get(i);
//											count++;
//											successMap.put(i, count);
//										}
//										Thread.currentThread().sleep(1000L-(costTime));
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//							}
//							if(sumcost<=1000*10){
//								successCount++;
//							}
							
							long costTime = dao.selectPointsByTime(null, null, null, null);
							if(costTime<=1000){
								try {
									synchronized (currentSeq) {
										Integer count = successMap.get(currentSeq);
										count++;
										successMap.put(currentSeq, count);
									}
									Thread.currentThread().sleep(1000L-(costTime));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
				}
				pool.shutdown();
				while(true){
					if(pool.isTerminated()){
						System.out.println("i="+i+" success count="+successMap.get(currentSeq));
						String sql="insert perform_only_simple_query(batch_id,db_type,operate_time,target_times_per,actual_times_per) values(?,?,?,?,?)";
						Object[] params={BizUtils.getBatchId(),1,new Timestamp(beginTime),currentSeq,successMap.get(currentSeq)};
						BizUtils.insertBySqlAndParam(sql,params);
						try {
							Thread.currentThread().sleep(1000L);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
			Map<Integer,Integer> successMap=new HashMap<Integer,Integer>();
			for(int i=0;i<1000;i++){
				Integer currentSeq=i;
				successMap.put(currentSeq, 0);
				pool=Executors.newFixedThreadPool(i+1);
				long beginTime=System.currentTimeMillis();
				for(int j=0;j<i;j++){
					pool.execute(new Runnable() {
						@Override
						public void run() {
//							List<String> sensors=new ArrayList<String>();
//							sensors.add("s1");
//							sensors.add("s10");
//							sensors.add("s100");
//							long sumcost=0;
//							for(int k=0;k<10;k++){
//								long costTime = dao.selectMaxByTimeAndDevice("10", null,null,"point",sensors);
//								sumcost+=(costTime);
//								if(costTime<1000){
//									try {
//										Thread.currentThread().sleep(1000L-(costTime));
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//							}
//							if(sumcost<=1000*10){
//								successCount++;
//							}
							List<String> sensors=new ArrayList<String>();
							for(int tmp=1;tmp<=500;tmp++){
								sensors.add("s"+tmp);
							}
							long costTime = dao.selectMaxByTimeAndDevice("10", null,null,"point",sensors);
							if(costTime<=1000){
								try {
									synchronized (currentSeq) {
										Integer count = successMap.get(currentSeq);
										count++;
										successMap.put(currentSeq, count);
									}
									Thread.currentThread().sleep(1000L-(costTime));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
				}
				pool.shutdown();
				while(true){
					if(pool.isTerminated()){
						System.out.println("i="+i+" success count="+successMap.get(currentSeq));
						String sql="insert perform_only_aggre_query(batch_id,db_type,operate_time,target_times_per,actual_times_per) values(?,?,?,?,?)";
						Object[] params={BizUtils.getBatchId(),1,new Timestamp(beginTime),currentSeq,successMap.get(currentSeq)};
						BizUtils.insertBySqlAndParam(sql,params);
						try {
							Thread.currentThread().sleep(1000L);
						} catch (Exception e) {
							e.printStackTrace();
						}
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
		Map<String,Boolean> flagMap=new HashMap<String,Boolean>();
		startStableInsertLoad(flagMap,0.2);//施加固定的写入压力
		singleSimpleQueryPerform();//测试简单读取
		singleAggregatePerform();//测试分析读取
		endStableInsertLoad(flagMap);//结束固定的写入压力   每次调用的都带一个唯一的名字，可以结束该线程
	}
	/**
	 * 结束固定的写入压力
	 */
	private void endStableInsertLoad(Map<String,Boolean> flagMap) {
		flagMap.put("flag",false);
	}
	/**
	 * 
	 * @param ratio 写入压力最大比例
	 * 如 0.2 则压力为最大压力的0.2倍
	 */
	private void startStableInsertLoad(Map<String,Boolean> flagMap,final double ratio) {
		flagMap.put("flag", true);
		//获取最大压力
		int maxLoad=getMaxInsertLoad();//TODO 
		//施加压力并记录   FIXME 最好设计为与实际类似的场景，ps,但是具体实际场景是未知的，先做出来再说吧
		int loadLine=(int) (ratio*maxLoad);
		int loadDeviceCount=20;//FIXME  后期从数据库中分析出来结果
		
		Thread thread=new Thread(new  Runnable() {
			public void run() {
				while(true){//每秒钟插入 maxLoad*ratio条记录
					long startTime = System.currentTimeMillis();
					Boolean flag = flagMap.get("flag");
					if(!flag){
						break;
					}
					OnlineConfig config = InitManager.getOnlineConfig();
					List<Database> dbs = config.getDatabases();
					for(Database db:dbs){
						String type = db.getType();
						List<Device> devices = db.getDevices();
						//写入性能测试时候，传感器的开始时间，结束时间不重要，知道测试到数据库到瓶颈位置
						//开始时间和结束时间也有意义，但当前时间不在这个时间范围内，则不生成数据
						for(int i=0;i<devices.size();i++){
							List<BenchmarkPoint> points=new ArrayList<BenchmarkPoint>();//FIXME 生成loadLine条points 根据loadDeviceCount生成
							for(int j=0;j<loadDeviceCount;j++){
								Device device=devices.get(i);
								List<Sensor> sensors = device.getSensors();
								for(Sensor sensor:sensors){
									//原来版本是生成该传感器的所有数据，然后插入数据
									long currentTime=System.currentTimeMillis();
									//FIXME 以上这三个参数可进行优化，如果时间不满足这三个参数，则不生成数据
									Double max = sensor.getMax();
									Double min = sensor.getMin();
									Long cycle = sensor.getCycle();
									String functionId = sensor.getFuctionRefId();
									BenchmarkPoint point=new BenchmarkPoint();
									point.setDeviceName(device.getName()+(j+1));
									point.setDeviceTags(device.getNameTags());
									point.setSensorName(sensor.getName());
									point.setSensorTags(sensor.getTags());
									point.setTimestamp(currentTime);
									point.setValue(Function.getValueByFuntionidAndParam(functionId, max, min, cycle, currentTime));
									points.add(point);
								}
							}
							BaseDao dao = DaoFactory.getDao(type);
							long beginTime=System.currentTimeMillis();
							long costTime = dao.insertMultiPoints(points);
							System.out.println(costTime);
							if(costTime<1000L){
								costTime=1000L;
							}
							String sql="insert into load_stable_insert"
									+ "(db_type,batch_id,target_devices_per,"
									+ "target_lines_per,actual_lines_per,operate_time) values(?,?,?,?,?,?)";
							Object[] params={1,BizUtils.getBatchId(),loadDeviceCount,points.size(),points.size()*1000/costTime,new Timestamp(beginTime)};
							BizUtils.insertBySqlAndParam(sql, params);
						}
					}
					long endTime = System.currentTimeMillis();
					long functionCostTime=endTime-startTime;
					if(functionCostTime<1000L){
						try {
							Thread.sleep(1000L-functionCostTime);//线程休息，保证每秒钟执行一次插入语句
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		thread.start();
			
	}
	private int getMaxInsertLoad() {
		return 0;
	}
	@Override
	public void multiStableQueryAndInsertPerform() {
		Map<String,Boolean> flagMap=new HashMap<String,Boolean>();//FIXME flagMap可优化
		//固定一个读取的压力，不断添加写入压力，测试混合负载下写入性能
		startStableQueryLoad(flagMap,0.2,99,1);
		insertPerform();
		endStableQueryLoad(flagMap);
	}
	/**
	 * 开始稳定的查询负载
	 * 
	 * 在数据加压程序运行过程中，普通查询:分析查询的比例是一定的，后期参数进行优化
	 * 
	 * simple,aggregate 两者必须大于等于0，且两者不可以都为0
	 * @param ratio
	 * @param simple  
	 * @param aggregate
	 * TODO 与前面的混合加压测试方法类似，做完后，设法将方法合并，此次先完成功能，功能完成后进行优化
	 */
	private void startStableQueryLoad(Map<String,Boolean> flagMap,double ratio,double simple,double aggregate) {
		flagMap.put("flag", true);
		int  maxLoad=getMaxQueryLoad(simple,aggregate);
//		BaseDao dao = DaoFactory.getDao("influxdb");//FIXME influxdb
		if(simple<0||aggregate<0||(simple+aggregate==0)){
			System.out.println("========================施加混合负载压力参与异常==========================");
			return ;
		}
		Random random = new Random();
		double simpeRatio=simple/(simple+aggregate);
		double aggregateRatio=aggregate/(simple+aggregate);
		OnlineConfig config = InitManager.getOnlineConfig();
		List<Database> dbs = config.getDatabases();
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					Boolean flag = flagMap.get("flag");
					if(!flag){
						break;
					}
					for(Database db:dbs){
						String type = db.getType();
						if(!"influxdb".equals(type)){//FIXME influxdb
							continue;
						}
						BaseDao dao = DaoFactory.getDao(type);
						pool=Executors.newFixedThreadPool(maxLoad+1);
						long startTime=System.currentTimeMillis();
						List<Integer> successSimpleList=new ArrayList<Integer>();
						List<Integer> failedSimpleList=new ArrayList<Integer>();
						List<Integer> successAggreList=new ArrayList<Integer>();
						List<Integer> failedsAggreList=new ArrayList<Integer>();
						for(int j=0;j<maxLoad;j++){
							pool.execute(new Runnable() {
								@Override
								public void run() {
									
									if(random.nextDouble()<simpeRatio){
										long simpleCostTime = dao.selectPointsByTime(null, null, null, null);
										if(simpleCostTime<=1000&&simpleCostTime!=-1L){
											successSimpleList.add(1);
										}else{
											failedsAggreList.add(0);
										}
									}else{
										//分析查询
										List<String> sensors=new ArrayList<String>();
										for(int tmp=1;tmp<=500;tmp++){
											sensors.add("s"+tmp);
										}
										long aggreCostTime = dao.selectMaxByTimeAndDevice("10", null,null,"point",sensors);
										if(aggreCostTime<=1000&&aggreCostTime!=-1L){
											successAggreList.add(1);
										}else{
											failedsAggreList.add(0);
										}
									}
									
								}
							});
						}
						pool.shutdown();
						while(true){
							if(pool.isTerminated()){
								String sql="insert load_stable_query(batch_id,db_type,operate_time,target_times_per,actual_times_per,"
										+ "target_simple_query_count,actual_simple_query_count,target_aggre_query_count,actual_aggre_query_count)"
										+ " values(?,?,?,?,?,?,?,?,?)";
								Object[] params={BizUtils.getBatchId(),1,new Timestamp(startTime),maxLoad,successAggreList.size()+successSimpleList.size(),
										successSimpleList.size()+failedSimpleList.size(),successSimpleList.size(),
										successAggreList.size()+failedsAggreList.size(),successAggreList.size()};
								BizUtils.insertBySqlAndParam(sql,params);
								try {
									long endTime=System.currentTimeMillis();
									long costTime=endTime-startTime;
									if(costTime<=1000){
										try {
											Thread.currentThread().sleep(1000L-(costTime));
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
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
			}
		});
		thread.start();
	}
	private int getMaxQueryLoad(double simple, double aggregate) {
		return 50;//FIXME 需要查库进行分析 
	}
	private void endStableQueryLoad(Map<String,Boolean> flagMap) {
		flagMap.put("flag", false);
	}
}
