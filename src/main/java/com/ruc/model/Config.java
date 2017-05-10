package com.ruc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.ruc.model.graphite.GraphiteRecord;
import com.ruc.model.influxdb.InfluxdbRecord;
import com.ruc.model.opentsdb.OpentsdbRecord;
import com.ruc.model.tsfile.TsfileRecord;
@XmlRootElement(name="config")
public class Config implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<OpentsdbRecord> opentsdbs=new ArrayList<OpentsdbRecord>();
	private List<InfluxdbRecord> influxdbs=new ArrayList<InfluxdbRecord>();
	private List<GraphiteRecord> graphites=new ArrayList<GraphiteRecord>();
	private List<TsfileRecord> tsfiles=new ArrayList<TsfileRecord>();
	private String mode;
	private String serverUrl;
	private String dbType;
	@XmlElementWrapper(name="opentsdbs")
	@XmlElement(name="opentsdbRecord")
	public List<OpentsdbRecord> getOpentsdbs() {
		return opentsdbs;
	}
	public void setOpentsdbs(List<OpentsdbRecord> opentsdbs) {
		this.opentsdbs = opentsdbs;
	}
	@XmlElementWrapper(name="influxdbs")
	@XmlElement(name="influxdbRecord")
	public List<InfluxdbRecord> getInfluxdbs() {
		return influxdbs;
	}
	public void setInfluxdbs(List<InfluxdbRecord> influxdbs) {
		this.influxdbs = influxdbs;
	}
	@XmlElementWrapper(name="graphites")
	@XmlElement(name="graphiteRecord")
	public List<GraphiteRecord> getGraphites() {
		return graphites;
	}
	public void setGraphites(List<GraphiteRecord> graphites) {
		this.graphites = graphites;
	}
	@XmlElementWrapper(name="tsfiles")
	@XmlElement(name="tsfileRecord")
	public List<TsfileRecord> getTsfiles() {
		return tsfiles;
	}
	public void setTsfiles(List<TsfileRecord> tsfiles) {
		this.tsfiles = tsfiles;
	}
	@XmlAttribute(name="mode")
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	@XmlAttribute(name="serverUrl")
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	@Override
	public String toString() {
		return "Config [opentsdbs=" + opentsdbs + ", influxdbs=" + influxdbs
				+ ", graphites=" + graphites + ", mode=" + mode
				+ ", serverUrl=" + serverUrl + "]";
	}
	@XmlAttribute(name="dbtype")
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
}
