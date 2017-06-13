package cn.edu.ruc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.ruc.conf.base.device.Tag;

public class BenchmarkPoint implements Serializable{
	private static final long serialVersionUID = 1L;
	private String deviceName;
	private List<Tag> deviceTags=new ArrayList<Tag>();
	private Long timestamp;
	private String sensorName;
	private List<Tag> sensorTags=new ArrayList<Tag>();
	private Number value;
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public List<Tag> getDeviceTags() {
		return deviceTags;
	}
	public void setDeviceTags(List<Tag> deviceTags) {
		this.deviceTags = deviceTags;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	public List<Tag> getSensorTags() {
		return sensorTags;
	}
	public void setSensorTags(List<Tag> sensorTags) {
		this.sensorTags = sensorTags;
	}
	public Number getValue() {
		return value;
	}
	public void setValue(Number value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "BenchmarkPoint [deviceName=" + deviceName + ", deviceTags=" + deviceTags + ", timestamp=" + timestamp
				+ ", sensorName=" + sensorName + ", sensorTags=" + sensorTags + ", value=" + value + "]";
	}
}
