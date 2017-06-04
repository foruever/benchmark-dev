package cn.edu.ruc.conf.base.offline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name="config")
public class GenerateOffline implements Serializable{
	private static final long serialVersionUID = 1L;
	List<Database> databases=new ArrayList<Database>();
	@XmlElement(name="database")
	public List<Database> getDatabases() {
		return databases;
	}
	public void setDatabases(List<Database> databases) {
		this.databases = databases;
	}
}
