package com.realsight.westworld.engine.server;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.realsight.westworld.engine.util.Settings;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@SuppressWarnings("deprecation")
@Service
public class RCAServer {
	
	private static final String RCAURL = Settings.getInstance().get("rca.url");
	private static final String SOLR_URL = "http://localhost:8083/solr/rca";
	private HttpClient client = new DefaultHttpClient();
	private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);
	
	public class SendMetric implements Runnable{
		
		String metric = null;
		private Long start_timestamp = 0L;
		private final int rows = 100;
		private HttpSolrClient solrClient = null;
		
		public SendMetric(String metric) {
			this.metric = metric;
			this.solrClient = new HttpSolrClient.Builder(SOLR_URL).build();
			this.solrClient.setParser(new XMLResponseParser());
		}
		
		public boolean update() throws ClientProtocolException, IOException, SolrServerException {
			boolean hasnext = false;
			String Q = "rca_timestamp_l:[" + start_timestamp + " TO *]";
			String FQ1 = this.metric+"_f:*";
			SolrQuery query = new SolrQuery();
			query.setFields(this.metric+"_f", "rca_timestamp_l");
			query.setQuery(Q);
			query.setFilterQueries(FQ1);
			query.setRows(rows);
			query.addSort("rca_timestamp_l", ORDER.asc);
			QueryResponse response = this.solrClient.query(query);			
			Iterator<SolrDocument> iter = response.getResults().iterator();
			while(iter.hasNext()) {
				SolrDocument doc = iter.next();
				this.start_timestamp = (Long) doc.getFieldValue("rca_timestamp_l");
				float value = (float) doc.getFieldValue(this.metric+"_f");
				String hosturl = RCAURL + "/group/upload/" + metric + "/";
				JSONObject metric_json = new JSONObject();
				metric_json.put("value", value);
				metric_json.put("timestamp", start_timestamp);
				HttpGet get = new HttpGet(hosturl + URLEncoder.encode(metric_json.toString(), "UTF-8"));
				
				HttpResponse resp = client.execute(get);
				Scanner sin = new Scanner(resp.getEntity().getContent());
				System.out.println(get);
				while(sin.hasNext()) {
					String req = sin.nextLine();
					System.out.println(req);
				}
				sin.close();
				hasnext = true;
				start_timestamp += 1L;
			}
			return hasnext;
		}
		
		@Override
		public void run() {
			try {
				while(update());
			} catch (IOException | SolrServerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String createMetric(String name){
		String hosturl = RCAURL + "/group/create/" + name + "/value,timestamp";
		HttpGet get = new HttpGet(hosturl);
		try {
			HttpResponse response = client.execute(get);
			System.out.println(hosturl + " " + response.getStatusLine().getStatusCode());
			Scanner sin = new Scanner(response.getEntity().getContent());
			String req = sin.next();
			if (req.equals("success")){
				scheduledThreadPool.scheduleAtFixedRate(new SendMetric(name), 0, 30, TimeUnit.SECONDS);
			}
			sin.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "wa";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "wa";
		}
		return "ac";
	}
	
	public static void main(String[] args) {
		RCAServer rca = new RCAServer();
		rca.createMetric("mem");
	}
}
