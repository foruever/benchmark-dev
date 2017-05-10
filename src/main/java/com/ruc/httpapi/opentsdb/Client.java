package com.ruc.httpapi.opentsdb;

import java.io.IOException;

import com.ruc.httpapi.opentsdb.builder.MetricBuilder;
import com.ruc.httpapi.opentsdb.request.QueryBuilder;
import com.ruc.httpapi.opentsdb.response.Response;
import com.ruc.httpapi.opentsdb.response.SimpleHttpResponse;


public interface Client {

	String PUT_POST_API = "/api/put";

    String QUERY_POST_API = "/api/query";
    String DELETE_POST_API = "/api/query?method_override=delete";
    
    String DROP_POST_API="api/dropcaches";

	/**
	 * Sends metrics from the builder to the KairosDB server.
	 *
	 * @param builder
	 *            metrics builder
	 * @return response from the server
	 * @throws IOException
	 *             problem occurred sending to the server
	 */
	Response pushMetrics(MetricBuilder builder) throws IOException;

	SimpleHttpResponse pushQueries(QueryBuilder builder) throws IOException;
	SimpleHttpResponse deleteQueries(QueryBuilder builder) throws IOException;
	SimpleHttpResponse dropCaches() throws IOException;
}