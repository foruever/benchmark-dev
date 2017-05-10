package com.ruc.crud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import cn.edu.thu.tsfile.common.utils.RandomAccessOutputStream;
import cn.edu.thu.tsfile.common.utils.TSRandomAccessFileWriter;
import cn.edu.thu.tsfile.timeseries.FileFormat.TsFile;

import com.alibaba.fastjson.JSON;
import com.ruc.CommonUtils;
import com.ruc.constant.TypeEnum;
import com.ruc.model.Config;
import com.ruc.model.RecordTime;
import com.ruc.model.tsfile.TsfileRecord;
import com.ruc.model.tsfile.TsfileSensor;
import com.ruc.model.tsfile.TsfileValue;

public class TsFileDao {
	public static void main(String[] args) {
		try {
			Long startTime= System.currentTimeMillis();
			String path = "test.ts";
			String s = "{\n" +
	                "    \"schema\": [\n" +
	                "        {\n" +
	                "            \"measurement_id\": \"sensor_1\",\n" +
	                "            \"data_type\": \"FLOAT\",\n" +
	                "            \"encoding\": \"RLE\"\n" +
	                "        },\n" +
	                "        {\n" +
	                "            \"measurement_id\": \"sensor_2\",\n" +
	                "            \"data_type\": \"INT32\",\n" +
	                "            \"encoding\": \"TS_2DIFF\"\n" +
	                "        },\n" +
	                "        {\n" +
	                "            \"measurement_id\": \"sensor_3\",\n" +
	                "            \"data_type\": \"INT32\",\n" +
	                "            \"encoding\": \"TS_2DIFF\"\n" +
	                "        }\n" +
	                "    ],\n" +
	                "    \"row_group_size\": 134217728\n" +
	                "}";
			System.out.println(s);
			TSRandomAccessFileWriter writer =new RandomAccessOutputStream(new File("f://ts.ts"));
//			TSRandomAccessFileWriter writer =new RandomAccessOutputStream(new File("f://ts.ts"),"rws");
			JSONObject jo=new JSONObject(s);
			File sourceFile=new File("f://bigdata.txt");
			FileInputStream fis =new FileInputStream(sourceFile);
			InputStreamReader reader=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(reader);
			int count=0;
			String line=null;
			TsFile tsFile = new TsFile(writer,jo);
			while((line=br.readLine())!=null){
				count++;
				tsFile.writeLine(line);
			}
			System.out.println(count);
			Long endTime= System.currentTimeMillis();
			System.out.println(endTime-startTime);
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+", sensor_1, 1.2, sensor_2, 20, sensor_3,"+"\n device_1,"+System.currentTimeMillis()+", sensor_1, , sensor_2, 28, sensor_3, 56");
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+", sensor_1, , sensor_2, 28, sensor_3, 56");
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+", sensor_1, 1.4, sensor_2, 21, sensor_3,");
//			tsFile.writeLine("device_1,"+System.currentTimeMillis()+",sensor_1,1.2,sensor_2,20,sensor_4,9999,sensor_3,51");
//			TSRecord tr=new TSRecord(System.currentTimeMillis(), "device_1");
//			tsFile.writeLine(tr);
			tsFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void onlineInsert(Config config){
		List<TsfileRecord> tsfiles = config.getTsfiles();
		for(final TsfileRecord record:tsfiles){
			CommonUtils.ONLINE_THREADS.execute(new Runnable() {
				@Override
				public void run() {
					long endTime=CommonUtils.data2timestamp(record.getEndTime());
					RecordTime time=new RecordTime(endTime,record.getStep(), record.getCacheLine());
					String tsFilePath = record.getTsFilePath();
					String deltaObjectId = record.getDeltaObjectId();
					String scheme=initSchemeByRecord(record);
					System.out.println(scheme);
//					int BV=1/0;
					while(time.getCurrentTime()<=time.getEndTime()){//当当前时间小于结束时间时，继续生成
						List<String> list=new ArrayList<String>();
						for(int i=0;i<record.getCacheLine();i++){
							StringBuilder sc=new StringBuilder();
							sc.append(deltaObjectId+","+time.getCurrentTime()+",");
							List<TsfileSensor> sensors = record.getSensors();
							for(int j=0;j<sensors.size();j++){
								TsfileSensor sensor=sensors.get(j);
								sc.append(sensor.getKey()+",");
								TsfileValue valueObj = sensor.getValue();
								String type = valueObj.getType();
								double value = CommonUtils.initBaseRecordInfo(valueObj, time);
								if(TypeEnum.INT.getName().equals(type)){
									sc.append((long)value);
								}
								if(TypeEnum.FLOAT.getName().equals(type)){
									sc.append(value);
								}
								if(j<sensors.size()-1){
									sc.append(",");
								}
							}
//							System.out.println(sc.toString());
							time.addStep();
							list.add(sc.toString());
						}
						insertToTsFileByOnline(list, tsFilePath, scheme);
					}
				}

			});
		}
	}
	/**
	 * 获取scheme
	 * @param record
	 * @return
	 */
	private static String initSchemeByRecord(TsfileRecord record) {
		Map<String,Object> map=new HashMap<String,Object>();
		List<TsfileSensor> sensors = record.getSensors();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		for(TsfileSensor sensor:sensors){
			Map<String,Object> schemeMap=new HashMap<String, Object>();
			schemeMap.put("measurement_id", sensor.getKey());
			String type=sensor.getValue().getType();
			if(TypeEnum.INT.getName().equals(type)){
				schemeMap.put("data_type","INT64");
			}
			if(TypeEnum.FLOAT.getName().equals(type)){
				schemeMap.put("data_type","DOUBLE");
			}
			
			schemeMap.put("encoding", "PLAIN");
			list.add(schemeMap);
		}
		map.put("schema",list);
		map.put("row_group_size",134217728);
		return JSON.toJSONString(map);
	}
	/**
	 * @see 原生的对象封装麻烦，不用原生的 直接字符串，让程序自己转
	 * @param sourceList 缓存数据
	 * @param tsFilePath tsFile路径
	 * @param scheme 数据scheme格式
	 */
	private static void insertToTsFileByOnline(List<String> sourceList,String tsFilePath,String scheme){
		if(sourceList==null){
			return;
		}
		JSONObject jo=new JSONObject(scheme);
		try {
			TSRandomAccessFileWriter writer =new RandomAccessOutputStream(new File(tsFilePath));
			TsFile tsFile = new TsFile(writer,jo);
			for(String source:sourceList){
				tsFile.writeLine(source);
			}
			tsFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	/**
	 * offLine 调用生成导入数据 以及简单评测
	 * @param sourcePath 源数据路径
	 * @param tsFilePath tsFile路径
	 * @param schemePath tsFile的scheme路径
	 */
	private static void insertToTsFileByOffLine(String sourcePath,String tsFilePath,String schemePath){
		if(StringUtils.isBlank(sourcePath)||StringUtils.isBlank(tsFilePath)||StringUtils.isBlank(schemePath)){
			return;
		}
		try {
			Long startTime= System.currentTimeMillis();
			BufferedReader schemeReader=new BufferedReader(new InputStreamReader(new FileInputStream(new File(schemePath))));
			StringBuffer sc=new StringBuffer();
			String schemeTemp="";
			while((schemeTemp=schemeReader.readLine())!=null){
				sc.append(schemeTemp);
			}
			JSONObject jo=new JSONObject(sc.toString());//JO
			schemeReader.close();
			
			TSRandomAccessFileWriter writer =new RandomAccessOutputStream(new File(tsFilePath));//WRITER
			TsFile tsFile = new TsFile(writer,jo);
			
			File sourceFile=new File(sourcePath);
			FileInputStream fis =new FileInputStream(sourceFile);
			InputStreamReader reader=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(reader);
			int count=0;
			String line=null;
			while((line=br.readLine())!=null){
				count++;
				tsFile.writeLine(line);
			}
			br.close();
			Long endTime= System.currentTimeMillis();
			System.out.println("total import "+count+" lines");
			System.out.println("total cost "+ (endTime-startTime)+" ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
