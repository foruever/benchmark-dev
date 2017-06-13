package cn.edu.ruc.conf.init;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.BeanUtils;

import cn.edu.ruc.conf.base.Database;
import cn.edu.ruc.conf.base.DeviceConf;
import cn.edu.ruc.conf.base.device.Device;
import cn.edu.ruc.conf.base.device.Devices;
import cn.edu.ruc.conf.base.device.SensorConf;
import cn.edu.ruc.conf.base.device.Tag;
import cn.edu.ruc.conf.base.offline.GenerateOffline;
import cn.edu.ruc.conf.base.online.GenerateOnline;
import cn.edu.ruc.conf.base.sensor.Sensor;
import cn.edu.ruc.conf.base.sensor.Sensors;
import cn.edu.ruc.conf.sys.OfflineConfig;
import cn.edu.ruc.conf.sys.OnlineConfig;
import cn.edu.ruc.utils.DateUtil;

import com.ruc.CommonUtils;

/**
 * 
 * @author Sunxg
 *
 */
public class InitManager {
	private static final OfflineConfig OFFLINE_CONFIG=new OfflineConfig();
	private static final OnlineConfig ONLINE_CONFIG=new OnlineConfig();
	private static List<Sensor> SENSORS=null; 
	private static List<Device> DEVICES=null;
	private static List<Database> OFFLINE_DATABASES=null;
	private static List<Database> ONLINE_DATABASES=null;//FIXME DATABASE包含的设备可以再次优化为模板，然后DATABASES直接引用
	private static void initBaseConf(){
		//sensor.xml start
		Object object1=null;
		try {
			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
			JAXBContext context = JAXBContext.newInstance(Sensors.class,Sensor.class);
			Unmarshaller unmarshaller = context.createUnmarshaller(); 
			object1 = unmarshaller.unmarshal(new File(path+"/sensor.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Sensors sensors=(Sensors)object1;
		SENSORS=sensors.getSensors();
		//sensor.xml end
		
		//device.xml start
		Object object2=null;
		try {
			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
			JAXBContext context = JAXBContext.newInstance(Devices.class,Device.class,SensorConf.class,Tag.class);
			Unmarshaller unmarshaller = context.createUnmarshaller(); 
			object2 = unmarshaller.unmarshal(new File(path+"/device.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Devices devices=(Devices)object2;
		DEVICES=devices.getDevices();
		//device.xml end
		
		//generate-offline.xml start
		Object object3=null;
		try {
			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
			JAXBContext context = JAXBContext.newInstance(GenerateOffline.class,Database.class,DeviceConf.class);
			Unmarshaller unmarshaller = context.createUnmarshaller(); 
			object3 = unmarshaller.unmarshal(new File(path+"/generate-offline.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		GenerateOffline offline=(GenerateOffline)object3;
		OFFLINE_DATABASES=offline.getDatabases();
		//generate-offline.xml end
		//generate-online.xml start
		Object object4=null;
		try {
			String path = CommonUtils.class.getClassLoader().getResource("").getPath();
			JAXBContext context = JAXBContext.newInstance(GenerateOnline.class,Database.class,DeviceConf.class);
			Unmarshaller unmarshaller = context.createUnmarshaller(); 
			object4 = unmarshaller.unmarshal(new File(path+"/generate-online.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		GenerateOnline online=(GenerateOnline)object4;
		ONLINE_DATABASES=online.getDatabases();
		//generate-online.xml end
		
	}
	private static void initOfflineConfig() {
		List<cn.edu.ruc.conf.sys.Database> dbs = OFFLINE_CONFIG.getDatabases();
		if(OFFLINE_DATABASES!=null&&OFFLINE_DATABASES.size()>0){
			for(Database cdb:OFFLINE_DATABASES){
				cn.edu.ruc.conf.sys.Database db=new cn.edu.ruc.conf.sys.Database();
				db.setType(cdb.getType());
				List<cn.edu.ruc.conf.sys.Device> devices = db.getDevices();
				List<DeviceConf> deviceConfs = cdb.getDeviceConf();
				if(deviceConfs!=null&&deviceConfs.size()>0){
					for(DeviceConf dc:deviceConfs){
						cn.edu.ruc.conf.sys.Device device=new cn.edu.ruc.conf.sys.Device();
						Device cDevice = getDeviceById(dc.getDeviceRefId());
						device.setName(cDevice.getName());
						device.setNameTags(cDevice.getNameTags());
						device.setId(cDevice.getId());
						List<cn.edu.ruc.conf.sys.Sensor> sensors = device.getSensors();
						List<SensorConf> sensorConfs = cDevice.getSensors();
						if(sensorConfs!=null&&sensorConfs.size()>0){
							for(SensorConf sc:sensorConfs){
								cn.edu.ruc.conf.sys.Sensor sensor=new cn.edu.ruc.conf.sys.Sensor();
								sensor.setName(sc.getName());
								sensor.setStep(sc.getStep());
								sensor.setTags(sc.getSensorTags());
								sensor.setStartTime(DateUtil.datastr2Date(sc.getBeginTime()));
								sensor.setEndTime(DateUtil.datastr2Date(sc.getEndTime()));
								sensor.setId(sc.getSensorRefId());
								Sensor cSensor= getSensorById(sc.getSensorRefId());
								try {
									BeanUtils.copyProperties(sensor, cSensor);
								} catch (Exception e) {
									e.printStackTrace();
								}
								sensors.add(sensor);
							}
						}
						devices.add(device);
					}
				}
				dbs.add(db);
			}
		}
	}
	private static void initOnlineConf() {
		List<cn.edu.ruc.conf.sys.Database> dbs = ONLINE_CONFIG.getDatabases();
		if(ONLINE_DATABASES!=null&&ONLINE_DATABASES.size()>0){
			for(Database cdb:ONLINE_DATABASES){
				cn.edu.ruc.conf.sys.Database db=new cn.edu.ruc.conf.sys.Database();
				db.setType(cdb.getType());
				List<cn.edu.ruc.conf.sys.Device> devices = db.getDevices();
				List<DeviceConf> deviceConfs = cdb.getDeviceConf();
				if(deviceConfs!=null&&deviceConfs.size()>0){
					for(DeviceConf dc:deviceConfs){
						cn.edu.ruc.conf.sys.Device device=new cn.edu.ruc.conf.sys.Device();
						Device cDevice = getDeviceById(dc.getDeviceRefId());
						device.setName(cDevice.getName());
						device.setNameTags(cDevice.getNameTags());
						device.setId(cDevice.getId());
						List<cn.edu.ruc.conf.sys.Sensor> sensors = device.getSensors();
						List<SensorConf> sensorConfs = cDevice.getSensors();
						if(sensorConfs!=null&&sensorConfs.size()>0){
							for(SensorConf sc:sensorConfs){
								cn.edu.ruc.conf.sys.Sensor sensor=new cn.edu.ruc.conf.sys.Sensor();
								sensor.setName(sc.getName());
								sensor.setStep(sc.getStep());
								sensor.setTags(sc.getSensorTags());
								sensor.setStartTime(DateUtil.datastr2Date(sc.getBeginTime()));
								sensor.setEndTime(DateUtil.datastr2Date(sc.getEndTime()));
								sensor.setId(sc.getSensorRefId());
								Sensor cSensor= getSensorById(sc.getSensorRefId());
								try {
									BeanUtils.copyProperties(sensor, cSensor);
								} catch (Exception e) {
									e.printStackTrace();
								}
								sensors.add(sensor);
							}
						}
						devices.add(device);
					}
				}
				dbs.add(db);
			}
		}
	}
	private static Sensor getSensorById(String sensorRefId) {
		if(SENSORS!=null&&SENSORS.size()>0){
			for(Sensor sensor:SENSORS){
				if(sensor.getId().equals(sensorRefId)){
					return sensor;
				}
			}
		}
		return null;
	}
	private static Device getDeviceById(String deviceRefId) {
		if(DEVICES!=null&&DEVICES.size()>0){
			for(Device device:DEVICES){
				if(device.getId().equals(deviceRefId)){
					return device;
				}
			}
		}
		return null;
	}
	public static OfflineConfig getOfflineConfig(){
		return OFFLINE_CONFIG;
	}
	public static OnlineConfig getOnlineConfig(){
		return ONLINE_CONFIG;
	}
	public static void main(String[] args) {
		InitManager.initConfig();
		System.out.println(ONLINE_CONFIG);
	}
	public static void initConfig() {
		initBaseConf();
		initOfflineConfig();
		initOnlineConf();
	}
}

