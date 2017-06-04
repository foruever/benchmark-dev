package cn.edu.ruc.conf.base.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Device implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private List<Tag> nameTags=new ArrayList<Tag>();
	private List<SensorConf> sensors=new ArrayList<SensorConf>();
	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElementWrapper(name="nametags")
	@XmlElement(name="tag")
	public List<Tag> getNameTags() {
		return nameTags;
	}
	public void setNameTags(List<Tag> nameTags) {
		this.nameTags = nameTags;
	}
	@XmlElementWrapper(name="sensors")
	@XmlElement(name="sensor")
	public List<SensorConf> getSensors() {
		return sensors;
	}
	public void setSensors(List<SensorConf> sensors) {
		this.sensors = sensors;
	}
}
