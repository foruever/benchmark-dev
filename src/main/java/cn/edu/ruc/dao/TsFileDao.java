package cn.edu.ruc.dao;

import java.util.List;

import cn.edu.ruc.model.BenchmarkPoint;

public class TsFileDao implements BaseDao {


	@Override
	public long insertMultiPoints(List<BenchmarkPoint> points) {
		return 0;
	}

	@Override
	public boolean deleteAllPoints() {
		return false;
	}

}

