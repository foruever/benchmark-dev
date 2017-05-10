package com.ruc.model.tsfile;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.ruc.model.Record;

public class TsfileRecord extends Record{
	private static final long serialVersionUID = 1L;
	private String tsFilePath;
	private String deltaObjectId;
	private List<TsfileSensor> sensors=new ArrayList<TsfileSensor>();
	@XmlAttribute(name="tsFilePath")
	public String getTsFilePath() {
		return tsFilePath;
	}
	public void setTsFilePath(String tsFilePath) {
		this.tsFilePath = tsFilePath;
	}
	@XmlAttribute(name="deltaObjectId")
	public String getDeltaObjectId() {
		return deltaObjectId;
	}
	public void setDeltaObjectId(String deltaObjectId) {
		this.deltaObjectId = deltaObjectId;
	}
	@XmlElementWrapper(name="sensors")
	@XmlElement(name="sensor")
	public List<TsfileSensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<TsfileSensor> sensors) {
		this.sensors = sensors;
	}
}
