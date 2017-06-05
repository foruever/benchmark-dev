package cn.edu.ruc.conf.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.ruc.conf.base.device.Tag;
/**
 * 设备上传感器的配置
 * @author Sunxg
 *
 */
public class Sensor implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String fuctionRefId;
	private Double max;
	private Double min;
	private Long cycle;
	private Date startTime;
	private Date endTime;
	private Long step;
	private List<Tag> tags=new ArrayList<Tag>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFuctionRefId() {
		return fuctionRefId;
	}
	public void setFuctionRefId(String fuctionRefId) {
		this.fuctionRefId = fuctionRefId;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Long getCycle() {
		return cycle;
	}
	public void setCycle(Long cycle) {
		this.cycle = cycle;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Long getStep() {
		return step;
	}
	public void setStep(Long step) {
		this.step = step;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

