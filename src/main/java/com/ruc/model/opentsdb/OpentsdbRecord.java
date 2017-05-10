package com.ruc.model.opentsdb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.ruc.model.Record;

/**
 * opentsdb 的record类
 * @author sung
 *
 */
//@XmlRootElement(name="opentsdbRecord")
public class OpentsdbRecord extends Record{
	private static final long serialVersionUID = 1L;
	private String metric;
	private List<OpentsdbTag> tags=new ArrayList<OpentsdbTag>();
	private OpentsdbValue value;
	@XmlAttribute(name="metric")
	public String getMetric() {
		return metric;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}
	@XmlElement(name="value")
	public OpentsdbValue getValue() {
		return value;
	}
	public void setValue(OpentsdbValue value) {
		this.value = value;
	}
	@XmlElementWrapper(name="tags")
	@XmlElement(name="tag")
	public List<OpentsdbTag> getTags() {
		return tags;
	}
	public void setTags(List<OpentsdbTag> tags) {
		this.tags = tags;
	}
	@Override
	public String toString() {
		return "OpentsdbRecord [metric=" + metric + ", tags=" + tags
				+ ", value=" + value + "]";
	}
}
