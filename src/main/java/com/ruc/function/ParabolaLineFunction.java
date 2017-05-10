package com.ruc.function;
/**
 * 抛物线
 * ax*x+b
 * a>0开口向上
 * a<0开口向下
 * @author sxg
 */
public class ParabolaLineFunction extends BaseFunction{
	private static final long serialVersionUID = 1L;
	private double coeA;//系数A a>0 开口向上 a<0开口向下 随毫秒的函数，一般系数很小
	private double coeB;//系数B
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
	public ParabolaLineFunction(double coeA, double coeB) {
		super();
		this.coeA = coeA;
		this.coeB = coeB;
	}
	public ParabolaLineFunction() {
		super();
	}
}

