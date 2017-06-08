package cn.edu.ruc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	/**
	 * 
	 * @param dateStr yyyy-MM-dd hh:mm:ss
	 * @return
	 */
	public static Date datastr2Date(String dateStr){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//FIXME 注意24小时计时还是12小时计时
		Date date =null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 日期时间戳转为 yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @return
	 */
	public static String time2Str(long time){
		Date date=new Date(time);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr="1970-01-01 00:00:00";
		try {
			dateStr = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}
}

