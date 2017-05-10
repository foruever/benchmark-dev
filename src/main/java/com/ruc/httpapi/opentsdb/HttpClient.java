package com.ruc.httpapi.opentsdb;

import java.io.IOException;

import com.ruc.httpapi.opentsdb.builder.MetricBuilder;
import com.ruc.httpapi.opentsdb.request.QueryBuilder;
import com.ruc.httpapi.opentsdb.response.Response;
import com.ruc.httpapi.opentsdb.response.SimpleHttpResponse;


public interface HttpClient extends Client {

	public Response pushMetrics(MetricBuilder builder,
			ExpectResponse exceptResponse) throws IOException;

	public SimpleHttpResponse pushQueries(QueryBuilder builder,
                                          ExpectResponse exceptResponse) throws IOException;
}