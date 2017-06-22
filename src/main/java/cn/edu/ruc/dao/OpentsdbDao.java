package cn.edu.ruc.dao;

import java.util.Date;
import java.util.List;

import cn.edu.ruc.conf.base.device.Tag;
import cn.edu.ruc.httpapi.opentsdb.ExpectResponse;
import cn.edu.ruc.httpapi.opentsdb.HttpClient;
import cn.edu.ruc.httpapi.opentsdb.HttpClientImpl;
import cn.edu.ruc.httpapi.opentsdb.builder.MetricBuilder;
import cn.edu.ruc.httpapi.opentsdb.response.Response;
import cn.edu.ruc.model.BenchmarkPoint;

public class OpentsdbDao implements BaseDao {


	@Override
	public long insertMultiPoints(List<BenchmarkPoint> points) {
		MetricBuilder builder = MetricBuilder.getInstance();
		HttpClient client = new HttpClientImpl("");
		for(BenchmarkPoint point:points){
			StringBuilder pathBuffer=new StringBuilder();
			List<Tag> deviceTags = point.getDeviceTags();
			for(Tag tag:deviceTags){
				pathBuffer.append(tag.getValue());
				pathBuffer.append(".");
			}
			pathBuffer.append(point.getDeviceName());
			builder.addMetric(pathBuffer.toString()).setDataPoint(point.getTimestamp(),point.getValue().doubleValue());
		}
		try {
			Response response = client.pushMetrics(builder,ExpectResponse.DETAIL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean deleteAllPoints() {
		return false;
	}

	@Override
	public List<Object> selectPointsByTime(Date beginTime, Date endTime,
			String device, String sensor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object selectMaxByTimeAndDevice(String sqlId, Date beginTime, Date endTime, String device,
			List<String> sensors) {
		// TODO Auto-generated method stub
		return null;
	}

}

