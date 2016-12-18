package com.realsight.brain.simulation.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.realsight.brain.simulation.globalinfo.HttpMethod;

public class DataProducer implements Runnable {

	private CloseableHttpClient httpClient;

	private Map<String, String> params;

	private String url;

	private HttpMethod methodType;

	private volatile boolean stop = false;

	public DataProducer(String url, HttpMethod methodType, Map<String, String> params) {
		httpClient = HttpClients.createDefault();
		this.params = params;
		this.methodType = methodType;
		this.url = url;
	}

	private String getDataFromURL(HttpUriRequest request) throws ClientProtocolException, IOException {
		CloseableHttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	}

	private HttpUriRequest createHttpUriRequest(String url, Map<String, String> params, HttpMethod method) {
		HttpUriRequest request = null;
		switch (method) {
		case GET: {
			HttpGet get = new HttpGet(url);
			request = get;
		}
			break;
		case POST: {
			//
		}
			break;
		}
		return request;
	}

	public void run() {

		HttpUriRequest request = createHttpUriRequest(url, params, methodType);
		while (!stop) {
			try {
				String message = getDataFromURL(request);
				
			} catch (ClientProtocolException e) {
//				e.printStackTrace();
				System.err.println("ClientProtocolException");
				return;
			} catch (IOException e) {
//				e.printStackTrace();
				System.err.println("IOException");
				return;
			}
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		this.stop = true;
	}

}
