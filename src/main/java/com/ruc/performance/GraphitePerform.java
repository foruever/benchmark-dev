package com.ruc.performance;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ruc.CommonUtils;
import com.ruc.crud.GraphiteDao;

public class GraphitePerform {
	private final static String PATH="graphite-perform"+"_"+System.currentTimeMillis()+".pf";
	public static void main(String[] args) throws Exception {
//		beginCal("");
//		getLoadSpeed("http://influx-api.10step.top",null);
		getWriteThroughputAndReponse("192.168.241.130","2003");
//		getQueryThroughputAndResponse("http://192.168.241.128","8086");
	}
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
			Socket socket = new Socket(host, Integer.parseInt(port));
			OutputStream os = socket.getOutputStream();// 字节输出流
			PrintWriter pw = new PrintWriter(os);// 将输出流包装成打印流
			int flag=100;
			long start=System.currentTimeMillis()-600000L;
			while (start<System.currentTimeMillis()) {
				StringBuilder sc = new StringBuilder();
				for (int i = 0; i < 1; i++) {
					sc.append("ruc_perform_test.ruc_test.location.changping "
							+ (start)  + " "
							+ start  + "\n");// 必须带换行
					start+=1000;
																		// FIXME
				}
				System.out.println((sc.toString()));
				// 2、获取输出流，向服务器端发送信息
				pw.write(sc.toString());
				pw.flush();
			}
			socket.shutdownOutput();
			pw.close();
			os.close();
			socket.close();
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
}
