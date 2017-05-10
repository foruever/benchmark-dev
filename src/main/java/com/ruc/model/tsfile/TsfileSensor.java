package com.ruc.model.tsfile;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.ruc.model.Value;

/**
 * tsFile的Filed
 * @author sung
 *
 */
public class TsfileSensor implements Serializable{
	private static final long serialVersionUID = 1L;
	private String key;
	private TsfileValue value;
	@XmlElement(name="key")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@XmlElement(name="value")
	public TsfileValue getValue() {
		return value;
	}
	public void setValue(TsfileValue value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "TsfileField [key=" + key + ", value=" + value + "]";
	}
}
