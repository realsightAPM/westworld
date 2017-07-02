package com.realsight.westworld.tsp.sever;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.json.JSONObject;

import com.realsight.westworld.tsp.lib.model.LoggerAnalysis;
import com.realsight.westworld.tsp.lib.solr.SolrReader;
import com.realsight.westworld.tsp.lib.solr.SolrWriter;
import com.realsight.westworld.tsp.lib.util.Entry;
import com.realsight.westworld.tsp.lib.util.TimeUtil;

public class ServerLogger implements Runnable{

	private Long spleep_time = 1000L * 60 * 30;
	private SolrReader sr = null;
	private SolrWriter sw = null;
	
	public ServerLogger(SolrReader sr, SolrWriter sw) {
		this.sr = sr;
		this.sw = sw;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run(){
		try {
			LoggerAnalysis la = new LoggerAnalysis(spleep_time);
			while(true){
				if (! sr.hasNextResponse()) {
					try {
						sw.flush();
						Thread.sleep(spleep_time);
					} catch (InterruptedException | SolrServerException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				try {
					JSONObject json = new JSONObject(sr.nextResponse());
					Long timestamp = json.optLong("timestamp_l");
					String application = json.optString("application_s");
					String traceId = json.optString("traceId_s");
					String elapsed = json.optString("elapsed_s");
					String exception = json.optString("exception_s");
					try{
						la.analysis(application, "#", Long.parseLong(elapsed.trim()), timestamp);
						while(la.hasNext()) {
							Entry<String, Entry<Entry<Double, Double>, Entry<Double, Double>>> entry = la.next();
							Entry<String, String> entry0 = new Entry<String, String>("analysis_s", "LOGGER");
							Entry<String, Long> entry1 = new Entry<String, Long>("timestamp_l", timestamp);
							Entry<String, String> entry2 = new Entry<String, String>("rs_timestamp", TimeUtil.formatUnixtime2(timestamp));
							Entry<String, String> entry3 = new Entry<String, String>("application_s", application);
							Entry<String, String> entry4 = new Entry<String, String>("traceId_s", traceId);
							Entry<String, Double> entry5 = new Entry<String, Double>("elapsed_f", Double.parseDouble(elapsed.trim()));
							Entry<String, Double> entry6 = new Entry<String, Double>("visit_f",entry.getSecond().getSecond().getFirst());
							Entry<String, String> entry7 = new Entry<String, String>("exception_s", exception);
							Entry<String, Double> entry8 = new Entry<String, Double>("elapsed_anomaly_f", entry.getSecond().getFirst().getSecond());
							Entry<String, Double> entry9 = new Entry<String, Double>("visit_anomaly_f", entry.getSecond().getSecond().getSecond());
							sw.write(entry0, entry1, entry2, entry3, entry4, entry5, entry6, entry7, entry8, entry9);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
