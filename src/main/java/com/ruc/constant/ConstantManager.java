package com.ruc.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ruc.function.BrokenLineFunction;
import com.ruc.function.ExponLineFunction;
import com.ruc.function.LogLineFunction;
import com.ruc.function.MonoLineFunction;
import com.ruc.function.ParabolaLineFunction;
import com.ruc.function.SineLineFunction;
import com.ruc.function.SquareLineFunction;
import com.ruc.model.RecordTime;

/**
 * 常量管理类
 * @author sxg
 *
 */
public class ConstantManager {
	/**随机数常量*/
	public final static Random RANDOM=new Random();
	/**时间戳map*/
	public final static Map<Long,Integer> TIMESTAMP_MAP=new HashMap<Long,Integer>();
	/**当前正在生成的行*/
	public static long CURRENT_NUM=0;
	/**总共要生成的行数*/
	public static long GENERATE_LINE=100000L;
	/**分隔符*/
	public static String LINE_SEPARATOR=",";
	/**文件路径*/
	public static String FILE_PATH="";
	
	/**破折线函数对象*/
	public static final BrokenLineFunction BROKEN_LINE=new BrokenLineFunction(200,0,800);
	/**方波函数对象*/
	public static final SquareLineFunction SQUARE_LINE=new SquareLineFunction(200,200, 1000);
	/**单调斜率唯一递增函数*/
	public static final MonoLineFunction MONO_RISE_LINE=new MonoLineFunction(3,100);
	/**单调递减斜率唯一函数*/
	public static final MonoLineFunction MONO_DECREASE_LINE=new MonoLineFunction(-5,900);
	/**正弦函数*/
	public static final SineLineFunction SINE_LINE=new SineLineFunction(300,1000000,100,500);
	/** 抛物线开口向上函数*/
	public static final ParabolaLineFunction PARABOLA_UP_LINE=new ParabolaLineFunction(0.25,1000);
	/** 抛物线开口向下函数*/
	public static final ParabolaLineFunction PARABOLA_DOWN_LINE=new ParabolaLineFunction(-0.25,1000);
	/** log函数*/
	public static final LogLineFunction LOG_LINE=new LogLineFunction(0.5,2,100);
	/** 指数函数函数*/
	public static final ExponLineFunction EXPON_LINE=new ExponLineFunction(100, 0.001,100); 
	
	
	/**默认周期*/
	public static final Long DEFAULT_PERIOD=1000*3600L;
	
	/**生成方式*/
	public static String GENERATE_TYPE;
	/**开始时间*/
	public static long START_TIME;
	/**结束时间*/
	public static long END_TIME;
	/**步长*/
	public static long TIME_STEP;
	
	public final static double[] abnormalRate={0.005,0.01,0.1,0.15,0.2};
	
	/**
	 * key:record标签名称
	 * value:record对应的各个时间参数
	 * */
	public static final Map<String,RecordTime> RECORD_NAME_TIME=new HashMap<String, RecordTime>();
}
