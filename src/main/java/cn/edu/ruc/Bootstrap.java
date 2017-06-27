package cn.edu.ruc;

import cn.edu.ruc.conf.init.InitManager;
import cn.edu.ruc.service.ServiceBoot;

/**
 * 入口类
 * @author Sunxg
 *
 */
public class Bootstrap {
	public static int BATCH_ID=0;//FIXME 基本完成后，从数据库维护
	public static void main(String[] args) {
		//生成测试批次  FIXME 
		BATCH_ID=(int) (System.currentTimeMillis()/1000);
		//FIXME 1,初始化各个配置
		InitManager.initConfig();
		//FIXME 2,进行业务处理
		ServiceBoot.startServcie();
	}
}

