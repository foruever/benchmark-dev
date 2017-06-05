package cn.edu.ruc.function;

import java.util.Random;

public class Function {
	/**
	 * 获取单调函数浮点值
	 * @param max 最大值
	 * @param min 最小值
	 * @param cycle 周期，单位为s
	 * @param currentTime 当前时间 单位为ms
	 * @return
	 */
	public static double getMonoValue(double max,double min,double cycle,long currentTime){
		double k=(max-min)/(cycle*1000);
		return k*(currentTime%1000000);
	}
	/**
	 * 
	 * @param max 最大值
	 * @param min 最小值
	 * @param cycle 周期，单位为s
	 * @param currentTime 当前时间 单位为ms
	 * @return
	 */
	public static long getMonoValue(long max,long min,double cycle,long currentTime){
		double k=(max-min)/(cycle*1000);
		return (long)(k*(currentTime%(cycle*1000)));
	}
	
	/**
	 * 获取正弦函数浮点值
	 * @param max 最大值
	 * @param min 最小值
	 * @param cycle 周期，单位为s
	 * @param currentTime 当前时间 单位为ms
	 * @return
	 */
	public static double getSineValue(double max,double min,double cycle,long currentTime){
		double w=2*Math.PI/(cycle*1000);
		double a=(max-min)/2;
		double b=(max-min)/2;
		return Math.sin(w*(currentTime%(cycle*1000)))*a+b;
	}
	/**
	 * 获取方波函数浮点值
	 * @param max 最大值
	 * @param min 最小值
	 * @param cycle 周期，单位为s
	 * @param currentTime 当前时间 单位为ms
	 * @return
	 */
	public static double getSquareValue(double max,double min,double cycle,long currentTime){
		double t = cycle/2*1000;
		if((currentTime%(cycle*1000))<t){
			return max;
		}else{
			return min;
		}
	}
	private static final Random RANDOM=new Random();
	/**
	 * 获取随机数函数浮点值
	 * @param max 最大值
	 * @param min 最小值
	 * @param cycle 周期，单位为s
	 * @param currentTime 当前时间 单位为ms
	 * @return
	 */
	public static double getRandomValue(double max,double min){
		return RANDOM.nextDouble()*(max-min)+min;
	}
	public static void main(String[] args) throws InterruptedException {
		for(int i=0;i<1000;i++){
			Thread.currentThread().sleep(1000L);
//			System.out.println(getMonoValue(1000L, 0L, 1000,System.currentTimeMillis()));
//			System.out.println(getSineValue(1000L, 0L, 20,System.currentTimeMillis()));
//			System.out.println(getSquareValue(1000L, 0L, 20,System.currentTimeMillis()));
//			System.out.println(getRandomValue(1000L, 0L));
		}
	}
}
