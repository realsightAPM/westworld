package com.realsight.westworld.tsp.sever;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.json.JSONObject;

import com.realsight.westworld.tsp.api.OnlineAnomalyDetectionAPI;
import com.realsight.westworld.tsp.lib.series.TimeSeries;
import com.realsight.westworld.tsp.lib.solr.SolrReader;
import com.realsight.westworld.tsp.lib.solr.SolrWriter;
import com.realsight.westworld.tsp.lib.util.Entry;
import com.realsight.westworld.tsp.lib.util.TimeUtil;

public class ServerAnomaly implements Runnable{

	private Long spleep_time = 1000L * 60 * 60;
	private SolrReader sr = null;
	private SolrWriter sw = null;
	
	public ServerAnomaly(SolrReader sr, SolrWriter sw) {
		this.sr = sr;
		this.sw = sw;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run(){
		OnlineAnomalyDetectionAPI oad = new OnlineAnomalyDetectionAPI();
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
				double value = json.optDouble("maxYVal_d");
				Long timestamp = json.optLong("timestamp_l");
				TimeSeries.Entry<Double> ad = oad.detection(value, timestamp);
				double realAnomaly = 0;
				if (ad != null) {
					realAnomaly = oad.detection(value, timestamp).getItem();
				}
				try {
					sw.write( new Entry<String, String> ("analysis_s", json.optString("series_name_s")),
							new Entry<String, Double> ("anomaly_value_d", realAnomaly),
							new Entry<String, Long> ("timestamp_l", timestamp), 
							new Entry<String, Double> ("value_d", value),
							new Entry<String, String> ("timestamp_s", TimeUtil.formatUnixtime2(timestamp)),
							new Entry<String, String> ("rs_timestamp", TimeUtil.formatUnixtime2(timestamp)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
