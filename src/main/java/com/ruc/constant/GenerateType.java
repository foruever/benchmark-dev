package com.ruc.constant;
/**
 * 数据生产方式
 * 
 * @author sxg
 *
 */
public enum GenerateType {
	DATE(1,"date","按照开始结束时间"),LINE(2,"line","按照总共的行数"),ONLINE(3,"online","实时生成"),OFFLINE(4,"offline","离线批量生成");
	private Integer id;
	private String value;
	private String desc;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	private GenerateType(Integer id, String value, String desc) {
		this.id = id;
		this.value = value;
		this.desc = desc;
	}
}
