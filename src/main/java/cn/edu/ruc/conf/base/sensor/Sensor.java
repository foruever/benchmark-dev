package cn.edu.ruc.conf.base.sensor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
public class Sensor implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String fuctionRefId;
	private Double max;
	private Double min;
	private Long cycle;
	@XmlAttribute(name="id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlAttribute(name="function-ref-id")
	public String getFuctionRefId() {
		return fuctionRefId;
	}
	public void setFuctionRefId(String fuctionRefId) {
		this.fuctionRefId = fuctionRefId;
	}
	@XmlAttribute(name="max")
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	@XmlAttribute(name="min")
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	@XmlAttribute(name="cycle")
	public Long getCycle() {
		return cycle;
	}
	public void setCycle(Long cycle) {
		this.cycle = cycle;
	} 
}
