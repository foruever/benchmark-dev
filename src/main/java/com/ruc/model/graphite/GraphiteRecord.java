package com.ruc.model.graphite;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.ruc.model.Record;

/**
 * graphite的record
 * @author sung
 *
 */
public class GraphiteRecord extends Record{
	private static final long serialVersionUID = 1L;
	private String path;
	private GraphiteValue value;
	@XmlAttribute(name="path")
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@XmlElement(name="value")
	public GraphiteValue getValue() {
		return value;
	}
	public void setValue(GraphiteValue value) {
		this.value = value;
	}
}
