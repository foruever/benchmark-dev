package com.ruc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ruc.constant.ConstantManager;

/**
 * 测试类
 * @author sxg
 *
 */
public class TTTTTTTTTTTTest {
	public static void main(String[] args) throws Exception {
		CommonUtils.getRandomNumBetweenTwo(10, 2000000000);
//		Long start=System.currentTimeMillis();
//		UUID.randomUUID().toString();
//		System.out.println(UUID.randomUUID().toString());
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				System.out.println("1111111111");
//				try {
//					Thread.sleep(5000L);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				System.out.println("22222222222");
//			}
//		}).start();
//		System.out.println("333333333333333333");
//		mergeFile1();
//		mergeFile2(null,null);
//		Long end=System.currentTimeMillis();
//		System.out.println((end-start)/1000+"s");
//		ExecutorService testThreads = Executors.newFixedThreadPool(10);  
//		for(int i=0;i<3;i++){
//			testThreads.execute(new Runnable() {
//				@Override
//				public void run() {
//					for(int i=0;i<10;i++){
//						try {
//							Thread.currentThread().sleep(1000L);
//							System.out.println(Thread.currentThread().getName()+":"+ i);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			});
//		}
//		testThreads.shutdown();
//		while(true){
//			 if (testThreads.isTerminated()) {  
//	              System.out.println("结束了！");  
//	              break;  
//	         }  
//	            Thread.sleep(200);  
//		}
//		generateRandom();
//		generateRandomFloat();
//		System.out.println(Long.MAX_VALUE);
//		System.out.println(Long.MIN_VALUE);
//		for(int i=0;i<100;i++){
//			System.out.println(CommonUtils.generateRandomInt(-1, 1));
//		}
//		for(int i=0;i<100;i++){
//			System.out.println(CommonUtils.generateRandomFloat(-1, 1));
//		}
//		System.out.println(ConstantManager.timestamp_Map.size());
//		for(int i=0;i<1000000;i++){
//			long generateSysTimestamp = CommonUtils.generateSysTimestamp();
//			System.out.println(generateSysTimestamp);
//		}
//		testDate();
//		System.out.println(ConstantManager.timestamp_Map.size());
//		System.out.println(CommonUtils.max);
	}
	/**
	 * 生成某个范围内的int随机数
	 */
	public static void generateRandom(){
		int min=0;
		int max=1;
		int random;
		Random r=new Random();
		for(int i=0;i<100000;i++){
			random=min+r.nextInt(max-min);
			System.out.println(random);
		}
	}
	/**
	 * 生成某个范围内的float随机数
	 */
	public static void generateRandomFloat(){
		Long min=-1L;
		Long max=1L;
		double random;
		Random r=new Random();
		for(int i=0;i<100000;i++){
			random=min+(max-min)*r.nextDouble();
			System.out.println(random);
		}
	}
	/**
	 * 合并多个文件
	 */
	public static void mergeFile1(){
		FileChannel fc=null;
		String[] files={"E://1-data/1.txt","E://1-data/2.txt","E://1-data/3.txt","E://1-data/4.txt","E://1-data/5.txt"};
		File file=new File("E://1-data/merge.txt");
		try {
			fc=new FileOutputStream(file).getChannel();
			for(String fstr:files){
				FileChannel channel = new FileInputStream(new File(fstr)).getChannel();
				ByteBuffer bb = ByteBuffer.allocate(1024*8);
				while(channel.read(bb)!=-1){
					bb.flip();
					fc.write(bb);
					bb.clear();
				}
				channel.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fc!=null){
					fc.close();
				}
				System.out.println("合并万恒");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean mergeFile2(String[] fpaths, String resultPath) {


	    File resultFile = new File("E://1-data/merge.txt");
	    String[] files={"E://1-data/1.txt","E://1-data/2.txt","E://1-data/3.txt","E://1-data/4.txt","E://1-data/5.txt"};
	    try {
	        FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
	        for(String fstr:files){
	        	 FileChannel blk = new FileInputStream(new File(fstr)).getChannel();
		            resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
		            blk.close();
	        }
	        resultFileChannel.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return true;
	}
	/**
	 * 
	 * @param fpaths
	 * @param resultPath
	 * @return
	 */
	public static boolean mergeFile3(String[] fpaths, String resultPath) {
		
		
		File resultFile = new File("E://1-data/merge.txt");
		String[] files={"E://1-data/1.txt","E://1-data/2.txt","E://1-data/3.txt","E://1-data/4.txt","E://1-data/5.txt"};
		try {
			FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
			for(String fstr:files){
				FileChannel blk = new FileInputStream(new File(fstr)).getChannel();
				resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
				blk.close();
			}
			resultFileChannel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
//	public static void testExportFile(){
//	File file=new File("D:test.csv");
//	if(file.exists()){
//		file.delete();
//	}
//	FileWriter fw=new FileWriter(file);
//	for(int i=0;i<100;i++){
//		StringBuilder sc=new StringBuilder();
//		sc.append("a,b,c,d,e,"+i);
//		sc.append("\r\n");
//		fw.write(sc.toString());
//	}
//	fw.close();
	
//	//打印路径
//	String path="";
//	path=CSVTest.class.getClassLoader().getResource("").getPath();//到处jar后，获取当前jar包所在根路径
//	System.out.println(path);
//	//方法1
//	File file=new File(path+"/test.properties");//FileReader读取路径 --start
//	FileReader fr=new FileReader(file);
//	BufferedReader br=new BufferedReader(fr);
//	String s="";
//	while((s=br.readLine())!=null){
//		System.out.println(s);
//	}	
//	br.close();//FileReader读取路径 --end
//	//方法2
//	Properties pro=new Properties();//Properties读取路径 --start
//	FileInputStream fis=new FileInputStream(file);
//	pro.load(fis);
//	String filestr = pro.getProperty("file");
//	String locationstr = pro.getProperty("location");//Properties读取路径 --end
//	System.out.println(filestr+":"+locationstr);
//	
//	//方法3 ResourceBundle
//	ResourceBundle conf= ResourceBundle.getBundle("test");
//	String file3 = conf.getString("file");
//	String location3 = conf.getString("location");
//	System.out.println("file3:"+file3);
//	System.out.println("location3"+location3);
//}
	public static void testDate() throws Exception{
		String dateStr="2015-11-12";
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(dateStr);
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		System.out.println(System.currentTimeMillis()-date.getTime());
	}
}
