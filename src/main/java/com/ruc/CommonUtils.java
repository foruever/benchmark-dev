package com.ruc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.dom4j.Element;
import org.dom4j.Node;

import com.ruc.constant.ConstantManager;
import com.ruc.constant.FunctionTypeEnum;
import com.ruc.constant.GenerateType;
import com.ruc.constant.TypeEnum;
import com.ruc.constant.XmlNodeAttrEnum;
import com.ruc.function.BrokenLineFunction;
import com.ruc.function.ExponLineFunction;
import com.ruc.function.LogLineFunction;
import com.ruc.function.MonoLineFunction;
import com.ruc.function.ParabolaLineFunction;
import com.ruc.function.SineLineFunction;
import com.ruc.function.SquareLineFunction;
import com.ruc.model.Config;
import com.ruc.model.RecordTime;
import com.ruc.model.Value;
import com.ruc.model.XmlNodeAttr;
import com.ruc.model.graphite.GraphiteRecord;
import com.ruc.model.graphite.GraphiteValue;
import com.ruc.model.influxdb.InfluxdbField;
import com.ruc.model.influxdb.InfluxdbRecord;
import com.ruc.model.influxdb.InfluxdbTag;
import com.ruc.model.influxdb.InfluxdbValue;
import com.ruc.model.opentsdb.OpentsdbRecord;
import com.ruc.model.opentsdb.OpentsdbTag;
import com.ruc.model.opentsdb.OpentsdbValue;
import com.ruc.model.tsfile.TsfileRecord;
import com.ruc.model.tsfile.TsfileSensor;
import com.ruc.model.tsfile.TsfileValue;

public class CommonUtils {
	
	public static final ExecutorService ONLINE_THREADS = Executors.newFixedThreadPool(20);  
	/**
	 * 停止程序，并打印错误日志
	 */
	public static void stopThreadAndPrint(String log){
		System.err.println(log);
		Thread.currentThread().stop();
	}
	/**
	 * 生成某个范围内的整数
	 * @see 包含最小数，不包含最大数
	 */
	public static int generateRandomInt(long minLong,long maxLong){
		if(minLong>maxLong){
			stopThreadAndPrint("生成随机数min["+minLong+"]大于了max["+maxLong+"]");
		}
		if(minLong<Integer.MIN_VALUE){
			stopThreadAndPrint("min["+minLong+"]整型的最小值，请修正");
		}
		if(maxLong>Integer.MAX_VALUE){
			stopThreadAndPrint("max["+maxLong+"]整型的最大值，请修正");
		}
		int min=(int)minLong;
		int max=(int)maxLong;
		int random=0;
//		for(int i=0;i<100000;i++){
		random=min+ConstantManager.RANDOM.nextInt(max-min);
//		}
		return random;
	}
	/**
	 * 生成浮点型随机数
	 * @param min
	 * @return
	 */
	public static double generateRandomFloat(long min,long max){
		double random=0;
		random=min+(max-min)*ConstantManager.RANDOM.nextDouble();
		return random;
	}
	/**
	 * 生成高精度时间戳，毫秒+序号的
	 * 毫秒+000001,毫秒+000002......
	 */
	public synchronized static long generateSysTimestamp(){
		long timestamp=System.currentTimeMillis();
		Integer count = ConstantManager.TIMESTAMP_MAP.get(timestamp);
		if(count==null){
			count=0;
		}
		count++;
		ConstantManager.TIMESTAMP_MAP.clear();
		ConstantManager.TIMESTAMP_MAP.put(timestamp, count);
		String[] prefixs={"00000","0000","000","00","0",""};
		String timestampStr=timestamp+prefixs[(count+"").length()-1]+count;
		timestamp=Long.parseLong(timestampStr);
		return timestamp;
	}
	
