package cn.edu.ruc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import cn.edu.ruc.conf.init.InitManager;
import cn.edu.ruc.conf.sys.Database;
import cn.edu.ruc.conf.sys.Device;
import cn.edu.ruc.conf.sys.OnlineConfig;
import cn.edu.ruc.conf.sys.Sensor;
import cn.edu.ruc.dao.BaseDao;
import cn.edu.ruc.dao.DaoFactory;
import cn.edu.ruc.function.Function;
import cn.edu.ruc.model.BenchmarkPoint;

public class OnlineService implements BaseOnlineService{
	private static ExecutorService pool;
	@Override
	public void insertPerform() {
		OnlineConfig config = InitManager.getOnlineConfig();
		List<Database> dbs = config.getDatabases();
		for(Database db:dbs){
			String type = db.getType();
			System.out.println("type:"+type);
			List<BenchmarkPoint> points=new ArrayList<BenchmarkPoint>();
			List<Device> devices = db.getDevices();
			for(int i=0;i<devices.size();i++){
				Device device=devices.get(i);
				List<Sensor> sensors = device.getSensors();
				for(Sensor sensor:sensors){
					long startTime = sensor.getStartTime().getTime();
					long endTime =sensor.getEndTime().getTime();
					Double max = sensor.getMax();
					Double min = sensor.getMin();
					Long cycle = sensor.getCycle();
					String functionId = sensor.getFuctionRefId();
					Long step = sensor.getStep();
					while(startTime<=endTime){
						BenchmarkPoint point=new BenchmarkPoint();
						point.setDeviceName(device.getName()+(i+1));
						point.setDeviceTags(device.getNameTags());
						point.setSensorName(sensor.getName());
						point.setSensorTags(sensor.getTags());
						point.setTimestamp(startTime);
						point.setValue(Function.getValueByFuntionidAndParam(functionId, max, min, cycle, startTime));
						points.add(point);
						startTime+=step;
//						System.out.println(point);
					}
				}
				BaseDao dao = DaoFactory.getDao(type);
				long costTime = dao.insertMultiPoints(points);
				dao.deleteAllPoints();
				if(costTime<1000L){
					costTime=1000L;
				}
				System.out.println(points.size()+":"+(points.size()*1000/costTime)+" points/s");
			}
		}
	}
}
