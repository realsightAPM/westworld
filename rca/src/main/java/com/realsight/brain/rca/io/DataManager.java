package com.realsight.brain.rca.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.realsight.brain.rca.util.TimeUtil;
import com.realsight.brain.timeseries.lib.series.StringSeries;
import com.realsight.brain.timeseries.lib.util.data.TimeseriesData;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;

public class DataManager {
	
	private static final Logger logger = LogManager.getLogger(DataManager.class.getName());
	private static DataManager instance = null;
	private static final String SOLR_URL = "http://localhost:8083/solr/rca";
	private HttpSolrClient solrClient = null;
	private ScheduledExecutorService scheduledThreadPool = null;
	private static final Long time_win = 1000L * 60L * 60L;
	
	private class Commit implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				DataManager.getInstance().solrClient.commit();
			} catch (SolrServerException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	} 
	
	private DataManager() throws SolrServerException, IOException {
		scheduledThreadPool = Executors.newScheduledThreadPool(2);
		this.solrClient = new HttpSolrClient.Builder(SOLR_URL).build();
		this.solrClient.setParser(new XMLResponseParser());
		scheduledThreadPool.scheduleAtFixedRate(new Commit(), 1, 30, TimeUnit.SECONDS);
	}

	private static synchronized void syncInit() throws SolrServerException, IOException {
		if (instance == null) {
			instance = new DataManager();
		}
	}

	public static DataManager getInstance() throws SolrServerException, IOException {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}
	
	public String[] getGroupPropertys(String group_name) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		query.setFields("rca_group_s", "rca_propertys_s");
		query.set(CommonParams.Q, "rca_group_s:"+group_name);
		
		QueryResponse response = solrClient.query(query);
		SolrDocumentList documents = response.getResults();
		Iterator<SolrDocument> iter = documents.iterator();
		String[] propertys = null;
        if(iter.hasNext()) {
            SolrDocument doc = iter.next();
            propertys = ((String) doc.getFieldValue("rca_propertys_s")).split(",");
        }
        return propertys;
	}
	
	public String deleteGroup(String group_name) throws SolrServerException, IOException{
        solrClient.deleteByQuery("rca_group_s:"+group_name);
        solrClient.deleteByQuery("rca_group_s:"+group_name+"_data");
        solrClient.commit();
        return "success";
	}
	
	public String deleteAll() throws SolrServerException, IOException{
        solrClient.deleteByQuery("*:*");
        solrClient.commit();
        return "success";
	}
	
	public String deleteLog() throws SolrServerException, IOException{
        solrClient.deleteByQuery("rca_log_s:log_data");
        solrClient.commit();
        return "success";
	}
	
	public String createGroup(String group_name, String propertys) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		query.setFields("rca_group_s");
		query.set(CommonParams.Q, "rca_group_s:"+group_name);
		
		QueryResponse response = solrClient.query(query);
		SolrDocumentList t_list = response.getResults();
		if (!t_list.isEmpty())
			return "error";
		SolrInputDocument g_document = new SolrInputDocument();
		g_document.addField("rca_group_s", group_name);
		g_document.addField("rca_propertys_s", propertys);
		this.solrClient.add(g_document);
		this.solrClient.commit();
        logger.debug("Create solr detection group " + group_name);
        return "success";
	}
	
	private void mergeGroupData(String group_name, JSONObject data) throws Exception {
		Long timestamp = Long.parseLong(data.getString("timestamp").trim());
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("rca_group_s", group_name);
		doc.addField("rca_timestamp_l", timestamp);
		doc.addField("rca_rs_timestamp_s", TimeUtil.formatUnixtime2(timestamp));
		@SuppressWarnings("unchecked")
		Iterator<String> preportys = data.keys();
		while(preportys.hasNext()) {
			String preporty = preportys.next();
			if (preporty.equals("timestamp")) continue;
			doc.addField("rca_" + preporty + "_f", data.optString(preporty));
//			System.out.println("rca_" + preporty + "_f" + " -> " + data.optString(preporty));
		}
		this.solrClient.add(doc);
	}
	
	private void mergeLogData(JSONObject data) throws Exception {
		Long timestamp = Long.parseLong(data.getString("timestamp").trim());
//		System.out.println("SBSBSB + " + timestamp);
		SolrInputDocument doc = new SolrInputDocument();
//		System.out.println("SBSBSB + " + 1);
		doc.addField("rca_log_s", "log_data");
//		System.out.println("SBSBSB + " + 2);
        doc.addField("rca_timestamp_l", timestamp);
//        System.out.println("SBSBSB + " + 3);
		doc.addField("rca_rs_timestamp_s", TimeUtil.formatUnixtime2(timestamp));
//		System.out.println("SBSBSB + " + 4);
        doc.addField("rca_url_s", data.optString("url"));
//        System.out.println("SBSBSB + " + 5);
        doc.addField("rca_message_type_s", data.optString("message_type"));
//        System.out.println("SBSBSB + " + 6);
        doc.addField("rca_message_class_s", data.optString("message_class"));
//        System.out.println("SBSBSB + " + 7);
        doc.addField("rca_message_content_s", data.optString("message_content"));
//        System.out.println("SBSBSB + " + 8);
//        System.out.println(solrClient);
		this.solrClient.add(doc);
//		this.solrClient.commit();
//		System.out.println("SBSBSB + " + timestamp);
	}
	
	public void test() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		query.setFields("id", "ts_timestamp", "url", "log");
		String Q = "url:/service/user/info ajax GET" + 
				" AND id:3211e707-7402-4d42-8033-2614d6edcc48";
		query.set(CommonParams.Q, Q);
		System.out.println("sb " + query.getQuery());
		QueryResponse response = solrClient.query(query);
		Iterator<SolrDocument> iter = response.getResults().iterator();
		System.out.println("////////////////////////////////////////");
		while(iter.hasNext()) {
			SolrDocument t = iter.next();
			Iterator<String> it = t.getFieldNames().iterator();
			while(it.hasNext()) {
				String name = it.next();
				System.out.print(name + ":");
				System.out.println(t.getFieldValues(name));
			}
			System.out.println("/////////////////////////////////////////");
		}
	}
	
	private void mergeRCARank(JSONObject rca) throws Exception {
		
		Long timestamp = Long.parseLong(rca.getString("timestamp").trim());
		timestamp = timestamp - timestamp%time_win;
		JSONArray RCARank = rca.getJSONArray("rank");
		for(int i = 0; i < RCARank.length(); i++) {
			JSONObject rank = RCARank.optJSONObject(i);
			String urlrca = rank.optString("url_s");
//			if ( this.urlrca_timestamp.containsKey(urlrca) ) {
//				Long t_timestamp = this.urlrca_timestamp.get(urlrca);
//				if (t_timestamp + time_win < timestamp) {
//					this.urlrca_timestamp.put(urlrca, t_timestamp-t_timestamp%time_win);
//					this.urlrca_score.put(urlrca, 0.0);
//				}
////				Double t_score = this.urlrca_score.get(urlrca);
////				if (t_score > score) continue;
//			} else {
//				this.urlrca_timestamp.put(urlrca, timestamp-timestamp%time_win);
//				this.urlrca_score.put(urlrca, 0.0);
//			}
			
			SolrQuery query = new SolrQuery();
			query.setFields("id");
			String Q = "rca_s:rca_data" + 
					" AND rca_timestamp_l:" + timestamp + 
					" AND rca_urlhashcode_l:" + Math.abs(urlrca.hashCode());
			System.out.println(Q);
			query.set(CommonParams.Q, Q);
			solrClient.deleteByQuery(query.getQuery());
			@SuppressWarnings("unchecked")
			Iterator<String> keys = rank.keys();
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("rca_s", "rca_data");
	        doc.addField("rca_timestamp_l", timestamp);
			doc.addField("rca_rs_timestamp_s", TimeUtil.formatUnixtime2(timestamp));
			doc.addField("rca_urlhashcode_l", Math.abs(urlrca.hashCode()));
			while(keys.hasNext()) {
				String key = keys.next();
				doc.addField("rca_" + key, rank.optString(key));
//				System.out.println(key + " " + rank.optString(key));
			}
			this.solrClient.add(doc);
		}
		
	}
	
	public String uploadLogData(String log_propertys) throws Exception {
		JSONObject propertysJSON = new JSONObject(log_propertys);
        mergeLogData(propertysJSON);
		return "success";
	}
	
	public String uploadGroupData(String group_name, String values, double score) throws Exception {
        JSONObject propertysJSON = new JSONObject(values);
        propertysJSON.put("as", score);
        System.out.println(propertysJSON.toString());
        mergeGroupData(group_name, propertysJSON);
		return "success";
	}
	
	public String uploadRCARankData(Long timestamp, JSONArray RCARank) throws Exception {
		JSONObject rca = new JSONObject();
		rca.put("rank", RCARank);
		rca.put("timestamp", timestamp);
        mergeRCARank(rca);
		return "success";
	}
	
	public void metric2solr(String name, String path) throws SolrServerException, IOException {
		TimeseriesData td = new TimeseriesData(path);
		StringSeries nSeries = td.getPropertySeries("value");
		for (int i = 0; i < nSeries.size(); i++ ) {
			String value = nSeries.get(i).getItem();
			Long timestamp = nSeries.get(i).getInstant();
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField(name+"_f", value);
	        doc.addField("rca_timestamp_l", timestamp);
			doc.addField("rca_rs_timestamp_s", TimeUtil.formatUnixtime2(timestamp));
			this.solrClient.add(doc);
			if (i%1000 == 0) {
				this.solrClient.commit();
			}
		}
		this.solrClient.commit();
	}
	
	public static void main(String[] args) throws Exception {
//		DataManager.getInstance().deleteAll();
		DataManager.getInstance().deleteGroup("cpu");
		DataManager.getInstance().deleteGroup("mem");
//		DataManager.getInstance().test();
//		DataManager.getInstance().deleteAll();
//		String root = new File(System.getProperty("user.dir")).getPath();
//		String digitPath = Paths.get(root, "target", "data", "cpu.csv").toString();
//		DataManager.getInstance().metric2solr("cpu", digitPath);
//		System.out.println(time_win);
//		String property_list = "value,timestamp";
//		DataManager.getInstance().createGroup("aaa", property_list);
//		for (int i = 0; i < 200; i ++) {
//			JSONObject values = new JSONObject();
//			Long hour = 1000L * 60L * 60L;
//			values.put("value", 1.0*i);
//			values.put("timestamp", i*hour/30);
//			DataManager.getInstance().uploadGroupData("aaa", values.toString(), 0.1*i);
//		}
//		
//		for (int i = 0; i < 200; i ++) {
//			JSONObject values = new JSONObject();
//			Long hour = 1000L * 60L * 60L;
//			values.put("value", 1.0*i);
//			values.put("timestamp", i*hour/30);
//			values.put("url", "/service/user/info ajax GET");
//			values.put("message_type", "WARN");
//			values.put("message_class", "[com.neusoft.chartsshow.security.user.service.external.OAuth2QQResourceManager]");
//			values.put("message_content", "[QQtokencheck]Non-positiveexpirationtoken.Checkpassed.");
//			System.out.println(values);
//			DataManager.getInstance().uploadLogData(values.toString());
//		}
//		
	}
}