	/**
	 * 合并多个文件
	 */
	public static void mergeFile(String targPath,String[] files){
		FileChannel fc=null;
		File file=new File(targPath);
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
				System.out.println("合并完成");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
//	public static int max=0;
	
	
	/******
	 * =================================================================================
	 * ===================函数工具start===================================================
	 * =================================================================================
	 * ******/
	
	
	/**破折线函数对象*/
	private static BrokenLineFunction BROKEN_LINE;
	/**方波函数对象*/
	private static SquareLineFunction SQUARE_LINE;
	/**单调斜率唯一函数*/
	private static MonoLineFunction MONO_LINE;
	/**正弦函数*/
	private static SineLineFunction SINE_LINE;
	/**抛物线函数*/
	private static ParabolaLineFunction PARABOLA_LINE;
	/**log函数*/
	private static LogLineFunction LOG_LINE;
	/**指数函数*/
	private static ExponLineFunction EXPON_LINE;
	
	
	/**
	 * 破折线函数
	 * @param period
	 * @param min
	 * @param max
	 * @return
	 */
	public synchronized static double getBrokenLine(long period, double min, double max,RecordTime time){//FIXME 判空 需要方法重载
		if(BROKEN_LINE==null){
			BROKEN_LINE=new BrokenLineFunction(period, min, max);
		}else{
			BROKEN_LINE.setPeriod(period);
			BROKEN_LINE.setMin(min);
			BROKEN_LINE.setMax(max);
		}
//		double value=BROKEN_LINE.getMin()+BROKEN_LINE.getSlope()*(ConstantManager.CURRENT_NUM%BROKEN_LINE.getPeriod());
		double value=BROKEN_LINE.getMin()+BROKEN_LINE.getSlope()*(time.getCurrentTime()%BROKEN_LINE.getPeriod());
		return value;
	}
	/**
	 * 方波函数
	 * @param period 周期 
	 * @param min 最小值
	 * @param max 最大值
	 */
	public synchronized static double getSquareLine(long period, double min, double max,RecordTime time){//FIXME 判空 需要方法重载
		if(SQUARE_LINE==null){
			SQUARE_LINE=new SquareLineFunction(period, min, max);
		}else{
			SQUARE_LINE.setPeriod(period);
			SQUARE_LINE.setMin(min);
			SQUARE_LINE.setMax(max);
		}
		double value=0;
		if(time.getCurrentTime()/SQUARE_LINE.getPeriod()%2==0){//取min
			value=SQUARE_LINE.getMin();
		}else{//取max
			value=SQUARE_LINE.getMax();
		}
		return value;
	}
	
	/**
	 * 单调斜率唯一(默认周期1H)
	 * @param slope 斜率 大于0 单调增，小于0单调减 ==0唯一函数
	 * @param startValue 初始值
	 * @return
	 */
	public synchronized static double getMonoLine(double slope, double startValue,RecordTime time){//FIXME 判空 需要方法重载
		double value=getMonoLine(slope, startValue, ConstantManager.DEFAULT_PERIOD,time);
		return value;
	}
	/**
	 * 单调斜率唯一(带周期)
	 * @param slope 斜率 大于0 单调增，小于0单调减 ==0唯一函数
	 * @param startValue 初始值
	 * @param coeT 周期T  
	 * @return
	 */
	public synchronized static double getMonoLine(double slope, double startValue,double coeT,RecordTime time){//FIXME 判空 需要方法重载
		if(MONO_LINE==null){
			MONO_LINE=new MonoLineFunction(slope, startValue);
		}else{
			MONO_LINE.setSlope(slope);
			MONO_LINE.setStartValue(startValue);
		}
		Long relativeNum=time.getRaletiveTime();
//		double value=MONO_LINE.getSlope()*(relativeNum%coeT)+MONO_LINE.getStartValue();
		double value=MONO_LINE.getSlope()*(relativeNum%coeT)+MONO_LINE.getStartValue();
		return value;
	}
	/**
	 * 获取正弦函数值   正弦函数 Asin(w*t+C)+D  w=2*PI/T
	 * @param period 周期 T
	 * @param amplitude 振幅 A
	 * @param initialPhase 初相C
	 * @param offset 偏距 D
	 */
	public synchronized static double getSineLine(double period, double amplitude,
			double initialPhase, double offset,RecordTime time){//FIXME 判空 需要方法重载
		if(SINE_LINE==null){
			SINE_LINE=new SineLineFunction(period, amplitude, initialPhase, offset);
		}else{
			SINE_LINE.setPeriod(period);
			SINE_LINE.setAmplitude(amplitude);
			SINE_LINE.setOffset(offset);
			SINE_LINE.setInitialPhase(initialPhase);
		}
		double value=SINE_LINE.getAmplitude()
				*Math.sin(SINE_LINE.getAngularVelocity()*time.getCurrentTime()+SINE_LINE.getInitialPhase())
				+SINE_LINE.getOffset();
		return value;
	}
	/**
	 * 抛物线(默认周期1H)
	 */
	public synchronized static double getParabolaLine(double coeA, double coeB,RecordTime time){
		double value=getParabolaLine(coeA, coeB, ConstantManager.DEFAULT_PERIOD,time);
		return value;
	}
	/**
	 * 抛物线(带周期)
	 */
	public synchronized static double getParabolaLine(double coeA, double coeB,double coeT,RecordTime time){
		if(PARABOLA_LINE==null){
			PARABOLA_LINE=new ParabolaLineFunction(coeA, coeB);
		}else{
			PARABOLA_LINE.setCoeA(coeA);
			PARABOLA_LINE.setCoeB(coeB);
		}
		Long relativeNum=time.getRaletiveTime();
		double value=coeA*Math.pow(relativeNum%coeT, 2)+coeB;
		return value;
	}
	/**
	 * 对数函数(默认周期1H)
	 */
	public synchronized static double getLogLine(double coeA, double coeB,
			double coeC,RecordTime time){//FIXME 判空 需要方法重载
		double value=getLogLine(coeA, coeB, coeC, ConstantManager.DEFAULT_PERIOD,time);
		return value;
	}
	/**
	 * 对数函数(带周期)
	 */
	public synchronized static double getLogLine(double coeA, double coeB,
			double coeC,double coeT,RecordTime time){//FIXME 判空 需要方法重载
		if(LOG_LINE==null){
			LOG_LINE=new LogLineFunction(coeA, coeB, coeC);
		}else{
			LOG_LINE.setCoeA(coeA);
			LOG_LINE.setCoeB(coeB);
			LOG_LINE.setCoeC(coeC);
		}
		Long raletaveTime = time.getRaletiveTime();
		double value=coeA*Math.log(coeB*(raletaveTime%coeT)+1)+coeC;
		return value;
	}
	
	
	/**
	 * 指数函数(默认周期为1000*3600ms)
	 */
	public synchronized static double getExponLine(double coeA, double coeB,
			double coeC,RecordTime time){//FIXME 判空 需要方法重载
		double value=getExponLine(coeA, coeB, coeC,ConstantManager.DEFAULT_PERIOD,time);;
		return value;
	}
	/**
	 * 指数函数(带周期)
	 */
	public synchronized static double getExponLine(double coeA, double coeB,
			double coeC,double coeT,RecordTime time){//FIXME 判空 需要方法重载
		if(EXPON_LINE==null){
			EXPON_LINE=new ExponLineFunction(coeA, coeB,coeC);
		}else{
			EXPON_LINE.setCoeA(coeA);
			EXPON_LINE.setCoeA(coeB);
			EXPON_LINE.setCoeC(coeC);
		}
		Long raletaveTime = time.getRaletiveTime();
		double value=coeA*Math.exp(coeB*(raletaveTime%coeT))+coeC;
		return value;
	}
	
	/**
	 * 获取当前相对时间 ，当前绝对时间减去相对时间，统一处理，方便修改
	 */
//	private static Long getRaletaveTime(RecordTime time) {
//		Long relativeNum=time.getCurrentTime()-time.getStartTime();
//		return relativeNum;
//	}
	
	
	/**
	 * 初始化类型为key-value节点数据
	 * @param attr
	 * @return
	 */
	public static String initKeyValueNode(Element column,RecordTime time) {
		Node key = column.selectSingleNode("key");
		if(key==null){
			CommonUtils.stopThreadAndPrint("type为key-value的column下没有key标签");
		}
		StringBuilder sc=new StringBuilder();
		initBaseRecordInfo(sc,(Element)key,time);
		sc.append("=");
		Node value = column.selectSingleNode("value");
		if(value==null){
			CommonUtils.stopThreadAndPrint("type为key-value的column下没有value标签");
		}
		initBaseRecordInfo(sc,(Element)value,time);
		return sc.toString();
	}
	/**
	 * 
	 * 基于dom的xml解析 原生非实时生成
	 * 初始化每一条记录的基本信息
	 * 核心方法
	 * 
	 * TODO 函数可以优化为,传入当前行数，总行数，以及函数参数，直接算出来值 然后把该方法中计算的部分抽象出来，便于维护
	 * @param sc
	 * @param column
	 * @return
	 */
	public static StringBuilder initBaseRecordInfo(StringBuilder sc, Element column,RecordTime time) {
		XmlNodeAttr attr=new XmlNodeAttr(column);
		String type = attr.getType();
		String functionType=attr.getFunctionType();
		if(TypeEnum.STRING.getName().equals(type)){
			sc.append(attr.getValue());
		}
		String custom = column.attributeValue(XmlNodeAttrEnum.CUSTOM.getName()); 
		Boolean isCustom=false;
		double coeA=1000;
		double coeB=1000;
		double coeC=1000;
		double coeD=1000;
		double coeE=1000;
		double coeT=1000*1000L;
		if(StringUtils.isNoneBlank(custom)&&"true".equals(custom)){
			isCustom=true;
			String coeAStr = column.attributeValue(XmlNodeAttrEnum.COE_A.getName()); 
			String coeBStr = column.attributeValue(XmlNodeAttrEnum.COE_B.getName()); 
			String coeCStr = column.attributeValue(XmlNodeAttrEnum.COE_C.getName()); 
			String coeDStr = column.attributeValue(XmlNodeAttrEnum.COE_D.getName()); 
			String coeEStr = column.attributeValue(XmlNodeAttrEnum.COE_E.getName());
			String coeTStr = column.attributeValue(XmlNodeAttrEnum.COE_T.getName());
			if(StringUtils.isNotBlank(coeAStr)){
				coeA=Double.parseDouble(coeAStr);
			}
			if(StringUtils.isNotBlank(coeBStr)){
				coeB=Double.parseDouble(coeBStr);
			}
			if(StringUtils.isNotBlank(coeCStr)){
				coeC=Double.parseDouble(coeCStr);
			}
			if(StringUtils.isNotBlank(coeDStr)){
				coeD=Double.parseDouble(coeDStr);
			}
			if(StringUtils.isNotBlank(coeEStr)){
				coeE=Double.parseDouble(coeEStr);
			}
			if(StringUtils.isNotBlank(coeTStr)){
				coeT=Double.parseDouble(coeTStr);
			}
		}
		if(TypeEnum.INT.getName().equals(type)){
			long value=0;
			if(StringUtils.isNotBlank(attr.getValue())){
				value=Long.parseLong(attr.getValue());
			}else{
				if(FunctionTypeEnum.RANDOM.getName().equals(functionType)){
					value = CommonUtils.generateRandomInt(attr.getMin(), attr.getMax());
				}
				if(FunctionTypeEnum.BROKEN_LINE.getName().equals(functionType)){//破折线
					if(isCustom){
						value=(long) CommonUtils.getBrokenLine((long)coeA,coeB,coeC,time);
					}else{
						value=(long) CommonUtils.getBrokenLine(200,8,10000,time);
					}
				}
				if(FunctionTypeEnum.SQUARE_LINE.getName().equals(functionType)){//方波
					if(isCustom){
						value=(long) CommonUtils.getSquareLine((long)coeA, coeB, coeC,time);
					}else{
						value=(long) CommonUtils.getSquareLine(200, 500, 10000,time);
					}
				}
				if(FunctionTypeEnum.MONO_RISE.getName().equals(functionType)){//单调增，斜率唯一函数
					if(isCustom){
						value=(long) CommonUtils.getMonoLine(coeA, coeB,time);
					}else{
						value=(long) CommonUtils.getMonoLine(3, 300,time);
					}
				}
				if(FunctionTypeEnum.MONO_DECREASE.getName().equals(functionType)){//单调减，斜率唯一函数
					if(isCustom){
						value=(long) CommonUtils.getMonoLine(coeA, coeB,time);
					}else{
						value=(long) CommonUtils.getMonoLine(-5,10000,time);
					}
				}
				if(FunctionTypeEnum.SINE_LINE.getName().equals(functionType)){//正弦函数
					if(isCustom){
						value=(long) CommonUtils.getSineLine( 2*Math.PI/coeB,coeA,coeC, coeD,time);
					}else{
						value=(long) CommonUtils.getSineLine(1000*60*10,500000,100,500000,time);
					}
				}
				if(FunctionTypeEnum.PARABOLA_UP_LINE.getName().equals(functionType)){//抛物线开口向上
					if(isCustom){
						value=(long) CommonUtils.getParabolaLine(coeA, coeB,time);
					}else{
						value=(long) CommonUtils.getParabolaLine(500,500000,time);
					}
				}
				if(FunctionTypeEnum.PARABOLA_DOWN_LINE.getName().equals(functionType)){//抛物线开口向下
					if(isCustom){
						value=(long) CommonUtils.getParabolaLine(coeA, coeB,time);
					}else{
						value=(long) CommonUtils.getParabolaLine(-500,500000,time);
					}
				}
				if(FunctionTypeEnum.LOG_LINE.getName().equals(functionType)){//log函数
					if(isCustom){
						value=(long) CommonUtils.getLogLine(coeA, coeB,coeC,time);
					}else{
						value=(long) CommonUtils.getLogLine(500,200,100,time);
					}
				}
				if(FunctionTypeEnum.EXPON_LINE.getName().equals(functionType)){//指数函数
					if(isCustom){
						value=(long) CommonUtils.getExponLine(coeA, coeB,coeC,time);
					}else{
						value=(long) CommonUtils.getExponLine(500,0.001,100,time);
					}
				}
			}
			sc.append((long)getAbnormalPoint(value)+"");
		}
		if(TypeEnum.FLOAT.getName().equals(type)){
			double value=0;
			if(StringUtils.isNotBlank(attr.getValue())){
				value=Long.parseLong(attr.getValue());
			}else{
				if(FunctionTypeEnum.RANDOM.getName().equals(functionType)){
					value = CommonUtils.generateRandomInt(attr.getMin(), attr.getMax());
				}
				if(FunctionTypeEnum.BROKEN_LINE.getName().equals(functionType)){//破折线
					if(isCustom){
						value=CommonUtils.getBrokenLine((long)coeA,coeB,coeC,time);
					}else{
						value=CommonUtils.getBrokenLine(200,8,10000,time);
					}
				}
				if(FunctionTypeEnum.SQUARE_LINE.getName().equals(functionType)){//方波
					if(isCustom){
						value=CommonUtils.getSquareLine((long)coeA, coeB, coeC,time);
					}else{
						value=CommonUtils.getSquareLine(200, 500, 10000,time);
					}
				}
				if(FunctionTypeEnum.MONO_RISE.getName().equals(functionType)){//单调增，斜率唯一函数
					if(isCustom){
						value=CommonUtils.getMonoLine(coeA, coeB,time);
					}else{
						value=CommonUtils.getMonoLine(3, 300,time);
					}
				}
				if(FunctionTypeEnum.MONO_DECREASE.getName().equals(functionType)){//单调减，斜率唯一函数
					if(isCustom){
						value=CommonUtils.getMonoLine(coeA, coeB,time);
					}else{
						value=CommonUtils.getMonoLine(-5,10000,time);
					}
				}
				if(FunctionTypeEnum.SINE_LINE.getName().equals(functionType)){//正弦函数
					if(isCustom){
						value=CommonUtils.getSineLine(2*Math.PI/coeB,coeA, coeC, coeD,time);
					}else{
						value=CommonUtils.getSineLine(1000*60*10,500000,100,500000,time);
					}
				}
				if(FunctionTypeEnum.PARABOLA_UP_LINE.getName().equals(functionType)){//抛物线开口向上
					if(isCustom){
						value=CommonUtils.getParabolaLine(coeA, coeB,time);
					}else{
						value=CommonUtils.getParabolaLine(500,500000,time);
					}
				}
				if(FunctionTypeEnum.PARABOLA_DOWN_LINE.getName().equals(functionType)){//抛物线开口向下
					if(isCustom){
						value=CommonUtils.getParabolaLine(coeA, coeB,time);
					}else{
						value=CommonUtils.getParabolaLine(-500,500000,time);
					}
				}
				if(FunctionTypeEnum.LOG_LINE.getName().equals(functionType)){//log函数
					if(isCustom){
						value=CommonUtils.getLogLine(coeA, coeB,coeC,time);
					}else{
						value=CommonUtils.getLogLine(500,200,100,time);
					}
				}
				if(FunctionTypeEnum.EXPON_LINE.getName().equals(functionType)){//指数函数
					if(isCustom){
						value=CommonUtils.getExponLine(coeA, coeB,coeC,time);
					}else{
						value=CommonUtils.getExponLine(500,0.001,100,time);
					}
				}
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
//				if(FunctionTypeEnum.PARABOLA_UP_LINE.getName().equals(functionType)){//抛物线开口向上
//					value=CommonUtils.getParabolaLine(500,500000);
//				}
//				if(FunctionTypeEnum.PARABOLA_DOWN_LINE.getName().equals(functionType)){//抛物线开口向下
//					value=CommonUtils.getParabolaLine(-500,500000);
//				}
//				if(FunctionTypeEnum.LOG_LINE.getName().equals(functionType)){//log函数
//					value=CommonUtils.getLogLine(500,200,100);
//				}
//				if(FunctionTypeEnum.EXPON_LINE.getName().equals(functionType)){//指数函数
//					value=CommonUtils.getExponLine(500,0.001,100);
//				}
			}
			sc.append(FormatUtils.DoubleFormat(getAbnormalPoint(value)));
		}
		if(TypeEnum.TIMESTAMP.getName().equals(type)){
			if(ConstantManager.GENERATE_TYPE.equals(GenerateType.LINE.getValue())){
				sc.append(CommonUtils.generateSysTimestamp());
			}
			if(ConstantManager.GENERATE_TYPE.equals(GenerateType.DATE.getValue())){
				sc.append(time.getCurrentTime());
			}
		}
		if(TypeEnum.KEY_VALUE.getName().equals(type)){
			String keyValue= initKeyValueNode(column,time);
			sc.append(keyValue);
		}
		return sc;
	}
	
	/**
	 * 
	 * 基于sax的xml解析 实时生成
	 * 核心方法
	 * 
	 * @param sc
	 * @param column
	 * @return
	 */
	public static double initBaseRecordInfo(Value valueObj,RecordTime time) {
		//checkValue FIXME
		//chekeTime FIXME 
		double coeA = valueObj.getCoeA();
		double coeB = valueObj.getCoeB();
		double coeC = valueObj.getCoeC();
		double coeD = valueObj.getCoeD();
		double coeE = valueObj.getCoeE();
		double coeT = valueObj.getCoeT();
		String functionType = valueObj.getFunctionType();
//		String type = valueObj.getType();
		double value=0;
		if(FunctionTypeEnum.RANDOM.getName().equals(functionType)){
			value = CommonUtils.generateRandomFloat((long)coeA,(long) coeB);
		}
		if(FunctionTypeEnum.BROKEN_LINE.getName().equals(functionType)){//破折线
			value= CommonUtils.getBrokenLine((long)coeT,coeA,coeB,time);
		}
		if(FunctionTypeEnum.SQUARE_LINE.getName().equals(functionType)){//方波
			value= CommonUtils.getSquareLine((long)coeT, coeA, coeB,time);
		}
		if(FunctionTypeEnum.MONO_RISE.getName().equals(functionType)){//单调增，斜率唯一函数
			value= CommonUtils.getMonoLine(coeA, coeB,coeT,time);
		}
		if(FunctionTypeEnum.MONO_DECREASE.getName().equals(functionType)){//单调减，斜率唯一函数
			value= CommonUtils.getMonoLine(coeA, coeB,coeT,time);
		}
		if(FunctionTypeEnum.SINE_LINE.getName().equals(functionType)){//正弦函数
			value= CommonUtils.getSineLine( 2*Math.PI/coeB,coeA,coeC, coeD,time);
		}
		if(FunctionTypeEnum.PARABOLA_UP_LINE.getName().equals(functionType)){//抛物线开口向上
			value= CommonUtils.getParabolaLine(coeA, coeB,coeT,time);
		}
		if(FunctionTypeEnum.PARABOLA_DOWN_LINE.getName().equals(functionType)){//抛物线开口向下
			value= CommonUtils.getParabolaLine(coeA, coeB,coeT,time);
		}
		if(FunctionTypeEnum.LOG_LINE.getName().equals(functionType)){//log函数
			value= CommonUtils.getLogLine(coeA, coeB,coeC,coeT,time);
		}
		if(FunctionTypeEnum.EXPON_LINE.getName().equals(functionType)){//指数函数
			value= CommonUtils.getExponLine(coeA, coeB,coeC,coeT,time);
		}
		return getAbnormalPoint(value);
	}
	/**
	 * 获取带有噪点的值,并且带浮动值，上下浮动value*0.005
	 * @param value
	 * @return
	 */
	public static double getAbnormalPoint(double value){
		value=value*(1+(ConstantManager.RANDOM.nextDouble()/100-0.005));
		if(ConstantManager.RANDOM.nextDouble()<ConstantManager.abnormalRate[0]){
			value=value*(1+(ConstantManager.RANDOM.nextDouble()-0.5));
		}
		return value;
	}
	
	/******
	 * =================================================================================
	 * =============================函数工具end===========================================
	 * =================================================================================
	 * ******/
	/***online模式**/
	public static Config initXml(){
		Object object=null;
		try {
			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
			JAXBContext context = JAXBContext.newInstance(Config.class,OpentsdbRecord.class,OpentsdbTag.class,OpentsdbValue.class
					,InfluxdbField.class,InfluxdbRecord.class,InfluxdbTag.class,InfluxdbValue.class,GraphiteValue.class,GraphiteRecord.class
					,TsfileRecord.class,TsfileSensor.class,TsfileValue.class);
			Unmarshaller unmarshaller = context.createUnmarshaller(); 
			object = unmarshaller.unmarshal(new File(path+"/conf-online.xml"));
		} catch (Exception e) {
			e.printStackTrace();
			stopThreadAndPrint("初始化conf-online.xml错误");
		}
		Config config=(Config)object;
		return config;
	}
	/**
	 * 
	 * @param dateStr yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static long data2timestamp(String dateStr){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//FIXME 注意24小时计时还是12小时计时
		Long time=null;
		try {
			Date date = sdf.parse(dateStr);
			time=date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
	public static Random RANDOM=new Random();
	/**
	 * 算出某个范围内的随机数
	 */
	public static double getRandomNumBetweenTwo(double min,double max){
		min=ConstantManager.RANDOM.nextInt((int)(max-min))+min;
		System.out.println(min);
		if(max-min>1){
			getRandomNumBetweenTwo(min, max);
		}
		return min;
	}
	
	
	/**
	 * 在文件中追加内容
	 * @param path 相对路径
	 * @param content 内容
	 */
	public static synchronized void addStringToFile(String path,String content){
		try {
			String basePath = CommonUtils.class.getClassLoader().getResource("").getPath();
			File dir=new File(basePath+"/perform");
			if(!dir.exists()){
				dir.mkdirs();
			}
			String csvPath=basePath+"/perform/"+path;
			File file = new File(csvPath);
			FileWriter fw = new FileWriter(file,true);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
