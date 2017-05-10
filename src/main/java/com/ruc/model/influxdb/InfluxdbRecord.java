package com.ruc.model.influxdb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.ruc.model.Record;

/**
 * influxdb 的record类
 * @author sung
 *
 */
public class InfluxdbRecord extends Record{
	private static final long serialVersionUID = 1L;
	private String dbName;
	private String measurement;
	private List<InfluxdbTag> tags=new ArrayList<InfluxdbTag>();
	private List<InfluxdbField> fields=new ArrayList<InfluxdbField>();
	@XmlAttribute(name="measurement")
	public String getMeasurement() {
		return measurement;
	}
	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}
	@XmlElementWrapper(name="tags")
	@XmlElement(name="tag")
	public List<InfluxdbTag> getTags() {
		return tags;
	}
	public void setTags(List<InfluxdbTag> tags) {
		this.tags = tags;
	}
	@XmlElementWrapper(name="fields")
	@XmlElement(name="field")
	public List<InfluxdbField> getFields() {
		return fields;
	}
	public void setFields(List<InfluxdbField> fields) {
		this.fields = fields;
	}
	@XmlAttribute(name="dbName")
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
