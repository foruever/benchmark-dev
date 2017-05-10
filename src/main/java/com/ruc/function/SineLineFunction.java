package com.ruc.function;
/**
 * 正弦函数
 * Asin(wt+B)+C
 * @author sxg
 *
 */
public class SineLineFunction extends BaseFunction{
	private static final long serialVersionUID = 1L;
	private double period=1;//周期  单位是数据点数 最小为1  w=2*PI/period  
	private double amplitude;//振幅 A 
	private double initialPhase;//初相 B
	private double offset;//偏距 C
	private double angularVelocity;//角速度 w
	public double getPeriod() {
		return period;
	}
	public void setPeriod(double period) {
		this.period = period;
	}
	public double getAmplitude() {
		return amplitude;
	}
	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}
	public double getInitialPhase() {
		return initialPhase;
	}
	public void setInitialPhase(double initialPhase) {
		this.initialPhase = initialPhase;
	}
	public double getOffset() {
		return offset;
	}
	public void setOffset(double offset) {
		this.offset = offset;
	}
	public double getAngularVelocity() {
		angularVelocity=Math.PI*2/period;
		return angularVelocity;
	}
//	public void setAngularVelocity(double angularVelocity) {
//		this.angularVelocity = angularVelocity;
//	}
	/**
	 * 初始化正弦函数 Asin(w*t+B)+C  w=2*PI/T
	 * @param period 周期 T
	 * @param amplitude 振幅 A
	 * @param initialPhase 初相B
	 * @param offset 偏距 C
	 */
	public SineLineFunction(double period, double amplitude,
			double initialPhase, double offset) {
		super();
		this.period = period;
		this.amplitude = amplitude;
		this.initialPhase = initialPhase;
		this.offset = offset;
	}
	public SineLineFunction() {
		super();
	}
}

