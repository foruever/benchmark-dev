package cn.edu.ruc.conf.base.offline;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class DeviceConf implements Serializable{
	private static final long serialVersionUID = 1L;
	private String deviceRefId;
	@XmlAttribute(name="device-ref-id")
	public String getDeviceRefId() {
		return deviceRefId;
	}
	public void setDeviceRefId(String deviceRefId) {
		this.deviceRefId = deviceRefId;
	}
}
