package com.ruc.function;

import java.io.Serializable;
/**
 * 数据函数基类
 */
public abstract class BaseFunction implements Serializable {
	private static final long serialVersionUID = 1L;
	private double initValue;//初始函数值  用于估计函数的开始点
	public double getInitValue() {
		return initValue;
	}
	public void setInitValue(double initValue) {
		this.initValue = initValue;
	}
	
}
