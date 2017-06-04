package cn.edu.ruc.conf.base.sensor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="sensors")
public class Sensors implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Sensor> sensors=new ArrayList<Sensor>();
	@XmlElement(name="sensor")
	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}
	
}
