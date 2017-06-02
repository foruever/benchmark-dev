package cn.edu.ruc.enums;
/**
 * 
 * @author Sunxg
 * 数据库类型枚举类
 */
public enum DataBaseTypeEnum {
	TSFILE(1,"tsFile"),
	OPENTSDB(2,"opentsdb数据库"),
	INFLUXDB(3,"influxDB数据库"),
	MYSQL(4,"mysql数据库");
	
	
	private Integer value;
	private String desc;
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	private DataBaseTypeEnum(Integer value, String desc) {
		this.value = value;
		this.desc = desc;
	}
}

