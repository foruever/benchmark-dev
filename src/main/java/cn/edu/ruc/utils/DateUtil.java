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
}

