package com.ruc.function;
/**
 * 单调斜率唯一函数
 * k*x+b
 */
public class MonoLineFunction extends BaseFunction{
	private static final long serialVersionUID = 1L;
	/**
	 * 斜率 k
	 */
	private double slope;
	/**
	 * 初始值 b
	 */
	private double startValue;
	/**
	 * k*x+b
	 * @param slope 斜率k  >0为单调增，<0为单调减
	 * @param startValue 初始值b
	 */
	public MonoLineFunction(double slope, double startValue) {
		super();
		this.slope = slope;
		this.startValue = startValue;
	}
	public double getSlope() {
		return slope;
	}
	public void setSlope(double slope) {
		this.slope = slope;
	}
	public MonoLineFunction() {
		super();
	}
	public double getStartValue() {
		return startValue;
	}
	public void setStartValue(double startValue) {
		this.startValue = startValue;
	}
}

