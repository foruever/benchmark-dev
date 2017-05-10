package com.ruc.model.influxdb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.ruc.model.Value;

/**
 * influxdb的Filed
 * @author sung
 *
 */
public class InfluxdbField implements Serializable{
	private static final long serialVersionUID = 1L;
	private String key;
	private InfluxdbValue value;
	@XmlElement(name="key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@XmlElement(name="value")
	public InfluxdbValue getValue() {
		return value;
	}
	public void setValue(InfluxdbValue value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "InfluxdbField [key=" + key + ", value=" + value + "]";
	}
}
