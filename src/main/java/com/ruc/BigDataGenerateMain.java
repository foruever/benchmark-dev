package com.ruc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;

import com.ruc.constant.ConstantManager;
import com.ruc.constant.FunctionTypeEnum;
import com.ruc.constant.GenerateType;
import com.ruc.constant.TypeEnum;
import com.ruc.constant.XmlNodeAttrEnum;
import com.ruc.constant.XmlNodeNameEnum;
import com.ruc.crud.GraphiteDao;
import com.ruc.crud.InfluxDBDao;
import com.ruc.crud.OpenTSDBDao;
import com.ruc.crud.TsFileDao;
import com.ruc.model.Config;
import com.ruc.model.RecordTime;
import com.ruc.model.XmlNodeAttr;


/**
 * 后期学习利用一下 git的分支(branch)，方便版本控制
 * @author sxg
 *
 */
public class BigDataGenerateMain {
	/**先做单线程的*/
	public static void main(String[] args) throws Exception {
		long startTime=System.currentTimeMillis();
		String path = BigDataGenerateMain.class.getClassLoader().getResource("").getPath();
		Document document = getDomDocument(path+"/conf.xml");
		initGenerateType(document);
		initLog4j(path);
		if(ConstantManager.GENERATE_TYPE.equals(GenerateType.DATE.getValue())||ConstantManager.GENERATE_TYPE.equals(GenerateType.LINE.getValue())){
			readXml(document);
			long endTime=System.currentTimeMillis();
			System.out.println("generator cost ["+(endTime-startTime)+"]ms");
			System.out.println("write finished!");
		}
		if(ConstantManager.GENERATE_TYPE.equals(GenerateType.ONLINE.getValue())){
			Config config = CommonUtils.initXml();
			String dbType=config.getDbType();
			if(StringUtils.isBlank(dbType)){
				CommonUtils.stopThreadAndPrint("dbType为空");
				return;
			}
			if("opentsdb".equals(dbType)){
				OpenTSDBDao.onlineInsert(config);
			}
			if("influxdb".equals(dbType)){
				InfluxDBDao.onlineInsert(config);
			}
			if("graphite".equals(dbType)){
				GraphiteDao.onlineInsert(config);
			}
			if("tsfile".equals(dbType)){
				TsFileDao.onlineInsert(config);
			}
		}
	}
	/**
	 * 初始化Log4j 
	 */
	private static void initLog4j(String path) {
		Properties props = new Properties();
		InputStream is;
		try {
			is = new FileInputStream(new File(path+"/log4j.properties"));
			props.load(is);
			is.close();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String format = sdf.format(new Date());
			String logFile = path + props.getProperty("log4j.appender.file.File")+format+".log";//设置路径
			props.setProperty("log4j.appender.file.File",logFile);
			PropertyConfigurator.configure(props);//装入log4j配置信息
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @see http://www.runoob.com/xpath/xpath-syntax.html (xpath 语法以及解析) 
	 * @param path
	 */
	public static void readXml(Document document){
		try {
//			Document document = getDomDocument(path);
//			Long generateCount=getGenerateCount(document);
//			initGenerateType(document);
			getGenerateCount(document);
			initGenerateDate(document);
			initLineSeparator(document);//初始化分隔符
			String csvPath=getCsvPath(document);
	        File file = new File(csvPath);
	        if (file.exists()){
	        	file.delete();
	        }
	        FileWriter fw = new FileWriter(file,true);
			List<Element> records = document.selectNodes("/"+XmlNodeNameEnum.RECORDS.getName()+"/"+XmlNodeNameEnum.RECORD.getName());
//			Element record=null;
//			int count=0;
//			for(Element r:records){
//				String active = r.attributeValue("active");
//				if("true".equals(active)){
//					count++;
//					if(count>1){
//						CommonUtils.stopThreadAndPrint("xml中,record的active属性的激活数目超过一个，请修正");
//					}
//					record=r;
//				}
//			}
//			if(count==0){
//				CommonUtils.stopThreadAndPrint("xml中,record的active属性的激活数目为0个，请修正");
//				Thread.currentThread().stop();
//			}
//			List<Element> columns = record.selectNodes(XmlNodeNameEnum.COLUMN.getName());
//			if(columns==null||columns.size()==0){
//				CommonUtils.stopThreadAndPrint("column的数目为0，请核实后再运行程序");
//			}
			if(ConstantManager.GENERATE_TYPE.equals(GenerateType.LINE.getValue())){
				RecordTime time=new RecordTime(ConstantManager.CURRENT_NUM, ConstantManager.END_TIME, ConstantManager.START_TIME, ConstantManager.TIME_STEP, 1000L);
				for(ConstantManager.CURRENT_NUM=0;ConstantManager.CURRENT_NUM<ConstantManager.GENERATE_LINE;ConstantManager.CURRENT_NUM++){
					for(Element record:records){
						String active = record.attributeValue("active");
						if("true".equals(active)){
							List<Element> columns = record.selectNodes(XmlNodeNameEnum.COLUMN.getName());
							StringBuilder sc=new StringBuilder();
							for(int j=0;j<columns.size();j++){
								Element column=columns.get(j);
								CommonUtils.initBaseRecordInfo(sc, column,time);
								if(j==columns.size()-1){
									sc.append("\r\n");
								}else{
									sc.append(ConstantManager.LINE_SEPARATOR);
								}
							}
							fw.write(sc.toString());
						}
					}
					if((ConstantManager.CURRENT_NUM+1)%100000==0){
						System.out.println("generate line count:["+(ConstantManager.CURRENT_NUM+1)+"]");
					}
				}
			}
			if(ConstantManager.GENERATE_TYPE.equals(GenerateType.DATE.getValue())){
				int i=0;
				RecordTime time=new RecordTime(ConstantManager.START_TIME, ConstantManager.END_TIME, ConstantManager.START_TIME, ConstantManager.TIME_STEP, 1000L);
				for(;time.getCurrentTime()<time.getEndTime();time.addStep()){
					for(Element record:records){
						String active = record.attributeValue("active");
						if("true".equals(active)){
							List<Element> columns = record.selectNodes(XmlNodeNameEnum.COLUMN.getName());
							StringBuilder sc=new StringBuilder();
							for(int j=0;j<columns.size();j++){
								Element column=columns.get(j);
								CommonUtils.initBaseRecordInfo(sc, column,time);
								if(j==columns.size()-1){
									sc.append("\r\n");
								}else{
									sc.append(ConstantManager.LINE_SEPARATOR);
								}
							}
							fw.write(sc.toString());
							i++;
							if((i+1)%100000==0){
								System.out.println("generate line count:["+(i+1)+"]");
							}
						}
					}
				}
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}

	/***
	 * 根据路径dom获取xml的document对象
	 * @param path xml所在路径
	 * @return
	 */
	public static Document getDomDocument(String path){
		try {
			DOMReader reader=new DOMReader();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
			DocumentBuilder builder = factory.newDocumentBuilder();  
			org.w3c.dom.Document parse = builder.parse(new File(path));
			Document document = reader.read(parse);
			return document;
		} catch (Exception e) {
			e.printStackTrace();
			CommonUtils.stopThreadAndPrint("读取路径为:["+path+"]的xml文档异常");
		}
		return null;
	}
	/**
	 * 根据Document获取生成数
	 * 根据生成方式 不同，生成数也不同
	 * 按照行数生成，直接设置行数
	 * 按照时间生成，根据结束时间和开始时间所差的毫秒数生成
	 */
	public static long getGenerateCount(Document doc){
		Node countNode = doc.selectSingleNode("/"+XmlNodeNameEnum.RECORDS.getName()+"/"+XmlNodeNameEnum.GENERATE_LINE.getName());
		if(countNode!=null){
			String number = ((Element)countNode).attributeValue(XmlNodeAttrEnum.VALUE.getName());
			try {
				ConstantManager.GENERATE_LINE=Long.parseLong(number);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				CommonUtils.stopThreadAndPrint("字符串转数字异常，generateLine标签->number属性不是数字["+number+"]");
			}
		}
		return ConstantManager.GENERATE_LINE;
	}
	/**
	 * 初始化生成方式
	 * @param document
	 */
	private static void initGenerateType(Document document) {
		Node node = document.selectSingleNode("/"+XmlNodeNameEnum.RECORDS.getName()+"/"+XmlNodeNameEnum.GENERATE_TYPE.getName());
		if(node==null){
			ConstantManager.GENERATE_TYPE=GenerateType.LINE.getValue();
			return;
		}
		String value = ((Element)node).attributeValue(XmlNodeAttrEnum.VALUE.getName());
		ConstantManager.GENERATE_TYPE=value;
	}
	/**
	 *默认当前时间 1970的第一天向后推100000条 
	 */
	private static void initGenerateDate(Document document) {
		Node node = document.selectSingleNode("/"+XmlNodeNameEnum.RECORDS.getName()+"/"+XmlNodeNameEnum.GENERATE_DATE.getName());
		if(node==null){
			ConstantManager.START_TIME=0L;
			ConstantManager.END_TIME=100000L;
			ConstantManager.TIME_STEP=1L;
		}
		String startDateStr=((Element )node).attributeValue(XmlNodeAttrEnum.START_DATE.getName(),"1970-01-01 00:00:00");
		String endDateStr=((Element )node).attributeValue(XmlNodeAttrEnum.END_DATE.getName(),"1970-01-02 00:00:00");
		String timeStepStr = ((Element )node).attributeValue(XmlNodeAttrEnum.STEP.getName(),"1000");
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 try {
			Date startDate = sdf.parse(startDateStr);
			Date endDate=sdf.parse(endDateStr);
			ConstantManager.START_TIME= startDate.getTime();
			ConstantManager.END_TIME=endDate.getTime();
			ConstantManager.TIME_STEP=Long.parseLong(timeStepStr);
		} catch (ParseException e) {
			CommonUtils.stopThreadAndPrint("startDate["+startDateStr+"],endDate["+endDateStr+"]的数据格式不满足[yyyy-MM-dd hh:mm:ss]");
		}
	}
	/**
	 * 根据Document获取分隔符，默认为逗号
	 * 
	 */
	public static String initLineSeparator(Document doc){
		Node separatorNode = doc.selectSingleNode("/"+XmlNodeNameEnum.RECORDS.getName()+"/"+XmlNodeNameEnum.LINE_SEPARATOR.getName());
		if(separatorNode!=null){
			String selaratorNode = ((Element)separatorNode).attributeValue(XmlNodeAttrEnum.VALUE.getName());
			try {
				ConstantManager.LINE_SEPARATOR=selaratorNode;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ConstantManager.LINE_SEPARATOR;
	}
	/**
	 * 获取column,key,value标签的generate-type
	 * @param node
	 * @return
	 */
	public static String getFuntionType(Node node){
		if(node==null){
			return null;
		}
		//不为column,key,value直接报日志
		if(!("column".equals(node.getName())||"key".equals(node.getName())||"value".equals(node.getName()))){
			System.out.println(node.getName()+"[错误]调用getGenerateType方法");
			return null;
		}
		Element ele= (Element) node;
		String functionType = ele.attributeValue(XmlNodeAttrEnum.FUNCTION_TYPE.getName(),FunctionTypeEnum.RANDOM.getName());//生成方式默认为 随机生成
		return functionType;
	}
	/**
	 * 获取csv导出路径 默认与jar包同目录路径
	 * @param document
	 * @return
	 */
	public static String getCsvPath(Document document) {
		Node node = document.selectSingleNode("/"+XmlNodeNameEnum.RECORDS.getName()+"/"+XmlNodeNameEnum.CSV_PATH.getName());
		String basePath = BigDataGenerateMain.class.getClassLoader().getResource("").getPath();
		File file=new File(basePath+"/data");
		if(!file.exists()){
			file.mkdirs();
		}
		String defaultPath=BigDataGenerateMain.class.getClassLoader().getResource("").getPath()+"/data/bigdata_"+System.currentTimeMillis()+".txt";
		if(node==null){
			ConstantManager.FILE_PATH=defaultPath;
			return defaultPath;
		}else{
			Element ele=(Element)node;
			String path = ele.attributeValue(XmlNodeAttrEnum.VALUE.getName(),defaultPath);
			ConstantManager.FILE_PATH=path;
			return path;
		}
	}
	
	
	
	
}
