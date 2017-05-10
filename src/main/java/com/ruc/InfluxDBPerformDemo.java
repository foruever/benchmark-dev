package com.ruc;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;

/**
 * influxDB performance
 * @author sxg
 */
public class InfluxDBPerformDemo {
	private final static String PATH="influxdb-perform"+"_"+System.currentTimeMillis()+".pf";
	public static void main(String[] args) throws Exception {
		testttttttttttttttt();
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
