package com.ruc.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Tag implements Serializable {
	private static final long serialVersionUID = 1L;
	private String key;
	private String value;
	@XmlAttribute(name="key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@XmlAttribute(name="value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Tag [key=" + key + ", value=" + value + "]";
	}
}
