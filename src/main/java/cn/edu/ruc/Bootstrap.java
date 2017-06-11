package cn.edu.ruc;

import cn.edu.ruc.conf.init.InitManager;
import cn.edu.ruc.service.ServiceBoot;

/**
 * 入口类
 * @author Sunxg
 *
 */
public class Bootstrap {
	public static void main(String[] args) {
		//FIXME 1,初始化各个配置
		InitManager.initConfig();
		//FIXME 2,进行业务处理
		ServiceBoot.startServcie();
	}
}

