package com.ruc;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.ruc.model.Config;
import com.ruc.model.opentsdb.OpentsdbRecord;
import com.ruc.model.opentsdb.OpentsdbTag;
import com.ruc.model.opentsdb.OpentsdbValue;

public class TestXml2Obj {
	public static void main(String[] args) throws Exception {
//		String path = GenerateBigDataCSV.class.getClassLoader().getResource("").getPath();
//		JAXBContext context = JAXBContext.newInstance(OpentsdbRecord.class,OpentsdbTag.class,OpentsdbValue.class);
//		Unmarshaller unmarshaller = context.createUnmarshaller(); 
//		Object object = unmarshaller.unmarshal(new File(path+"/conf-online-1.xml"));
//		System.out.println(object);
		String path = GenerateBigDataCSV.class.getClassLoader().getResource("").getPath();
		JAXBContext context = JAXBContext.newInstance(Config.class,OpentsdbRecord.class,OpentsdbTag.class,OpentsdbValue.class);
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		Object object = unmarshaller.unmarshal(new File(path+"/conf-online.xml"));
		Config config=(Config)object;
		System.out.println(config);
	}
}
