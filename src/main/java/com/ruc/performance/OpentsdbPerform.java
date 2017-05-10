package com.ruc.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ruc.CommonUtils;
import com.ruc.httpapi.opentsdb.ExpectResponse;
import com.ruc.httpapi.opentsdb.HttpClient;
import com.ruc.httpapi.opentsdb.HttpClientImpl;
import com.ruc.httpapi.opentsdb.builder.MetricBuilder;
import com.ruc.httpapi.opentsdb.request.Query;
import com.ruc.httpapi.opentsdb.request.QueryBuilder;
import com.ruc.httpapi.opentsdb.request.SubQueries;
import com.ruc.httpapi.opentsdb.response.Response;
import com.ruc.httpapi.opentsdb.response.SimpleHttpResponse;
import com.ruc.model.RecordTime;

public class OpentsdbPerform {
	private final static String PATH="opentsdb-perform"+"_"+System.currentTimeMillis()+".pf";
	public static void main(String[] args) throws Exception {
//		beginCal("");
//		getLoadSpeed("http://influx-api.10step.top",null);
//		getWriteThroughputAndReponse("192.168.254.132","4242");
		getQueryThroughputAndResponse("http://192.168.241.128","4242");
//		hbase();
	}
    // 声明静态配置
//    static Configuration conf = null;
//    static {
//        conf = HBaseConfiguration.create();
//        conf.set("hbase.zookeeper.quorum", "192.168.254.132");
////        conf.set("hbase.zookeeper.property.clientPort", "2181");
//    }
//	public static void hbase() throws Exception{
//		HBaseAdmin admin = new HBaseAdmin(conf);
//		String tableName = "blog2";
//		String[] family = { "article", "author" };
//	    HTableDescriptor desc = new HTableDescriptor(tableName);
//	    for (int i = 0; i < family.length; i++) {
//	       desc.addFamily(new HColumnDescriptor(family[i]));
//	    }
//	    admin.createTable(desc);
//	    System.out.println("create table Success!");
//	}
	public  static void beginCal(String mode) throws Exception{
		
	}
	
	/**
	 * 1，装载速度测试
	 * @param host http://.... ip或者url
	 */
	public static void getLoadSpeed(String host,String port){
		System.out.println("==================load speed test start===================");
		try {
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
			String url=host;
			if(StringUtils.isNotBlank(port)){
				url=url+":"+port;
			}
			CommonUtils.addStringToFile(PATH,"############### opentsdb write through put And response ##############\n");
			HttpClient client = new HttpClientImpl(url);
			long[] lines={1,100,10000,100000,1000000};
			Map<String,List<Double>> timeoutmap =initWriteTimeoutStatMap();
			Map<String,List<Double>> speedMap=initWriteSpeedStatMap();
			MetricBuilder builder = MetricBuilder.getInstance();
			for(int i=0;i<lines.length&&i<lines[i];i++){
				long line=lines[i];
				double sumTime=0;
				double sumspeed=0;
				Long timestamp= System.currentTimeMillis()-1000000000L;
				for(int p=0;p<100;p++){
					builder.addMetric("ruc_perform_test.ruc_test").setDataPoint(timestamp,CommonUtils.getSineLine(1000*1000L, 100, 0, 150,new RecordTime()))
					.addTag("location", "beijing");
					timestamp+=1000L;
					Long writeStartTime= System.currentTimeMillis();
					Response response = client.pushMetrics(builder,ExpectResponse.DETAIL);
					Long writeEndTime= System.currentTimeMillis();
					System.out.println("insert "+line+" lines cost ["+(writeEndTime-writeStartTime)+"]ms");
					sumTime+=(writeEndTime-writeStartTime);
					sumspeed+=line/(double)(writeEndTime-writeStartTime);
					timeoutmap.get(line+"_line").add((double) (writeEndTime-writeStartTime));
					speedMap.get(line+"_line").add(line/(double)(writeEndTime-writeStartTime));
					//FIXME delete 
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
				CommonUtils.addStringToFile(PATH,"95%:["+(long)(speedMap.get(line+"_line").get(5)*1000)+"]lines/s，");
				CommonUtils.addStringToFile(PATH,"99%:["+(long)(speedMap.get(line+"_line").get(1)*1000)+"]lines/s\n");
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
		map.put("100_line",new ArrayList<Double>());
		map.put("10000_line",new ArrayList<Double>());
		map.put("100000_line",new ArrayList<Double>());
		map.put("1000000_line",new ArrayList<Double>());
		return map;
	}
	private static Map<String, List<Double>> initWriteSpeedStatMap() {
		Map<String,List<Double>> map=new HashMap<String, List<Double>>();
		map.put("1_line",new ArrayList<Double>());
		map.put("100_line",new ArrayList<Double>());
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
			HttpClient client = new HttpClientImpl(url);
			//先插入 100,000,000条数据
			Long timestamp= System.currentTimeMillis()-100000000000L;
			for(int i=0;i<1000;i++){
				MetricBuilder builder = MetricBuilder.getInstance();
				for(int p=0;p<100000;p++){
					builder.addMetric("ruc_perform_test.ruc_test").setDataPoint(timestamp,CommonUtils.getSineLine(1000*1000L, 100, 0, 150,new RecordTime()))
					.addTag("location", "beijing");
					timestamp+=1000L;
				}
				Response response = client.pushMetrics(builder,ExpectResponse.STATUS_CODE);
				System.out.println("has insert "+(i+1)*100000+"条数据");
			}
			//进行查询统计
			CommonUtils.addStringToFile(PATH,"############### opentsdb query through put And response ##############\n");
			long[] lines={1000,10000,100000,300000,1000000,3000000};
			Map<String, List<Double>> timeoutmap = initReadTimeoutStatMap();
			Map<String, List<Long>> speedMap = initReadSpeedStatMap();
			for(int i=0;i<lines.length&&i<lines[i];i++){
				long line=lines[i];
				Query query=new Query(timestamp);
				query.addEnd(System.currentTimeMillis());
				QueryBuilder qb = QueryBuilder.getInstance();
				qb.getQuery().addStart(timestamp).addEnd(timestamp+line*1000);
				SubQueries sq=new SubQueries();
				sq.setMetric("ruc_perform_test.ruc_test");
				sq.setAggregator("avg");
				qb.getQuery().addSubQuery(sq);
				double sumTime=0;
				long sumspeed=0;
				for(int j=0;j<100;j++){
					long startTime = System.currentTimeMillis();
					SimpleHttpResponse pushQueries = client.pushQueries(qb);
					long endTime = System.currentTimeMillis();
					long costTime=endTime-startTime;
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
		CommonUtils.addStringToFile(PATH,"\n\n\n");
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
}
