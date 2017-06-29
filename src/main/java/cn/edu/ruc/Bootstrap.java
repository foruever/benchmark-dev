package cn.edu.ruc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import cn.edu.ruc.conf.init.InitManager;
import cn.edu.ruc.service.ServiceBoot;

/**
 * 入口类
 * @author Sunxg
 *
 */
public class Bootstrap {
	public static int BATCH_ID=0;//FIXME 基本完成后，从数据库维护
	public static void main(String[] args) throws Exception {
//		Properties properties = System.getProperties();
//		Set<Object> keySet = properties.keySet();
//		for(Object key:keySet){
//			System.out.println(key+" = "+properties.getProperty(key.toString()));
//		}
//		System.out.println("=================================");
//		String path = Class.class.getClass().getResource("/").getPath();
//		System.out.println(path);
//		System.out.println(System.getProperty("user.dir"));
//		
//		
//		InputStream in = Class.class.getClass().getResourceAsStream("/aaa.json");
//		BufferedReader br=new BufferedReader(new InputStreamReader(in));
//		String s=null;
//		while((s=br.readLine())!=null){
//			System.out.println(s);
//		}
		
		//生成测试批次  FIXME 
		BATCH_ID=(int) (System.currentTimeMillis()/1000);
		//FIXME 1,初始化各个配置
		InitManager.initConfig();
		//FIXME 2,进行业务处理
//		ServiceBoot.startServcie();
	}
}

