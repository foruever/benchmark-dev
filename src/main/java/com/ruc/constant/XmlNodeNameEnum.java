package com.ruc.constant;
/**
 * 基本配置xml文件的节点名称
 * @author sxg
 *
 */
public enum XmlNodeNameEnum {
	RECORDS("records","根节点"),RECORD("record","record节点"),
	GENERATE_LINE("generateLine","生成行数"),COLUMN("column","列"),
	KEY("key","列"),VALUE("value","列"),CSV_PATH("csvPath","csv导出路径"),
	LINE_SEPARATOR("lineSeparator","列的分隔符"),GENERATE_TYPE("generateType","生成方式节点"),GENERATE_DATE("generateDate","生成时间");
	private XmlNodeNameEnum() {
	}
	private XmlNodeNameEnum(String name, String desc) {
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
