package com.ruc.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import cn.edu.thu.tsfile.common.utils.RandomAccessOutputStream;
import cn.edu.thu.tsfile.common.utils.TSRandomAccessFileReader;
import cn.edu.thu.tsfile.common.utils.TSRandomAccessFileWriter;
import cn.edu.thu.tsfile.timeseries.FileFormat.TsFile;
import cn.edu.thu.tsfile.timeseries.read.LocalFileInput;
import cn.edu.thu.tsfile.timeseries.read.qp.Path;
import cn.edu.thu.tsfile.timeseries.read.query.QueryDataSet;
import cn.edu.thu.tsfile.timeseries.write.exception.WriteProcessException;
import cn.edu.thu.tsfile.timeseries.write.record.TSRecord;


public class MainMain {
	public static void main(String[] args) {
//		LinkedList<String> ll=new LinkedList<String>();
//		ll.addFirst("");
//		ll.getFirst();
//		ll.removeFirst();
//		testSocket();
//		pool.execute(new Runnable() {
//			
//			@Override
//			public void run() {
////				produce();
//			}
//		});
//		pool.execute(new Runnable() {
//			
//			@Override
//			public void run() {
//				customer();
//			}
//		});
//		ArrayBlockingQueue<Long> abq=new ArrayBlockingQueue<Long>(1000);
//		for(int i=0;i<100;i++){
//			abq.add((long)i);
//		}
//		for(Long i:abq){
////			System.out.println(i);
//		}
//		System.out.println(abq.size());
//		int seq=0;
//		if(!abq.contains(10L)){
//			System.out.println(-1);
//		}else{
//			Iterator<Long> it = abq.iterator();
//			while(it.hasNext()){
//				seq++;
//				Long next = it.next();
//				if(next.longValue()==(long)10){
//					System.out.println(seq);
//				}
//			}
//			
//		};
//		while(true){
//			System.out.println("=====================start");
//			try {
//				Thread.currentThread().sleep(500L);
//				System.out.println(abq.take());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			System.out.println("=====================end");
//		}
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~tsFile write --start~~~~~~~~~~~~~~~~~~~~~~~~~~~
//		try {
//			String path = "test.ts";
//			String s = "{\n" +
//	                "    \"schema\": [\n" +
//	                "        {\n" +
//	                "            \"measurement_id\": \"sensor_1\",\n" +
//	                "            \"data_type\": \"FLOAT\",\n" +
//	                "            \"encoding\": \"RLE\"\n" +
//	                "        },\n" +
//	                "        {\n" +
//	                "            \"measurement_id\": \"sensor_2\",\n" +
//	                "            \"data_type\": \"INT32\",\n" +
//	                "            \"encoding\": \"TS_2DIFF\"\n" +
//	                "        },\n" +
//	                "        {\n" +
//	                "            \"measurement_id\": \"sensor_3\",\n" +
//	                "            \"data_type\": \"INT32\",\n" +
//	                "            \"encoding\": \"TS_2DIFF\"\n" +
//	                "        }\n" +
//	                "    ],\n" +
//	                "    \"row_group_size\": 134217728\n" +
//	                "}";
//			TSRandomAccessFileWriter writer =new RandomAccessOutputStream(new File("f://ts.ts"));
////			TSRandomAccessFileWriter writer =new RandomAccessOutputStream(new File("f://ts.ts"),"rws");
//			JSONObject jo=new JSONObject(s);
//			TsFile tsFile = new TsFile(writer,jo);
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+", sensor_1, 1.2, sensor_2, 20, sensor_3,");
//			Thread.currentThread().sleep(1000L);
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+", sensor_1, , sensor_2, 28, sensor_3, 56");
//			Thread.currentThread().sleep(1000L);
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+", sensor_1, 1.4, sensor_2, 21, sensor_3,");
//			Thread.currentThread().sleep(1000L);
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+",sensor_1,1.2,sensor_2,20,sensor_4,9999,sensor_3,51");
//			TSRecord tr=new TSRecord(System.currentTimeMillis(), "device_1");
//			tsFile.writeLine(tr);
//			tsFile.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

        // read example : no filter
        try {
			LocalFileInput input = new LocalFileInput("f://ts.ts");
			TsFile readTsFile = new TsFile(input);
			ArrayList<Path> paths = new ArrayList<Path>();
			paths.add(new Path("device_1.sensor_1"));
			paths.add(new Path("device_1.sensor_2"));
			paths.add(new Path("device_1.sensor_3"));
			QueryDataSet queryDataSet = readTsFile.query(paths, null, null);
			while(queryDataSet.hasNextRecord()){
			    System.out.println(queryDataSet.getNextRecord());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~tsFile write--end~~~~~~~~~~~~~~~~~~~~~~~~~~~
	}
	public static void testSocket(){
        Socket socket = new Socket();
        // ->@wjw_add
        try {
			socket.setReuseAddress(true);
			socket.setKeepAlive(true); // Will monitor the TCP connection is
			// valid
			socket.setTcpNoDelay(true); // Socket buffer Whetherclosed, to
			// ensure timely delivery of data
			socket.setSoLinger(true, 0); // Control calls close () method,
			// the underlying socket is closed
			// immediately
			// <-@wjw_add
			
			socket.connect(new InetSocketAddress("123.56.107.69", 6379), 2000);
			socket.setSoTimeout(2000);
			OutputStream outputStream = socket.getOutputStream();
			InputStream inputStream = socket.getInputStream();
			System.out.println(inputStream);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void listener(Long cscGroupId){
//		while(true){
//			while(){
//				Thread.currentThread().wait(500);
//			}
//		}
	}
	public static ExecutorService pool = Executors.newFixedThreadPool(15);
	private static BlockingQueue<String> eggs=new ArrayBlockingQueue<String>(1000);
	public static void produce(){
		for(int i=0;i<1000;i++){
			eggs.add(i+":hahaha");
			try {
				Thread.sleep(i*100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void customer(){
		while(true){
			try {
				System.out.println("customer:begin");
				System.out.println(eggs.poll(30,TimeUnit.SECONDS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
