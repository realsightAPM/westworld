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

public class SolrMetricApp {
	

	public List<ArrayList<Double>> app;
	public List<Double> appAver;
	public List<ArrayList<String>> appDisc;
	public List<String> attrList;
	public List<Integer> stateNums;
	
	private SolrMetricApp() {}
	
	public SolrMetricApp(long start, Statistic stat, MetricOption option) {
		int group_num = (int) (option.gap/option.interval);
		SolrMetricPid metricPid = new SolrMetricPid(start, stat, option);
		List<String> attrList_tmp = metricPid.app_list;
		List<Boolean> flag = new ArrayList<Boolean>();
		for (int i = 0; i < attrList_tmp.size(); i++) {
			flag.add(false);
		}

		List<ArrayList<Double>> app_tmp = new ArrayList<ArrayList<Double>>();
		app = new ArrayList<ArrayList<Double>>();
		appAver = new ArrayList<Double>();
		appDisc = new ArrayList<ArrayList<String>>();
		attrList = new ArrayList<String>();
		stateNums = new ArrayList<Integer>();
		for (int i = 0; i < attrList_tmp.size(); i++) {
			app_tmp.add(new ArrayList<Double>());
		}
		
		for (int i = 0; i < attrList_tmp.size(); i++) {
			for (int j = 0; j < group_num; j++) {
				double sum = 0;
				for (String it : metricPid.pidMap.get(attrList_tmp.get(i))) {
					sum += metricPid.pid.get(metricPid.map.get(it)).get(j);
				}
				if (sum/option.core > 0.001) flag.set(i, true);
				app_tmp.get(i).add(sum/option.core);
			}
		}
		
		for (int i = 0; i < attrList_tmp.size(); i++) {
			if (flag.get(i)) {
				attrList.add(attrList_tmp.get(i));
				app.add(app_tmp.get(i));
				appAver.add(stat.run(app_tmp.get(i)));
			}
		}
		
		Discretization disc = new Discretization();
		
		for (int i = 0; i < attrList.size(); i++) {
			appDisc.add(new ArrayList<String> ());
			stateNums.add(0);
			for (int j = 0; j < group_num; j++) {
				int pos = disc.run(app.get(i).get(j), 0.1, 0.2);
				if (pos > stateNums.get(i)) stateNums.set(i, pos);
				appDisc.get(i).add(""+((char)('a'+pos)));
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
