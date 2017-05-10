package com.ruc;

import java.text.DecimalFormat;

/**
 * 格式工具类
 * @author sung
 *
 */
public class FormatUtils {
	/**
	 * 将double类型转为非科学技术法
	 * @param db
	 * @return
	 */
	public static String DoubleFormat(double db){
		DecimalFormat df=new DecimalFormat("0.000");
		String format = df.format(db);
		return format;
	}
}
