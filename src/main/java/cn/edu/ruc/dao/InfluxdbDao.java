package cn.edu.ruc.dao;

import java.io.StringBufferInputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import cn.edu.ruc.conf.base.device.Tag;
import cn.edu.ruc.model.BenchmarkPoint;
import okhttp3.OkHttpClient;
/**
 * influxdb处理数据库类
 * @author Sunxg
 *
 */
public class InfluxdbDao implements BaseDao {
	private InfluxDB influxDB=null;
	public InfluxdbDao() {
		//FIXME 修改为连接资源统一管理
//		initInfluxDB();
	}
	private void initInfluxDB() {
		if(influxDB==null){
			okhttp3.OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
			cbuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
			influxDB = InfluxDBFactory.connect("http://influx-api.10step.top",cbuilder);//FIXME URL 
		}
	}
	private InfluxDB getSession(String url){
		if(influxDB==null){
			okhttp3.OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
			cbuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
			influxDB = InfluxDBFactory.connect(url,cbuilder);//FIXME URL 
		}
		return influxDB;
	}
	@Override
	public long insertMultiPoints(List<BenchmarkPoint> points) {
		okhttp3.OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
		cbuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
		InfluxDB influxDB = InfluxDBFactory.connect("http://10.77.110.224:8086",cbuilder);//FIXME URL 
//		InfluxDB influxDB = InfluxDBFactory.connect("http://114.115.137.143:8086",cbuilder);//FIXME URL 
//		InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.254.132:8086",cbuilder);//FIXME URL 
		String dbName = "benchmark_perform";  
		influxDB.createDatabase(dbName);  
		org.influxdb.dto.BatchPoints.Builder builder = BatchPoints  
				.database(dbName)  
				.consistency(ConsistencyLevel.ALL);
		BatchPoints batchPoints = builder.build();
		for(BenchmarkPoint benchmarkPoint:points){
			String deviceName=benchmarkPoint.getDeviceName();
			org.influxdb.dto.Point.Builder pointBuilder = Point.measurement("point")  
					.time(benchmarkPoint.getTimestamp(), TimeUnit.MILLISECONDS); 
			pointBuilder.addField(benchmarkPoint.getSensorName(),benchmarkPoint.getValue());
			for(Tag tag:benchmarkPoint.getDeviceTags()){
				builder.tag(tag.getKey(),tag.getValue());
			}
			builder.tag("device",deviceName);
			batchPoints.point(pointBuilder.build());
		}
		long beginTime=System.currentTimeMillis();
		influxDB.write(batchPoints);//FIXME 因为这个只能每次发送的tag(key=value)必须是相同的，不满足需求，所以需要自己修改下，调用http框架，发送数
		//FIXME 2017-06-28 已纠正   不过时间不太准确，可以再优化，基本版本完后再进行优化
		long endTime=System.currentTimeMillis();
		long cost=endTime-beginTime;
		influxDB.close();
		return cost;
	}

	@Override
	public boolean deleteAllPoints() {
		okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
		builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
//		InfluxDB influxDB = InfluxDBFactory.connect("http://influx-api.10step.top",builder); 
		InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.254.132:8086",builder); 
		influxDB.deleteDatabase("benchmark_perform");
		return false;
	}
	@Override
	public List<Object> selectPointsByTime(Date beginTime, Date endTime,
			String device, String sensor) {
		String sql="select s1,s10,s100 from fengche1 limit 10 ";
		Query query=new Query(sql,"ruc_benchmark");
		try {
			QueryResult qr = influxDB.query(query);
		} catch (Exception e) {
//			System.out.println("--");
		}
//		System.out.println(qr);
		return null;
	}
	private static long beginTime;
	private static long count=0;
	private static long seq=0;
	private static ExecutorService pool=Executors.newFixedThreadPool(1000);
	public static void main(String[] args) {
		InfluxdbDao dao=new InfluxdbDao();
		dao.getSession("http://influx-api.10step.top");
		for(int i=0;i<100;i++){
			pool=Executors.newFixedThreadPool(i+1);
			beginTime=System.currentTimeMillis();
			for(int j=0;j<i;j++){
				pool.execute(new Runnable() {
					@Override
					public void run() {
						dao.selectPointsByTime(null, null, null, null);
						long currentTime=System.currentTimeMillis();
//						System.out.println("currentTime-beginTime="+(currentTime-beginTime));
						if(currentTime-beginTime<=1000){
							count++;
						}
//						System.out.println("count="+count);
//						seq++;
//						System.out.println("seq="+seq);
					}
				});
			}
			pool.shutdown();
			while(true){
				if(pool.isTerminated()){
					System.out.println("i="+i+" success count="+count);
					count=0;
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
	@Override
	public Object selectMaxByTimeAndDevice(String sqlId, Date beginTime, Date endTime, String device,
			List<String> sensors) {
		StringBuilder sc=new StringBuilder();
		sc.append("select ");
		for(int i=0;i<sensors.size();i++){
			String sensor=sensors.get(i);
			sc.append("max(");
			sc.append(sensor);
			sc.append(")");
			if(i!=sensors.size()-1){
				sc.append(",");
			}
		}
		sc.append(" from ");
		sc.append(device);
		sc.append(" where time>='2017-06-01 00:00:00' and time< '2017-06-02 00:00:00' ");
		sc.append(" group by time(1h)");
		Query query=new Query(sc.toString(),"ruc_benchmark");
//		System.out.println(sc.toString());
		QueryResult qr = influxDB.query(query);
//		System.out.println(qr);
		return null;
	}


}

