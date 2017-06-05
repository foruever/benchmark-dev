package cn.edu.ruc.conf.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.ruc.conf.base.device.Tag;

public class Device implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Sensor> sensors=new ArrayList<Sensor>();
	private String id;
	private String name;
	private List<Tag> nameTags=new ArrayList<Tag>();
	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Tag> getNameTags() {
		return nameTags;
	}
	public void setNameTags(List<Tag> nameTags) {
		this.nameTags = nameTags;
	}
}

