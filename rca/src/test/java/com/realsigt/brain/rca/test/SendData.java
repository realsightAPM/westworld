package com.realsigt.brain.rca.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.realsight.brain.rca.server.RCAServer;
import com.realsight.brain.rca.util.Settings;
import com.realsight.brain.timeseries.lib.series.MultipleStringSeries;
import com.realsight.brain.timeseries.lib.series.StringSeries;
import com.realsight.brain.timeseries.lib.util.data.TimeseriesData;

public class SendData {
	
	private static final Logger logger = LogManager.getLogger(RCAServer.class.getName());
	
	public class RequestURL implements Runnable{
		
		String url = null;
		
		public RequestURL(String url) {
			this.url = url;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				ClientResource client = new ClientResource(url);
				String info = client.get().getText().toString();
				logger.debug("[ " + url + " ] " + info);
			} catch (ResourceException e) {
				// TODO Auto-generated catch block
				logger.error("[ " + url + " ] ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("[ " + url + " ] ");
			}
		}
		
	}
	
	private void sendSeries(MultipleStringSeries nSeries, String hosturl) {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);  
		try {
			for (int i = 0; i < nSeries.size(); i++ ) {
				JSONObject propertysJSON = new JSONObject();
				
				for (int j = 0; j < nSeries.getProperty_list().size(); j++) {
					String name = nSeries.getProperty_list().get(j);
					String value = nSeries.getColumn(name).get(i).getItem();
					propertysJSON.put(name, value);
				}
				propertysJSON.put("timestamp", nSeries.get(i).getInstant());
				String url = hosturl + "/" + URLEncoder.encode(propertysJSON.toString(), "UTF-8");
				fixedThreadPool.submit(new RequestURL(url));
				Thread.sleep(100);
			}
		} catch (ResourceException | UnsupportedEncodingException | JSONException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	
	private void sendSeries(String name, StringSeries nSeries, String hosturl) {
		MultipleStringSeries mSeries = new MultipleStringSeries(name, nSeries);
		sendSeries(mSeries, hosturl);
	}
	
	public void sendDigit(String property, String localPath) {
		String hosturl = Settings.getInstance().get("send.url") + "/group";
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);  
		fixedThreadPool.submit(new RequestURL(hosturl + "/delete/cpu"));
		fixedThreadPool.submit(new RequestURL(hosturl + "/create/cpu/value,timestamp"));
		hosturl = hosturl + "/upload/cpu";
		TimeseriesData td = new TimeseriesData(localPath);
		StringSeries nSeries = td.getPropertySeries("value");
		sendSeries(property, nSeries, hosturl);
	}
	
	public void sendLOG(String localPath) {
		String hosturl = Settings.getInstance().get("send.url");
		hosturl = hosturl + "/log/upload";
		TimeseriesData td = new TimeseriesData(localPath);
		MultipleStringSeries nSeries = td.getPropertySeries();
		sendSeries(nSeries, hosturl);
	}
	
	public static void main(String[] args) {
		String root = new File(System.getProperty("user.dir")).getPath();
//		String digitPath = Paths.get(root, "target", "data", "cpu.csv").toString();
//		new SendData().sendDigit("cpu", digitPath);
		String logPath = Paths.get(root, "target", "data", "log.csv").toString();
		new SendData().sendLOG(logPath);
	}
}
