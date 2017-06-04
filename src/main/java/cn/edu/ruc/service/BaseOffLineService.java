package cn.edu.ruc.service;
/**
 * 离线模式，数据生成业务接口类
 */
public interface BaseOffLineService {
	/**根据配置文件生成数据*/
	public boolean generateData(String confUrl);
	/**默认生成数据*/
	public boolean generateData();
}
