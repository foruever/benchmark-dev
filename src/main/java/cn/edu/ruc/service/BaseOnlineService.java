package cn.edu.ruc.service;
/**
 * 在线模式
 * 进行各项性能测试
 * @author sung
 *
 */
public interface BaseOnlineService {
	public void insertPerform();
	public void singleSimpleQueryPerform();
	public void singleAggregatePerform();
	/**
	 * 
	 * @param simple 简单查询所占比例
	 * @param aggregate 分析查询所占比例
	 */
	public void singleSimpleAndAggrMutiPerform(double simple,double aggregate);
	/**
	 * 先写死的
	 * 实现功能，然后改为可配置的
	 * */
	public void multiStableInsertAndQueryPerform();
	/**
	 * 先写死的
	 * 实现功能，然后改为可配置的
	 */
	public void multiStableQueryAndInsertPerform();
}
