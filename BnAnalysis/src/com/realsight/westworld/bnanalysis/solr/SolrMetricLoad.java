package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.basic.Discretization;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SolrMetricLoad {


	public List<Double> load;
	public List<String> loadDisc;
	public int stateNum;
	
	private SolrMetricLoad() {}
	
	public SolrMetricLoad(long start, Statistic stat, MetricOption option) {
		
		load = new ArrayList<Double>();
		loadDisc = new ArrayList<String>();
		List<String> loadSeparate = new ArrayList<String>();
		
		SolrClient solr = new HttpSolrClient.Builder(option.readUrl).build();
		
		int group_num = (int) (option.gap/option.interval);
		
		List<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < group_num; i++) {
			data.add(new ArrayList<Double>());
		}
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		
		String rs_start = TimeUtil.formatUnixtime2(start);
		String rs_end = TimeUtil.formatUnixtime2(start+option.gap);
		
		List<String> fq = new ArrayList<String>(option.fq);
		fq.add("metricset_name_s:load");
		fq.add("rs_timestamp_tdt:[" + rs_start + " TO " + rs_end + "]");
		
		String[] fq_str = new String[fq.size()];
		for (int i = 0; i < fq_str.length; i++) {
			fq_str[i] = fq.get(i);
		}
		
		solrQuery.setFilterQueries(fq_str);
		solrQuery.setFields("system_load_norm_1_f", "rs_timestamp_tdt");
		
		solrQuery.setRows(2000000);
		solrQuery.setSort("rs_timestamp_tdt", ORDER.asc);
		
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
		System.out.println("load docs.size(): " + docs.size());
		for (int i = 0; i < docs.size(); i++) {
			long timestamp_tmp = ((Date) docs.get(i).get("rs_timestamp_tdt")).getTime();
			int x = (int) ((timestamp_tmp-start)/option.interval);
			if (x >= group_num) continue;
			double tmp_double = (Float) docs.get(i).get("system_load_norm_1_f");
			data.get(x).add(tmp_double);
		}
		
		for (int i = 0; i < group_num; i++) {
			load.add(stat.run(data.get(i)));
		}
		
		Discretization disc = new Discretization();
		stateNum = 0;
		for (int i = 0; i < group_num; i++) {
			int pos = disc.run(load.get(i), 0.5, 2);
			if (pos > stateNum) stateNum = pos;
			loadDisc.add("" + ((char) ('a'+pos)));
		}
	}
	
	public static void main(String[] args) {
		MetricOption option = new MetricOption("http://10.0.67.14:8080/solr/option", "bn_metrics3");
		SolrMetricLoad metricLoad = new SolrMetricLoad(option.startTime, new Mean(), option);
		
		for (Double it : metricLoad.load) {
			System.out.println(it);
		}
	}
	
}
