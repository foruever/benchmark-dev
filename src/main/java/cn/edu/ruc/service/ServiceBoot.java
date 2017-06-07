package cn.edu.ruc.service;


/**
 * 业务处理入口
 * @author Sunxg
 *
 */
public class ServiceBoot {
	public static void startServcie(){
		//FIXME 后期加判断
		OffLineService offLine=new OffLineService();
		offLine.generateData();
	}
}

