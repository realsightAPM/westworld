package apm.http;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import apm.globalinfo.HttpMethod;

public class DataProducer implements Runnable{
	
	private CloseableHttpClient  httpClient;
	
	private Map<String,String> params;
	
	private String url;
	
	private HttpMethod methodType;
	
	private DataProcess process;
	
	private volatile boolean stop = false;
	
	private ScheduledExecutorService scheduledThreadPool;
	
	public DataProducer(String url,HttpMethod methodType,Map<String,String> params,DataProcess process){
		httpClient = HttpClients.createDefault();
		this.params = params;
		this.process = process;
		this.methodType = methodType;
		this.url = url;
	}
	
	private String getDataFromURL(HttpUriRequest request) throws ClientProtocolException, IOException{
		CloseableHttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	}

	private HttpUriRequest createHttpUriRequest(String url,Map<String,String> params,HttpMethod method){
		HttpUriRequest request = null;
		switch(method){
			case GET:
			{
				StringBuffer getParams = new StringBuffer();
				for(Entry<String,String> entry : params.entrySet()){
					getParams.append(entry.getKey());
					getParams.append("=");
					getParams.append(entry.getValue());
					getParams.append("&");
				}
				url = url+"?"+getParams.toString();
				HttpGet get = new HttpGet(url);
				request = get;
			}break;
			case POST:
			{
				//
			}break;
		}
		return request;
	}
	
	public void run() {
		
		HttpUriRequest request = createHttpUriRequest(url,params,methodType);
			while(!stop){
				try {
					String message = getDataFromURL(request);
					if(params.containsKey("part"))
					{
						HttpData data = new HttpData(params.get("part"),message);
						process.process(data);
					}
				} catch (ClientProtocolException e) {
					//e.printStackTrace();
					System.out.println("ClientProtocolException");
					return;
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("IOException");
					return;
				}
			}
	}
	
	public void start(){
		//Thread thread = new Thread(this);
		//thread.start();
		scheduledThreadPool	= Executors.newScheduledThreadPool(1);
		scheduledThreadPool.scheduleWithFixedDelay(this, 0, 10, TimeUnit.SECONDS);
		
	}
	
	public void stop(){
		scheduledThreadPool.shutdownNow();
		this.stop=true;
	}
	
}
