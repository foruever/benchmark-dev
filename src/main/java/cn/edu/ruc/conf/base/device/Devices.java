package cn.edu.ruc.conf.base.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="config")
public class Devices implements Serializable{
	private static final long serialVersionUID = 1L;
	List<Device> devices=new ArrayList<Device>();
	@XmlElement(name="device")
	public List<Device> getDevices() {
		return devices;
	}
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
}
