package com.ruc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
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
import com.ruc.model.XmlNodeAttr;

public class testMain {
	public static final ExecutorService TEST_THREADS = Executors.newFixedThreadPool(10);  
	/**四线程的
	 * 算法:
	 * 分为四个文件，写完后合并个文件
	 * */
	public static void main(String[] args) throws Exception {
		long startTime=System.currentTimeMillis();
		String path = testMain.class.getClassLoader().getResource("").getPath();
		readXml(path+"/conf.xml");
		long endTime=System.currentTimeMillis();
		System.out.println("generator cost ["+(endTime-startTime)+"]ms");
		System.out.println("write finished!");
	}
	/**
	 * @see http://www.runoob.com/xpath/xpath-syntax.html (xpath 语法以及解析) 
	 * @param path
	 */
	public static void readXml(String path){
		try {
			Document document = getDomDocument(path);
//			Long generateCount=getGenerateCount(document);
			initGenerateType(document);
			getGenerateCount(document);
			initGenerateDate(document);
			initLineSeparator(document);//初始化分隔符
			String csvPath=getCsvPath(document);
			final List<Element> records = document.selectNodes("/"+XmlNodeNameEnum.RECORDS.getName()+"/"+XmlNodeNameEnum.RECORD.getName());
			final int all=8;//四个线程
			final String[] tempPaths=new String[all];
			final String defaultPath=testMain.class.getClassLoader().getResource("").getPath()+"/bigdata_"+System.currentTimeMillis()+"_";
			//写文件
//			for(Element r:records){
//				String active = r.attributeValue("active");
//				if("true".equals(active)){
//					final Element record=r;
						for(int seq=0;seq<all;seq++){
							final int seq1=seq;
							TEST_THREADS.execute(new Runnable() {
								@Override
								public void run() {
									try {
										tempPaths[seq1]=defaultPath+seq1;
										final File file = new File(tempPaths[seq1]);
										if (file.exists()){
											file.delete();
										}
										FileWriter fw = new FileWriter(file,true);
//										List<Element> columns = record.selectNodes(XmlNodeNameEnum.COLUMN.getName());
//										if(columns==null||columns.size()==0){
//											CommonUtils.stopThreadAndPrint("column的数目为0，请核实后再运行程序");
//										}
										if(ConstantManager.GENERATE_TYPE.equals(GenerateType.DATE.getValue())){
											int i=0;
											long step=(ConstantManager.END_TIME-ConstantManager.START_TIME)/all;
											for(long start=ConstantManager.START_TIME+step*seq1;start<ConstantManager.START_TIME+step*(seq1+1);start+=ConstantManager.TIME_STEP){
												for(Element r:records){
													String active = r.attributeValue("active");
													if("true".equals(active)){
														final Element record=r;
														List<Element> columns = record.selectNodes(XmlNodeNameEnum.COLUMN.getName());
														if(columns==null||columns.size()==0){
															CommonUtils.stopThreadAndPrint("column的数目为0，请核实后再运行程序");
														}
														StringBuilder sc=new StringBuilder();
														for(int j=0;j<columns.size();j++){
															Element column=columns.get(j);
															initBaseRecordInfo(sc, column,start);
															if(j==columns.size()-1){
																sc.append("\r\n");
															}else{
																sc.append(ConstantManager.LINE_SEPARATOR);
															}
														}
														fw.write(sc.toString());
													}
													i++;
													if((i+1)%10000==0){
														System.out.println("seq:"+seq1+" generate line count:["+(i+1)+"]");
													}
												}
											}
										}
										fw.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
						}
//				}
//			}
			//2 合并文件 监听线程，都执行完了，进行文件合并
			TEST_THREADS.shutdown();
			while(true){
				if(TEST_THREADS.isTerminated()){
					long start=System.currentTimeMillis();
					CommonUtils.mergeFile("E://1-data/"+System.currentTimeMillis()+"_merge.txt",tempPaths);
					long end=System.currentTimeMillis();
					System.out.println("合并消耗时间["+(end-start)+"]ms");
					break;
				}
				Thread.currentThread().sleep(200L);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}   
	}
	/**
	 * 初始化类型为key-value节点数据
	 * @param attr
	 * @return
	 */
	public static String initKeyValueNode(Element column,long num) {
		Node key = column.selectSingleNode("key");
		if(key==null){
			CommonUtils.stopThreadAndPrint("type为key-value的column下没有key标签");
		}
		StringBuilder sc=new StringBuilder();
		initBaseRecordInfo(sc,(Element)key,num);
		sc.append("=");
		Node value = column.selectSingleNode("value");
		if(value==null){
			CommonUtils.stopThreadAndPrint("type为key-value的column下没有value标签");
		}
		initBaseRecordInfo(sc,(Element)value,num);
		return sc.toString();
	}
	/**
	 * 初始化每一条记录的基本信息
	 * 核心方法
	 * 
	 * TODO 函数可以优化为,传入当前行数，总行数，以及函数参数，直接算出来值 然后把该方法中计算的部分抽象出来，便于维护
	 * @param sc
	 * @param column
	 * @return
	 */
	public static StringBuilder initBaseRecordInfo(StringBuilder sc, Element column,Long num) {
		XmlNodeAttr attr=new XmlNodeAttr(column);
		String type = attr.getType();
		String functionType=attr.getFunctionType();
		if(TypeEnum.STRING.getName().equals(type)){
			sc.append(attr.getValue());
		}
//		if(TypeEnum.INT.getName().equals(type)){
//			long value=0;
//			if(StringUtils.isNotBlank(attr.getValue())){
//				value=Long.parseLong(attr.getValue());
//			}else{
//				if(FunctionTypeEnum.RANDOM.getName().equals(functionType)){
//					value = CommonUtils.generateRandomInt(attr.getMin(), attr.getMax());
//				}
//				if(FunctionTypeEnum.BROKEN_LINE.getName().equals(functionType)){//破折线
//						value=(long) CommonUtils.getBrokenLine(200,8,10000);
////					}
//				}
//				if(FunctionTypeEnum.SQUARE_LINE.getName().equals(functionType)){//方波
//					value=(long) CommonUtils.getSquareLine(200, 500, 10000);
//				}
//				if(FunctionTypeEnum.MONO_RISE.getName().equals(functionType)){//单调增，斜率唯一函数
//					value=(long) CommonUtils.getMonoLine(3, 300);
//				}
//				if(FunctionTypeEnum.MONO_DECREASE.getName().equals(functionType)){//单调减，斜率唯一函数
//					value=(long) CommonUtils.getMonoLine(-5,10000);
//				}
//				if(FunctionTypeEnum.SINE_LINE.getName().equals(functionType)){//正弦函数
//					value=(long) CommonUtils.getSineLine(500,500000,100,500);
//				}
//			}
//			sc.append(value);
//		}
//		if(TypeEnum.FLOAT.getName().equals(type)){
//			double value=0;
//			if(StringUtils.isNotBlank(attr.getValue())){
//				value=Long.parseLong(attr.getValue());
//			}else{
//				if(FunctionTypeEnum.RANDOM.getName().equals(functionType)){
//					value=CommonUtils.generateRandomFloat(attr.getMin(), attr.getMax());
//				}
//				if(FunctionTypeEnum.BROKEN_LINE.getName().equals(functionType)){//破折线
//					//value=k*长*斜率
//					value=CommonUtils.getBrokenLine(200,8,10000);
//				}
//				if(FunctionTypeEnum.SQUARE_LINE.getName().equals(functionType)){//方波
//					value= CommonUtils.getSquareLine(200, 698.56, 99584.8);
//				}
//				if(FunctionTypeEnum.MONO_RISE.getName().equals(functionType)){//单调增，斜率唯一函数
//					value=CommonUtils.getMonoLine(2.85, 10);
//				}
//				if(FunctionTypeEnum.MONO_DECREASE.getName().equals(functionType)){//单调减，斜率唯一函数
//					value=CommonUtils.getMonoLine(-5.6,10000);
//				}
//				if(FunctionTypeEnum.SINE_LINE.getName().equals(functionType)){//正弦函数
//					value=CommonUtils.getSineLine(500,500000,100,500);
//				}
//			}
//			sc.append(value);
//		}
		if(TypeEnum.TIMESTAMP.getName().equals(type)){
			if(ConstantManager.GENERATE_TYPE.equals(GenerateType.LINE.getValue())){
				sc.append(CommonUtils.generateSysTimestamp());
			}
			if(ConstantManager.GENERATE_TYPE.equals(GenerateType.DATE.getValue())){
				sc.append(num);
			}
		}
		if(TypeEnum.KEY_VALUE.getName().equals(type)){
			String keyValue= initKeyValueNode(column,num);
			sc.append(keyValue);
		}
		return sc;
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
		String endDateStr=((Element )node).attributeValue(XmlNodeAttrEnum.END_DATE.getName(),"1970-01-01 00:16:40");
		String timeStepStr = ((Element )node).attributeValue(XmlNodeAttrEnum.STEP.getName(),"1");
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
		String defaultPath=testMain.class.getClassLoader().getResource("").getPath()+"/bigdata_"+System.currentTimeMillis()+".csv";
		if(node==null){
			return defaultPath;
		}else{
			Element ele=(Element)node;
			String path = ele.attributeValue(XmlNodeAttrEnum.VALUE.getName(),defaultPath);
			return path;
		}
	}
	
}

