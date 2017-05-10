package com.ruc.function;
/**
 * 方波
 * @author sxg
 *
 */
public class SquareLineFunction extends BaseFunction{
	private static final long serialVersionUID = 1L;
	private long period;//周期  单位是数据点数 最小为1
	private double min;//最大值
	private double max;//最小值
	private double startValue;//开始值  默认为最小值
	public long getPeriod() {
		return period;
	}
	public void setPeriod(long period) {
		this.period = period;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getStartValue() {
		return startValue;
	}
	public void setStartValue(double startValue) {
		this.startValue = startValue;
	}
	public SquareLineFunction(long period, double min, double max) {
		super();
		this.period = period;
		this.min = min;
		this.max = max;
	}
	public SquareLineFunction() {
		super();
	}
}
