package cn.edu.ruc.httpapi.opentsdb;

import java.io.IOException;

import cn.edu.ruc.httpapi.opentsdb.builder.MetricBuilder;
import cn.edu.ruc.httpapi.opentsdb.request.QueryBuilder;
import cn.edu.ruc.httpapi.opentsdb.response.Response;
import cn.edu.ruc.httpapi.opentsdb.response.SimpleHttpResponse;



public interface HttpClient extends Client {

	public Response pushMetrics(MetricBuilder builder,
			ExpectResponse exceptResponse) throws IOException;

	public SimpleHttpResponse pushQueries(QueryBuilder builder,
                                          ExpectResponse exceptResponse) throws IOException;
}