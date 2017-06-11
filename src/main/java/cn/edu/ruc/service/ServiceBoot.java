package cn.edu.ruc.service;


/**
 * 业务处理入口
 * @author Sunxg
 *
 */
public class ServiceBoot {
	public static void startServcie(){
		
		//FIXME 数据生成，将设备传感器名称，放到上级目录上，比如传感器的具体名称，放到设备引入的名字上
		
		
		//FIXME 后期加判断
		OffLineService offLine=new OffLineService();
		offLine.generateData();
		
		
		//在线模式 性能测试
		OnlineService online=new OnlineService();
		online.insertPerform();
	}
}

