package com.ruc.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import org.apache.commons.lang.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;

import retrofit2.Retrofit;
import cn.edu.thu.tsfile.timeseries.read.FileReader;

import com.google.gson.GsonBuilder;
import com.ruc.BigDataGenerateMain;
import com.ruc.CommonUtils;
import com.ruc.constant.ConstantManager;
import com.ruc.constant.TypeEnum;
import com.ruc.model.influxdb.InfluxdbField;
import com.ruc.model.influxdb.InfluxdbValue;

/**
 * influxDB performance
 * @author sxg
 */
public class InfluxDBPerform implements DbPerform{
	private final static String PATH="influxdb-perform"+"_"+System.currentTimeMillis()+".pf";
	public static void main(String[] args) throws Exception {
//		beginCal("");
//		getLoadSpeed("http://influx-api.10step.top",null);
//		getWriteThroughputAndReponse("http://influx-api.10step.top",null);
//		getWriteThroughputAndReponse("http://192.168.241.128","8086");
//		getQueryThroughputAndResponse("http://192.168.241.128","8086");
		testttttttttttttttt();
	}
	public  static void beginCal(String mode) throws Exception{
//		initOkHttpTimeOut("http://influx-api.10step.top");
		Long generateStartTime=System.currentTimeMillis();
		BigDataGenerateMain.main(null);
		StringBuilder sc=new StringBuilder();
		File file=new File(ConstantManager.FILE_PATH);
		InputStreamReader isr=new InputStreamReader(new FileInputStream(file));
		long fileSize=file.length();
//		System.out.println(file.length());
		BufferedReader br=new BufferedReader(isr);
		String line=null;
		int count=0;
		while((line=br.readLine())!=null){
			sc.append(line);
			sc.append('\n');
			count++;
		}
//		Thread.currentThread().sleep(100000L);
//		System.out.println(count);
//		for(int i=0;i<100000;i++){
//			sc.append("sxg_test,host=server01,region=us-west value="+i+" "+(System.currentTimeMillis()+i));
//			sc.append("\n");
//		}
//		String records="cpu_load_short,host=server01,region=us-west value=0.64 "+System.currentTimeMillis();
		okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
		builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS);
//		InfluxDB influxDB = InfluxDBFactory.connect("http://influx-api.10step.top",,builder); 
		InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.254.132:8086",builder); 
		String dbName = "ruc_perform_test";  
		influxDB.createDatabase(dbName);  
		Long importStartTime=System.currentTimeMillis();
		try {
			influxDB.write(dbName,null, ConsistencyLevel.ALL, sc.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long importEndTime=System.currentTimeMillis();
		long costms=(importEndTime-importStartTime);
		System.out.println("import cost time ["+costms/1000.0+"]s");
		System.out.println("average ["+count/costms*1000+"] mertrics/s");
		System.out.println("average ["+(fileSize/(1000.0*1000.0))/costms*1000+"] M/s");
//		influxDB.deleteDatabase(dbName);
	}
	private static void initOkHttpTimeOut(String baseUrl) {
		 //retrofit底层用的okHttp,所以设置超时还需要okHttp
		 //然后设置5秒超时
		 //其中DEFAULT_TIMEOUT是我这边定义的一个常量
		 //TimeUnit为java.util.concurrent包下的时间单位
		 //TimeUnit.SECONDS这里为秒的单位
		 OkHttpClient client = new OkHttpClient.Builder()
		    .connectTimeout(1, TimeUnit.SECONDS)
		      .writeTimeout(1, TimeUnit.SECONDS)
		       .readTimeout(1, TimeUnit.SECONDS)
		                        .build();

		  //构建Retrofit对象
		  //然后将刚才设置好的okHttp对象,通过retrofit.client()方法 设置到retrofit中去
		 Retrofit build = new Retrofit.Builder().baseUrl(baseUrl).client(client).build();
//		    retrofit = new Retrofit.Builder().baseUrl(baseUrl)
//		    .addConverterFactory(GsonConverterFactory.create()).client(client).build();
	}
	
	/**
	 * 1，装载速度测试
	 * @param host http://.... ip或者url
	 */
	public static void getLoadSpeed(String host,String port){
		System.out.println("==================load speed test start===================");
		try {
			
			String url=host;
			if(StringUtils.isNotBlank(port)){
				url=url+":"+port;
			}
			BigDataGenerateMain.main(null);
			StringBuilder sc=new StringBuilder();
			File file=new File(ConstantManager.FILE_PATH);//FIXME 可修改为list 多线程保存
			InputStreamReader isr=new InputStreamReader(new FileInputStream(file));
			long fileSize=file.length();
			BufferedReader br=new BufferedReader(isr);
			String line=null;
			int count=0;
			while((line=br.readLine())!=null){
				sc.append(line);
				sc.append('\n');
				count++;
			}
			okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
			builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
			InfluxDB influxDB = InfluxDBFactory.connect(url,builder); 
			String dbName = "ruc_perform_test";  
			influxDB.createDatabase(dbName);  
			Long importStartTime=System.currentTimeMillis();
			try {
				influxDB.write(dbName,null, ConsistencyLevel.ALL, sc.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Long importEndTime=System.currentTimeMillis();
			long costms=(importEndTime-importStartTime);
			System.out.println("import cost time ["+costms/1000.0+"]s");
			System.out.println("average ["+count/costms*1000+"] mertrics/s");
			System.out.println("average ["+(fileSize/(1000.0*1000.0))/costms*1000+"] M/s");
			influxDB.deleteDatabase(dbName);
			//保存文件 记录到文件
			CommonUtils.addStringToFile(PATH, "#########load speed########\n");
			CommonUtils.addStringToFile(PATH, "average ["+count/costms*1000+"] mertrics/s\n");
			CommonUtils.addStringToFile(PATH, "average ["+(fileSize/(1000.0*1000.0))/costms*1000+"] M/s\n");
			CommonUtils.addStringToFile(PATH, "\n\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("==================load speed test end===================");
	}
	/**
	 * 2，3 写操作的吞吐量和响应时间
	 */
	public static void getWriteThroughputAndReponse(String host,String port){
		System.out.println("==================write through put and reponse test start===================");
		try {
			Random random=new Random();
			String url=host;
			if(StringUtils.isNotBlank(port)){
				url=url+":"+port;
			}
//			long[] lines={1,100,10000,100000,1000000};
//			long[] lines={1,100,10000};
			long[] lines={1,100,10000,100000,1000000};
			Map<String,List<Double>> timeoutmap =initWriteTimeoutStatMap();
			Map<String,List<Double>> speedMap=initWriteSpeedStatMap();
			for(int i=0;i<lines.length&&i<lines[i];i++){
				long line=lines[i];
				double sumTime=0;
				double sumspeed=0;
				for(int p=0;p<100;p++){
					okhttp3.OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
					cbuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
					InfluxDB influxDB = InfluxDBFactory.connect(url,cbuilder); 
					String dbName = "ruc_perform_test";  
					influxDB.createDatabase(dbName);  
					String measurement="ruc.test";
					long startTime = System.currentTimeMillis()-100000000L;
					org.influxdb.dto.BatchPoints.Builder builder = BatchPoints  
							.database(dbName)  
							.consistency(ConsistencyLevel.ALL);
					builder.tag("location","shanxi");
					builder.tag("location","beijing");
					BatchPoints batchPoints = builder.build();
					for(int j=0;j<line;j++){
						org.influxdb.dto.Point.Builder pointBuilder = Point.measurement(measurement)  
								.time(startTime++, TimeUnit.MILLISECONDS); 
						pointBuilder.addField("value",random.nextLong());
						batchPoints.point(pointBuilder.build());
					}
					Long writeStartTime=System.currentTimeMillis();
					influxDB.write(batchPoints);
					Long writeEndTime=System.currentTimeMillis();
					System.out.println("insert "+line+" lines cost ["+(writeEndTime-writeStartTime)+"]ms");
					sumTime+=(writeEndTime-writeStartTime);
					sumspeed+=line/(double)(writeEndTime-writeStartTime);
					timeoutmap.get(line+"_line").add((double) (writeEndTime-writeStartTime));
					speedMap.get(line+"_line").add(line/(double)(writeEndTime-writeStartTime));
					influxDB.deleteDatabase(dbName);
				}
				Collections.sort(timeoutmap.get(line+"_line"));
				Collections.sort(speedMap.get(line+"_line"));
				CommonUtils.addStringToFile(PATH,"###############lines:"+line+"##############\n");
				CommonUtils.addStringToFile(PATH,"max:["+Collections.max(timeoutmap.get(line+"_line")).toString()+"]ms，");
				CommonUtils.addStringToFile(PATH,"min:["+Collections.min(timeoutmap.get(line+"_line")).toString()+"]ms，");
				CommonUtils.addStringToFile(PATH,"mean:["+sumTime/100+"]ms，");
				CommonUtils.addStringToFile(PATH,"95%:["+timeoutmap.get(line+"_line").get(94)+"]ms，");
				CommonUtils.addStringToFile(PATH,"99%:["+timeoutmap.get(line+"_line").get(98)+"]ms\n");
				
				CommonUtils.addStringToFile(PATH,"max:["+(long)(Collections.max(speedMap.get(line+"_line"))*1000)+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"min:["+(long)(Collections.min(speedMap.get(line+"_line"))*1000)+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"mean:["+(long)(sumspeed/100*1000)+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"95%:["+(long)(speedMap.get(line+"_line").get(94)*1000)+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"99%:["+(long)(speedMap.get(line+"_line").get(98)*1000)+"]lines/s\n");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		CommonUtils.addStringToFile(PATH,"\n\n\n");
		System.out.println("==================write through put and reponse test end===================");
	}
	private static Map<String, List<Double>> initWriteTimeoutStatMap() {
		Map<String,List<Double>> map=new HashMap<String, List<Double>>();
		map.put("1_line",new ArrayList<Double>());
//		map.put("10_line",new ArrayList<Double>());
		map.put("100_line",new ArrayList<Double>());
//		map.put("1000_line",new ArrayList<Double>());
		map.put("10000_line",new ArrayList<Double>());
		map.put("100000_line",new ArrayList<Double>());
		map.put("1000000_line",new ArrayList<Double>());
		return map;
	}
	private static Map<String, List<Double>> initWriteSpeedStatMap() {
		Map<String,List<Double>> map=new HashMap<String, List<Double>>();
		map.put("1_line",new ArrayList<Double>());
//		map.put("10_line",new ArrayList<Double>());
		map.put("100_line",new ArrayList<Double>());
//		map.put("1000_line",new ArrayList<Double>());
		map.put("10000_line",new ArrayList<Double>());
		map.put("100000_line",new ArrayList<Double>());
		map.put("1000000_line",new ArrayList<Double>());
		return map;
	}
	/**
	 * 4，5 读操作的吞吐量和响应时间
	 */
	public static void getQueryThroughputAndResponse(String host,String port){
		System.out.println("==================query through put and reponse test start===================");
		try {
			String url=host;
			if(StringUtils.isNotBlank(port)){
				url=url+":"+port;
			}
			long startTime=System.currentTimeMillis()-100000000L;
			String dbName = "ruc_perform_test";  
			String measurement="ruc_test";
			//1,先插入100,000,000条数据  每次100,000条
			System.out.println("=============  insert 100,000,000 lines start ============= ");
			
//			for(int i=0;i<1000;i++){
//				okhttp3.OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
//				cbuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
//				InfluxDB influxDB = InfluxDBFactory.connect(url,cbuilder); 
//				influxDB.createDatabase(dbName);  
//				org.influxdb.dto.BatchPoints.Builder builder = BatchPoints  
//						.database(dbName)  
//						.consistency(ConsistencyLevel.ALL);
//				builder.tag("location","shanxi");
//				BatchPoints batchPoints = builder.build();
//				for(int j=0;j<100000;j++){
//					org.influxdb.dto.Point.Builder pointBuilder = Point.measurement(measurement)  
//							.time(startTime++, TimeUnit.MILLISECONDS); 
//					pointBuilder.addField("value",j);
//					batchPoints.point(pointBuilder.build());
//				}
//				influxDB.write(batchPoints);
//				System.out.println("====== has finished ["+(i+1)*100000+"]lines");
//			}
			System.out.println("============= insert 100,000,000 lines end =============");
			//2,查询不同规模数据，并保存日志
			CommonUtils.addStringToFile(PATH,"############### influxDB query through put And response ##############\n");
			okhttp3.OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
			cbuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
			InfluxDB influxDB = InfluxDBFactory.connect(url,cbuilder); 
			long[] lines={1000,10000,100000,300000,1000000,3000000};
			Map<String, List<Double>> timeoutmap = initReadTimeoutStatMap();
			Map<String, List<Long>> speedMap = initReadSpeedStatMap();
			for(int i=0;i<lines.length&&i<lines[i];i++){
				long line=lines[i];
				String sql= "select * from "+measurement +" limit "+lines[i];
				System.out.println(sql);
				Query query=new Query(sql, dbName);
				double sumTime=0;
				long sumspeed=0;
				for(int j=0;j<100;j++){
					long startTime1 = System.currentTimeMillis();
					influxDB.query(query, TimeUnit.MILLISECONDS);
					long endTime2 = System.currentTimeMillis();
					long costTime=endTime2-startTime1;
					timeoutmap.get(line+"_line").add(costTime/(double)line);
					speedMap.get(line+"_line").add((long) (line/(double)costTime*1000));
					sumTime+=(costTime/(double)line);
					sumspeed+=(long) (line/(double)costTime*1000);
				}
				Collections.sort(timeoutmap.get(line+"_line"));
				Collections.sort(speedMap.get(line+"_line"));
				CommonUtils.addStringToFile(PATH,"###############lines:"+line+"##############\n");
				CommonUtils.addStringToFile(PATH,"max:["+Collections.max(timeoutmap.get(line+"_line")).toString()+"]ms/line，");
				CommonUtils.addStringToFile(PATH,"min:["+Collections.min(timeoutmap.get(line+"_line")).toString()+"]ms/line，");
				CommonUtils.addStringToFile(PATH,"mean:["+sumTime/100.0+"]ms/line，");
				CommonUtils.addStringToFile(PATH,"95%:["+timeoutmap.get(line+"_line").get(94)+"]ms/line，");
				CommonUtils.addStringToFile(PATH,"99%:["+timeoutmap.get(line+"_line").get(98)+"]ms/line\n");
				CommonUtils.addStringToFile(PATH,"max:["+(long)(Collections.max(speedMap.get(line+"_line")))+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"min:["+(long)(Collections.min(speedMap.get(line+"_line")))+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"mean:["+(long)(sumspeed/100)+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"95%:["+(long)(speedMap.get(line+"_line").get(5))+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"99%:["+(long)(speedMap.get(line+"_line").get(1))+"]lines/s\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("==================query through put and reponse test end===================");
	}
	
	private static Map<String, List<Double>> initReadTimeoutStatMap() {
		Map<String,List<Double>> map=new HashMap<String, List<Double>>();
		map.put("1000_line",new ArrayList<Double>());
		map.put("10000_line",new ArrayList<Double>());
		map.put("100000_line",new ArrayList<Double>());
		map.put("300000_line",new ArrayList<Double>());
		map.put("1000000_line",new ArrayList<Double>());
		map.put("3000000_line",new ArrayList<Double>());
		return map;
	}
	private static Map<String, List<Long>> initReadSpeedStatMap() {
		Map<String,List<Long>> map=new HashMap<String, List<Long>>();
		map.put("1000_line",new ArrayList<Long>());
		map.put("10000_line",new ArrayList<Long>());
		map.put("100000_line",new ArrayList<Long>());
		map.put("300000_line",new ArrayList<Long>());
		map.put("1000000_line",new ArrayList<Long>());
		map.put("3000000_line",new ArrayList<Long>());
		return map;
	}
	/**
	 * 6，获取压缩比
	 */
	public static void getCompressionRatio(){
		
	}
	public static void testttttttttttttttt(){
		String sql= "select * from ruc_test limit "+1000000;
		System.out.println(sql);
		Query query=new Query(sql,"ruc_perform_test");
		okhttp3.OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();//FIXME okHttp 设置超时时间
		cbuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS);
		InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.241.128:8086",cbuilder);
		Long startTime=System.currentTimeMillis();
		influxDB.query(query, TimeUnit.MILLISECONDS);
		Long endTime=System.currentTimeMillis();
		System.out.println(endTime-startTime);
		System.out.println(""+1000000.0/(endTime-startTime)*1000+"lines/sec");
		System.out.println(""+(endTime-startTime)/1000000.0+"ms/line");
	}
}
