package com.ruc.constant;
/**
 * xml文档中 type类型
 * @author sxg
 *
 */
public enum TypeEnum {
	STRING("string","字符串型"),INT("int","有符号整数型"),UNSIGNED_INT("unsigned-int","无符号整数型"),FLOAT("float","有符号浮点型"),UNSIGNED_FLOAT("unsigned-float","无符号浮点型"),KEY_VALUE("key-value","键值对性"),TIMESTAMP("timestamp","时间戳类型");
	private TypeEnum() {
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
	private TypeEnum(String name,String desc){
		this.name=name;
		this.desc=desc;
	}
}
