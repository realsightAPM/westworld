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
import com.realsight.westworld.bnanalysis.basic.Discretization;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class MetricAppCpu {
	

	public List<ArrayList<Double>> app;
	public List<Double> appAver;
	public List<ArrayList<String>> appDisc;
	public List<String> attrList;
	public List<Integer> stateNums;
	
	private MetricAppCpu() {}
	
	public MetricAppCpu(SolrMetricPid metricPid) {
		List<String> attrList_tmp = metricPid.app_list;

		List<ArrayList<Double>> app_tmp = metricPid.app_cpu_tmp;
		app = new ArrayList<ArrayList<Double>>();
		appAver = new ArrayList<Double>();
		appDisc = new ArrayList<ArrayList<String>>();
		attrList = new ArrayList<String>();
		stateNums = new ArrayList<Integer>();
		
		for (int i = 0; i < attrList_tmp.size(); i++) {
			if (metricPid.flag.get(i)) {
				attrList.add(attrList_tmp.get(i));
				app.add(app_tmp.get(i));
				appAver.add(new Mean().run(app_tmp.get(i)));
			}
		}
		
		Discretization disc = new Discretization();
		
		for (int i = 0; i < attrList.size(); i++) {
			appDisc.add(new ArrayList<String> ());
			stateNums.add(0);
			for (int j = 0; j < metricPid.group_num; j++) {
				int pos = disc.run(app.get(i).get(j), 0.1, 0.2);
				if (pos > stateNums.get(i)) stateNums.set(i, pos);
				appDisc.get(i).add(""+((char)('a'+pos)));
			}
		}
	}
	
	public static void main(String[] args) {
		
	}
}
