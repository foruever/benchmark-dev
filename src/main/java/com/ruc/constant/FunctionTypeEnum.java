package com.ruc.constant;
/**
 * 生成数据的函数类型
 * 对应function-type
 * @author sxg
 *
 */
public enum FunctionTypeEnum {
	RANDOM_FUNTION(-1,"random-function","随机选择一个函数"),
	RANDOM(0,"random","在范围内随机产生"), 
	CONSTANT_LINE(1,"constant-line","单直线常量数据"),
	BROKEN_LINE(2,"broken-line","破折线周期函数"),
	SQUARE_LINE(3,"square-line","矩形周期函数"),
	SINE_LINE(4,"sine-line","正弦函数"),
	MONO_RISE(5,"mono-rise","单调上升斜率唯一函数"),
	MONO_DECREASE(6,"mono-decrease","单调下降斜率唯一函数"),
	PARABOLA_UP_LINE(7,"parabola-up-line","抛物线开口向上函数"),
	PARABOLA_DOWN_LINE(8,"parabola-down-line","抛物线开口向下函数"),
	LOG_LINE(9,"log-line","log函数"),
	EXPON_LINE(10,"expon-line","指数函数"),;
	private int id;
	private String name;
	private String desc;
	private FunctionTypeEnum(int id, String name, String desc) {
		this.id = id;
		this.name = name;
		this.desc = desc;
	}
	private FunctionTypeEnum() {
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
