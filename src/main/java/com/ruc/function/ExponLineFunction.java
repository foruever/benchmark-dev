package com.ruc.function;
/**
 * 指数函数
 * Ae(B*x)+C
 * @author sxg
 * 步长为 最大行数/20
 */
public class ExponLineFunction extends BaseFunction{
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
	public ExponLineFunction(double coeA, double coeB, double coeC) {
		super();
		this.coeA = coeA;
		this.coeB = coeB;
		this.coeC = coeC;
	}
	public ExponLineFunction() {
		super();
	}
	
}

