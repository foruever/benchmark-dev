package cn.edu.ruc.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import cn.edu.ruc.enums.RunModeEnum;



/**
 * 业务处理入口
 * @author Sunxg
 *
 */
public class ServiceBoot {
	public static void startServcie(){
		
		//FIXME 数据生成，将设备传感器名称，放到上级目录上，比如传感器的具体名称，放到设备引入的名字上
		String runMode = getRunMode();
		
		//FIXME 后期加判断
		if(RunModeEnum.OFFLINE_MODE.getName().equals(runMode)){
			OffLineService offLine=new OffLineService();
//			offLine.generateData();
			System.out.println("我是离线生成数据");
		}
		
		if(RunModeEnum.PERFORM_TEST.getName().equals(runMode)){
			//在线模式 性能测试   首先导入一个月的数据
			System.out.println("我是在线生成数据");
			OnlineService online=new OnlineService();
//			每秒进去的数据量，通过加设备，怪怪的
//			online.insertPerform();
//			online.singleSimpleQueryPerform();
//			online.singleAggregatePerform();
//			online.multiStableInsertAndQueryPerform();
//			online.multiStableQueryAndInsertPerform();
		}
	}
	private static String RUN_MODE;
	public static String getRunMode(){
		if(StringUtils.isBlank(RUN_MODE)){
			initRunMode();
		}
		return RUN_MODE;
	}
	private static void initRunMode(){
		String path = System.getProperty("user.dir");
		File file=new File(path+"/conf/run_mode.properties");
		Properties prop=new Properties();
		try {
			prop.load(new FileInputStream(file));
			RUN_MODE=prop.getProperty("mode");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		getRunMode();
		System.out.println(getRunMode());
	}
}

