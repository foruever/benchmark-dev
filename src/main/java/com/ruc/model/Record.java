package com.ruc.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
/**
 * 设备时间序列记录
 * @author sxg
 *
 */
public abstract class Record implements Serializable{
	private static final long serialVersionUID = 1L;
	private String startTime;
	private String endTime;
	private long step;
	private long cacheLine;
	@XmlAttribute(name="startDate")
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	@XmlAttribute(name="endDate")
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	@XmlAttribute(name="step")
	public long getStep() {
		return step;
	}
	public void setStep(long step) {
		this.step = step;
	}
	@XmlAttribute(name="cacheLine")
	public long getCacheLine() {
		return cacheLine;
	}
	public void setCacheLine(long cacheLine) {
		this.cacheLine = cacheLine;
	}
}
