package com.ruc.constant;
/**
 * 基本配置xml文件的节点名称
 * @author sxg
 *
 */
public enum XmlNodeAttrEnum {
	NAME("name","节点名称"),VALUE("value","节点值"),TYPE("type","节点数据类型"),FUNCTION_TYPE("function-type","函数类型"),ORDER("order","排序号"),
	ACTIVE("active","激活状态"),MAX("max","最大值"),MIN("min","最小值"),START_DATE("startDate","开始时间"),END_DATE("endDate","结束时间"),
	STEP("step","步长"),COE_A("coeA","参数A"),COE_B("coeB","参数A"),COE_C("coeC","参数A"),COE_D("coeD","参数A"),COE_E("coeE","参数A"),
	COE_T("coeT","周期"),CUSTOM("custom","自定义");
	private XmlNodeAttrEnum() {
	}
	private XmlNodeAttrEnum(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	private String name;
	private String desc;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
