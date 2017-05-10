package com.ruc.crud;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.alibaba.fastjson.JSON;
import com.ruc.CommonUtils;
import com.ruc.constant.ConstantManager;
import com.ruc.constant.TypeEnum;
import com.ruc.httpapi.opentsdb.ExpectResponse;
import com.ruc.httpapi.opentsdb.HttpClient;
import com.ruc.httpapi.opentsdb.HttpClientImpl;
import com.ruc.httpapi.opentsdb.builder.Metric;
import com.ruc.httpapi.opentsdb.builder.MetricBuilder;
import com.ruc.httpapi.opentsdb.response.Response;
import com.ruc.model.Config;
import com.ruc.model.RecordTime;
import com.ruc.model.opentsdb.OpentsdbRecord;
import com.ruc.model.opentsdb.OpentsdbTag;
import com.ruc.model.opentsdb.OpentsdbValue;

/**
 * openTsdb操作类
 * @author sxg
 *https://github.com/shifeng258/opentsdb-client.git
 */
public class OpenTSDBDao {
	private static Logger logger=Logger.getLogger(OpenTSDBDao.class);
	public static void main(String[] args) throws Exception {
		Long startTime=System.currentTimeMillis();
		HttpClient client = new HttpClientImpl("http://192.168.254.132:4242");
		MetricBuilder builder = MetricBuilder.getInstance();
		for(int i=0;i<1000000;i++){
//			Thread.currentThread().sleep(1L);
			builder.addMetric("mymertric.test.data").setDataPoint(i+1,CommonUtils.getSineLine(1000*1000L, 100, 0, 150,new RecordTime()))
			.addTag("tag1", "tab1value").addTag("tag2", "tab2value");
		}
		int size = builder.getMetrics().size();
		Long endTime=System.currentTimeMillis();
		System.out.println("共产生["+size+"]条，消耗时间["+(endTime-startTime)+"]ms");
//		ConstantManager.START_TIME=System.currentTimeMillis();
//		ConstantManager.END_TIME=System.currentTimeMillis()+1000*3600L;
//		ConstantManager.CURRENT_NUM=ConstantManager.START_TIME;
//		for(;ConstantManager.CURRENT_NUM<ConstantManager.END_TIME;ConstantManager.CURRENT_NUM+=1000){
////			Thread.currentThread().sleep(900L);
//			MetricBuilder builder = MetricBuilder.getInstance();
//			for(int i=0;i<1000000;i++){
//				Thread.currentThread().sleep(1L);
//				builder.addMetric("mymertric.test.data").setDataPoint(ConstantManager.CURRENT_NUM+i,CommonUtils.getSineLine(1000*1000L, 100, 0, 150,new RecordTime()))
//				.addTag("tag1", "tab1value").addTag("tag2", "tab2value");
//			}
//			int size = builder.getMetrics().size();
//			Long endTime=System.currentTimeMillis();
//			System.out.println("共产生["+size+"]条，消耗时间["+(endTime-startTime)+"]ms");
//			try {
////				System.out.println(JSON.toJSONString(builder.getMetrics()));
////				Response response = client.pushMetrics(builder,
////						ExpectResponse.DETAIL);
////				System.out.println(response);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}


	}
	public static void onlineInsert(Config config){
		final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<OpentsdbRecord> opentsdbs = config.getOpentsdbs();
		if(opentsdbs.size()==0){
			return ;
		}
		final String serverUrl=config.getServerUrl();
		final HttpClient client = new HttpClientImpl(serverUrl);
		for(final OpentsdbRecord record:opentsdbs){
			CommonUtils.ONLINE_THREADS.execute(new Runnable() {
				@Override
				public void run() {
					long endTime=CommonUtils.data2timestamp(record.getEndTime());
					RecordTime time=new RecordTime(endTime,record.getStep(), record.getCacheLine());
					String metric = record.getMetric();
					final OpentsdbValue valueObj = record.getValue();
					String type = valueObj.getType();
					long cacheMs=time.getTimestep()*time.getCacheLine();
					while(time.getCurrentTime()<=time.getEndTime()){//当当前时间小于结束时间时，继续生成
						MetricBuilder builder = MetricBuilder.getInstance();
						Date startDate=new Date(time.getCurrentTime());
						for(int i=0;i<record.getCacheLine();i++){
							if(time.getCurrentTime()>time.getEndTime()){
								break;
							}
							Metric point=null;
							double valueDouble = CommonUtils.initBaseRecordInfo(valueObj, time);
							if(TypeEnum.INT.getName().equals(type)){
								point = builder.addMetric(metric).setDataPoint(time.getCurrentTime(),(long)valueDouble);
							}
							if(TypeEnum.FLOAT.getName().equals(type)){
								point=builder.addMetric(metric).setDataPoint(time.getCurrentTime(),valueDouble);
							}
							time.addStep();
							if(point!=null){
								List<OpentsdbTag> tags = record.getTags();
								if(tags.size()!=0){
									for(OpentsdbTag tag:tags){
										point.addTag(tag.getKey(),tag.getValue());
									}
								}
							}
						}
						Date endDate=new Date(time.getCurrentTime());
						try {
							if(time.getCurrentTime()-System.currentTimeMillis()>1000){//大于一秒情况下开始睡觉
								Thread.currentThread().sleep(time.getCurrentTime()-System.currentTimeMillis());
							}
							Response response = client.pushMetrics(builder,ExpectResponse.DETAIL);
							if(response.getStatusCode()==200){
								logger.info(sdf.format(startDate)+" to "+sdf.format(endDate)+"["+metric+"] data insert success");
							}else{
								logger.error(response);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
//					System.out.println("["+metric+"]数据插入完毕");
					logger.warn("metric["+metric+"]data insert finished");
				}
			});
		}
		CommonUtils.ONLINE_THREADS.shutdown();
		while(true){
			if(CommonUtils.ONLINE_THREADS.isTerminated()){
				logger.warn("all finished");
				break;
			}
			try {
				Thread.currentThread().sleep(200L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
