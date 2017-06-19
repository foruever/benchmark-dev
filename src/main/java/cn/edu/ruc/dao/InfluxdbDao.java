package cn.edu.ruc.dao;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;

import cn.edu.ruc.conf.base.device.Tag;
import cn.edu.ruc.model.BenchmarkPoint;
import okhttp3.OkHttpClient;

public class InfluxdbDao implements BaseDao {
	private InfluxDB influxDB=null;
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
//		InfluxDB influxDB = InfluxDBFactory.connect("http://influx-api.10step.top",cbuilder);//FIXME URL 
		InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.254.132:8086",cbuilder);//FIXME URL 
		String dbName = "benchmark_perform";  
		influxDB.createDatabase(dbName);  
		org.influxdb.dto.BatchPoints.Builder builder = BatchPoints  
				.database(dbName)  
				.consistency(ConsistencyLevel.ALL);
		if(points.size()>0){//FIXME 可优化
			BenchmarkPoint benchmarkPoint = points.get(0);
			for(Tag tag:benchmarkPoint.getDeviceTags()){
				builder.tag(tag.getKey(),tag.getValue());
			}
		}
		BatchPoints batchPoints = builder.build();
		for(BenchmarkPoint point:points){
			String measurement=point.getDeviceName();
			org.influxdb.dto.Point.Builder pointBuilder = Point.measurement(measurement)  
					.time(point.getTimestamp(), TimeUnit.MILLISECONDS); 
			pointBuilder.addField(point.getSensorName(),point.getValue());
			batchPoints.point(pointBuilder.build());
		}
		long beginTime=System.currentTimeMillis();
		influxDB.write(batchPoints);
		long endTime=System.currentTimeMillis();
		long cost=endTime-beginTime;
//		System.out.println("lines:"+points.size()+"||cost:"+cost);
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
	String sql="select s1 from fengche1 limit 1 ";
	Query query=new Query(sql,"ruc_benchmark");
	@Override
	public List<Object> selectPointsByTime(Date beginTime, Date endTime,
			String device, String sensor) {
		QueryResult qr = influxDB.query(query);
		System.out.println(qr);
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


}

