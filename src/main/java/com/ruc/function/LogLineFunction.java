package com.ruc.function;
/**
 * Log函数
 * AlogB*X+C;B>0
 * @author sxg
 *
 */
public class LogLineFunction extends BaseFunction{
	private static final long serialVersionUID = 1L;
	private double coeA;//系数A
	private double coeB;//系数B
	private double coeC;//系数C
	public double getCoeA() {
		return coeA;
	}
	public void setCoeA(double coeA) {
		this.coeA = coeA;
	}
	public double getCoeB() {
		return coeB;
	}
	public void setCoeB(double coeB) {
		this.coeB = coeB;
	}
	public double getCoeC() {
		return coeC;
	}
	public void setCoeC(double coeC) {
		this.coeC = coeC;
	}
	public LogLineFunction(double coeA, double coeB, double coeC) {
		super();
		this.coeA = coeA;
		this.coeB = coeB;
		this.coeC = coeC;
	}
	public LogLineFunction() {
		super();
	}
}

