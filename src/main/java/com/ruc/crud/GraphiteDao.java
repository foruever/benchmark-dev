package com.ruc.crud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ruc.CommonUtils;
import com.ruc.constant.TypeEnum;
import com.ruc.model.Config;
import com.ruc.model.RecordTime;
import com.ruc.model.graphite.GraphiteRecord;
import com.ruc.model.graphite.GraphiteValue;
import com.ruc.model.influxdb.InfluxdbRecord;

/**
 * graphite数据库操作类
 * 
 * @author Sunxg
 *
 */
public class GraphiteDao {
	private static Logger logger = Logger.getLogger(GraphiteDao.class);

	public static void main(String[] args) {
		String serverUrl = "192.168.254.134:2003";
		String[] split = serverUrl.split(":");
		try {
			Socket socket = new Socket(split[0], Integer.parseInt(split[1]));
			OutputStream os = socket.getOutputStream();// 字节输出流
			PrintWriter pw = new PrintWriter(os);// 将输出流包装成打印流
			int flag=100;
			while (flag!=1) {
				StringBuilder sc = new StringBuilder();
				for (int i = 0; i < 5; i++) {
					Thread.currentThread().sleep(1000L);
					sc.append("test.location.mm.ll.guangzhou "
							+ System.currentTimeMillis() % 1000 + " "
							+ System.currentTimeMillis()  + "\n");// 必须带换行
																		// FIXME
				}
				logger.info(sc.toString());
				// 2、获取输出流，向服务器端发送信息
				pw.write(sc.toString());
				pw.flush();
				// 3、获取输入流，并读取服务器端的响应信息
				// InputStream is = socket.getInputStream();
				// BufferedReader br = new BufferedReader(
				// new InputStreamReader(is));
				// String info = null;
				// while ((info = br.readLine()) != null) {
				// System.out.println("Hello,我是客户端，服务器说：" + info);
				// }
				// 4、关闭资源
				// br.close();
				// is.close();

			}
			socket.shutdownOutput();
			pw.close();
			os.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void onlineInsert(Config config) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String serverUrl = config.getServerUrl();
		List<GraphiteRecord> graphites = config.getGraphites();
		if (graphites.size() == 0) {
			return;
		}
		for (final GraphiteRecord record : graphites) {
			CommonUtils.ONLINE_THREADS.execute(new Runnable() {
				@Override
				public void run() {
					String[] split = serverUrl.split(":");
					String ip = split[0];
					Integer port = Integer.parseInt(split[1]);
					try {
						Socket socket = new Socket(ip, port);
						OutputStream os = socket.getOutputStream();// 字节输出流
						PrintWriter pw = new PrintWriter(os);// 将输出流包装成打印流
						long endTime=CommonUtils.data2timestamp(record.getEndTime());
						RecordTime time=new RecordTime(endTime,record.getStep(), record.getCacheLine());
						String path=record.getPath();
						GraphiteValue valueObj=record.getValue();
						String type =valueObj.getType();
						try {
							while(time.getCurrentTime()<=time.getEndTime()){//当当前时间小于结束时间时，继续生成
								StringBuilder sc=new StringBuilder();
								Date startDate=new Date(time.getCurrentTime());
								for(int i=0;i<record.getCacheLine();i++){
									sc.append(path+" ");
									double value = CommonUtils.initBaseRecordInfo(valueObj, time);
									if(TypeEnum.INT.getName().equals(type)){
										sc.append((long)value+" ");
									}
									if(TypeEnum.FLOAT.getName().equals(type)){
										sc.append(value+" ");
									}
									sc.append(time.getCurrentTime()/1000+"\n");//FIXME Graphite 不支持毫秒
									time.addStep();
								}
								Date endDate=new Date(time.getCurrentTime());
								if(time.getCurrentTime()-System.currentTimeMillis()>1000){//大于一秒情况下开始睡觉
									Thread.currentThread().sleep(time.getCurrentTime()-System.currentTimeMillis());
								}
								try {
									pw.write(sc.toString());
									pw.flush();
									logger.info(sdf.format(startDate)+" to "+sdf.format(endDate)+"["+path+"] data insert success");
								} catch (Exception e) {
									logger.error(sdf.format(startDate)+" to "+sdf.format(endDate)+"["+path+"] data insert failed", e);
								}
							}
							logger.warn("path["+path+"]data insert finished");
						} catch (Exception e) {
							logger.error("",e);
						}
						finally{
							socket.shutdownOutput();
							pw.close();
							os.close();
							socket.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
