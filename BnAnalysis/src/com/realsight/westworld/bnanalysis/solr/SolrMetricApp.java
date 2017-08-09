package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SolrMetricApp {
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}

	List<ArrayList<Double>> app;
	List<ArrayList<String>> appSeparate;
	
	private SolrMetricApp() {}
	
	public SolrMetricApp(long start, Statistic stat, MetricOption option) {
		
		List<String> app_list = new FacetApplication(option.readUrl).appList;          // 获取的应用列表
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < app_list.size(); i++) {
//			System.out.println(i + ": " + app_list.get(i));
			map.put(app_list.get(i), i);
		}
		
		app = new ArrayList<ArrayList<Double>>();
		appSeparate = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < app_list.size(); i++) {
			app.add(new ArrayList<Double>());
			appSeparate.add(new ArrayList<String>());
		}
		
		SolrClient solr = new HttpSolrClient.Builder(option.readUrl).build();
		int group_num = (int) (option.gap/option.interval);
		
		List<ArrayList<ArrayList<Double>>> data = new ArrayList<ArrayList<ArrayList<Double>>>();    // 原始数据
		for (int i = 0; i < app_list.size(); i++) {
			data.add(new ArrayList<ArrayList<Double>> ());
			for (int j= 0; j < group_num; j++) {
				data.get(i).add(new ArrayList<Double> ());
			}
		}
		
		// 查询
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		String rs_start = TimeUtil.formatUnixtime2(start);
		String rs_end = TimeUtil.formatUnixtime2(start+option.gap);
		
		List<String> fq = option.fq;
		fq.add("metricset_name_s:process");
		fq.add("rs_timestamp_tdt:[" + rs_start + " TO " + rs_end + "]");
		
		String[] fq_str = new String[fq.size()];
		for (int i = 0; i < fq_str.length; i++) {
			fq_str[i] = fq.get(i);
		}
		
		solrQuery.setFilterQueries(fq_str);
		solrQuery.setFields("system_process_name_s", "system_process_cpu_total_pct_f", "rs_timestamp_tdt");
		
		solrQuery.setRows(2000000);
		solrQuery.setSort("rs_timestamp_tdt", ORDER.asc);
		  // End 查询
		
		QueryResponse response = null;
		
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (Exception e) {
				System.out.print("网络read异常");
				e.printStackTrace();
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		try {
			solr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		SolrDocumentList docs = response.getResults();
		System.out.println("docs.size(): " + docs.size());
		for (int j = 0; j < docs.size(); j++) {
			String val_name = (String) docs.get(j).get("system_process_name_s");
			
			long timestamp_tmp = ((Date) docs.get(j).get("rs_timestamp_tdt")).getTime();
			
			int x = (int) ((timestamp_tmp-start)/option.interval);
			if (x >= group_num) continue;
			double tmp_double = (Float) docs.get(j).get("system_process_cpu_total_pct_f");
//				System.out.println(val_name+" "+map.get(val_name)+" "+x);
			data.get(map.get(val_name)).get(x).add(tmp_double);
		}
		
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < group_num; j++) {
				app.get(i).add(stat.run(data.get(i).get(j)));
			}
		}
		
	}
	
	public static void main(String[] args) {
		MetricOption option = new MetricOption("http://10.0.67.14:8080/solr/option", "bn_metrics3");
		SolrMetricApp metricApp = new SolrMetricApp(option.startTime, new Mean(), option);
		int col = metricApp.app.size();
		int row = metricApp.app.get(0).size();
		
//		for (int i = 0; i < row; i++) {
//			for (int j = 0; j < col; j++) {
//				System.out.print(metricApp.app.get(j).get(i) + "  ");
//			}
//			System.out.println();
//		}
	}
}
