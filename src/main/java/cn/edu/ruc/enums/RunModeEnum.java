package cn.edu.ruc.enums;
/**
 * 运行模式
 * @author Sunxg
 *
 */
public enum RunModeEnum {
	PERFORM_TEST("perform","性能测试模式"),OFFLINE_MODE("offline","离线数据生成模式");
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
	private RunModeEnum(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
}

