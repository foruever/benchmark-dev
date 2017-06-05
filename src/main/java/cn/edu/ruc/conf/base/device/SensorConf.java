package cn.edu.ruc.conf.base.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class SensorConf implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String beginTime;
	private String endTime;
	private String sensorRefId;
	private Long step;
	private List<Tag> sensorTags=new ArrayList<Tag>();
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute(name="begin-time")
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	@XmlAttribute(name="end-time")
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	@XmlAttribute(name="sensor-ref-id")
	public String getSensorRefId() {
		return sensorRefId;
	}
	public void setSensorRefId(String sensorRefId) {
		this.sensorRefId = sensorRefId;
	}
	@XmlElementWrapper(name="tags")
	@XmlElement(name="tag")
	public List<Tag> getSensorTags() {
		return sensorTags;
	}
	public void setSensorTags(List<Tag> sensorTags) {
		this.sensorTags = sensorTags;
	}
	@XmlAttribute(name="step")
	public Long getStep() {
		return step;
	}
	public void setStep(Long step) {
		this.step = step;
	}
}
