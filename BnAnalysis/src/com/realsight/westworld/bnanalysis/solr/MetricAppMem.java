package com.realsight.westworld.bnanalysis.solr;

import java.util.ArrayList;
import java.util.List;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.basic.Discretization;
import com.realsight.westworld.bnanalysis.statistic.Mean;

public class MetricAppMem {

	public List<ArrayList<Double>> app;
	public List<Double> appAver;
	public List<ArrayList<String>> appDisc;
	public List<String> attrList;
	public List<Integer> stateNums;
	
	private MetricAppMem() {}
	
	public MetricAppMem(SolrMetricPid metricPid) {
		List<String> attrList_tmp = metricPid.app_list;

		List<ArrayList<Double>> app_tmp = metricPid.app_mem_tmp;
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
				int pos = disc.run(app.get(i).get(j), 0.05, 0.15);
				if (pos > stateNums.get(i)) stateNums.set(i, pos);
				appDisc.get(i).add(""+((char)('a'+pos)));
			}
		}
	}
	
}
