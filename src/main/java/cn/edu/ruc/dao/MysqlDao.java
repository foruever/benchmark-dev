package cn.edu.ruc.dao;

import java.util.Date;
import java.util.List;

import cn.edu.ruc.model.BenchmarkPoint;

public class MysqlDao implements BaseDao {


	@Override
	public long insertMultiPoints(List<BenchmarkPoint> points) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean deleteAllPoints() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long selectPointsByTime(Date beginTime, Date endTime,
			String device, String sensor) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public long selectMaxByTimeAndDevice(String sqlId, Date beginTime, Date endTime, String device,
			List<String> sensors) {
		// TODO Auto-generated method stub
		return -1;
	}


}

