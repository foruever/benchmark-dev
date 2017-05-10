package com.ruc.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Value implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;//类型
	private String functionType;//函数类型
	private double coeA; //参数
	private double coeB; //参数
	private double coeC; //参数
	private double coeD; //参数
	private double coeE; //参数
	private double coeT; //参数T 周期 ms
	
	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@XmlAttribute(name="function-type")
	public String getFunctionType() {
		return functionType;
	}
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}
	@XmlAttribute(name="coeA")
	public double getCoeA() {
		return coeA;
	}
	public void setCoeA(double coeA) {
		this.coeA = coeA;
	}
	@XmlAttribute(name="coeB")
	public double getCoeB() {
		return coeB;
	}
	public void setCoeB(double coeB) {
		this.coeB = coeB;
	}
	@XmlAttribute(name="coeC")
	public double getCoeC() {
		return coeC;
	}
	public void setCoeC(double coeC) {
		this.coeC = coeC;
	}
	@XmlAttribute(name="coeD")
	public double getCoeD() {
		return coeD;
	}
	public void setCoeD(double coeD) {
		this.coeD = coeD;
	}
	@XmlAttribute(name="coeE")
	public double getCoeE() {
		return coeE;
	}
	public void setCoeE(double coeE) {
		this.coeE = coeE;
	}
	@Override
	public String toString() {
		return "Value [type=" + type + ", functionType=" + functionType
				+ ", coeA=" + coeA + ", coeB=" + coeB + ", coeC=" + coeC
				+ ", coeD=" + coeD + ", coeE=" + coeE + "]";
	}
	@XmlAttribute(name="coeT")
	public double getCoeT() {
		return coeT;
	}
	public void setCoeT(double coeT) {
		this.coeT = coeT;
	}
}
