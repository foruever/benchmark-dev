package cn.edu.ruc.conf.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Database implements Serializable{
	private static final long serialVersionUID = 1L;
	private String type;
	private List<DeviceConf> deviceConf=new ArrayList<DeviceConf>();
	@XmlElementWrapper(name="devices")
	@XmlElement(name="device")
	public List<DeviceConf> getDeviceConf() {
		return deviceConf;
	}
	public void setDeviceConf(List<DeviceConf> deviceConf) {
		this.deviceConf = deviceConf;
	}
	@XmlAttribute(name="type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
