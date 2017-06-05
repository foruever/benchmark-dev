package cn.edu.ruc.conf.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Database implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Device> devices=new ArrayList<Device>(); 
	private String type;
	public List<Device> getDevices() {
		return devices;
	}
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}

