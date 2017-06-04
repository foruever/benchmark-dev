package cn.edu.ruc.service;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.ruc.CommonUtils;
import com.ruc.model.Config;
import com.ruc.model.graphite.GraphiteRecord;
import com.ruc.model.graphite.GraphiteValue;
import com.ruc.model.influxdb.InfluxdbField;
import com.ruc.model.influxdb.InfluxdbRecord;
import com.ruc.model.influxdb.InfluxdbTag;
import com.ruc.model.influxdb.InfluxdbValue;
import com.ruc.model.opentsdb.OpentsdbRecord;
import com.ruc.model.opentsdb.OpentsdbTag;
import com.ruc.model.opentsdb.OpentsdbValue;
import com.ruc.model.tsfile.TsfileRecord;
import com.ruc.model.tsfile.TsfileSensor;
import com.ruc.model.tsfile.TsfileValue;

import cn.edu.ruc.conf.base.device.Device;
import cn.edu.ruc.conf.base.device.Devices;
import cn.edu.ruc.conf.base.device.SensorConf;
import cn.edu.ruc.conf.base.device.Tag;
import cn.edu.ruc.conf.base.offline.Database;
import cn.edu.ruc.conf.base.offline.DeviceConf;
import cn.edu.ruc.conf.base.offline.GenerateOffline;
import cn.edu.ruc.conf.base.sensor.Sensor;
import cn.edu.ruc.conf.base.sensor.Sensors;

public class OffLineService implements BaseOffLineService {

	@Override
	public boolean generateData(String confUrl) {
		try {
			//1 将配置文件解析为java对象
			//2 从对象中获取目标数据库，设备，各个设备所对应的点，各个点所对应的属性
			//3 根据2中的目标数据库 加工对象，生成数据文件，一个设备对应对应一个文件
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean generateData() {
		String rootPath=this.getClass().getClassLoader().getResource("").getPath();
		String baseConfPath=rootPath+"/generate-offline.conf";
		return generateData(baseConfPath);
	}
	public static void main(String[] args) {
		//sensor.xml start
//		Object object=null;
//		try {
//			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
//			JAXBContext context = JAXBContext.newInstance(Sensors.class,Sensor.class);
//			Unmarshaller unmarshaller = context.createUnmarshaller(); 
//			object = unmarshaller.unmarshal(new File(path+"/sensor.xml"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Sensors sensors=(Sensors)object;
		//sensor.xml end
		
		//device.xml start
//		Object object=null;
//		try {
//			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
//			JAXBContext context = JAXBContext.newInstance(Devices.class,Device.class,SensorConf.class,Tag.class);
//			Unmarshaller unmarshaller = context.createUnmarshaller(); 
//			object = unmarshaller.unmarshal(new File(path+"/device.xml"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Devices sensors=(Devices)object;
		//device.xml end
		
		//generate-offline.xml start
		Object object=null;
		try {
			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
			JAXBContext context = JAXBContext.newInstance(GenerateOffline.class,Database.class,DeviceConf.class);
			Unmarshaller unmarshaller = context.createUnmarshaller(); 
			object = unmarshaller.unmarshal(new File(path+"/generate-offline.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		GenerateOffline offlines=(GenerateOffline)object;
		//generate-offline.xml end
	}
}
