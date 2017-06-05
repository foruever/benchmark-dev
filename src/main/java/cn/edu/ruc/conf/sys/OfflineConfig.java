package cn.edu.ruc.conf.sys;

import java.util.ArrayList;
import java.util.List;

public class OfflineConfig extends Config{
	private static final long serialVersionUID = 1L;
	List<Database> databases=new ArrayList<Database>();
	public List<Database> getDatabases() {
		return databases;
	}
	public void setDatabases(List<Database> databases) {
		this.databases = databases;
	}
}

