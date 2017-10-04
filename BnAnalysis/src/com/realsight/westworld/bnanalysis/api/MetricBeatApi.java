package com.realsight.westworld.bnanalysis.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.algorithm.SortPairList;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.io.WriteCAS;
import com.realsight.westworld.bnanalysis.io.WriteCSV;
import com.realsight.westworld.bnanalysis.solr.MetricOption;
import com.realsight.westworld.bnanalysis.solr.MetricAppCpu;
import com.realsight.westworld.bnanalysis.solr.MetricAppMem;
import com.realsight.westworld.bnanalysis.solr.SolrMetricCpu;
import com.realsight.westworld.bnanalysis.solr.SolrMetricLoad;
import com.realsight.westworld.bnanalysis.solr.SolrMetricMem;
import com.realsight.westworld.bnanalysis.solr.SolrMetricPid;
import com.realsight.westworld.bnanalysis.solr.SolrOneDoc;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import norsys.netica.Node;

public class MetricBeatApi {

	public List<ArrayList<Double>> data;
	public List<ArrayList<String>> dataDisc;
	public List<String> attrList;
	public List<ArrayList<Integer>> parentMap;
	public List<String> attrList_;
	public List<Integer> states;
	public List<Pair<String, Double>> averPairList;
	
	public MetricBeatApi(long start, MetricOption option, String hostname) throws IOException {
		data = new ArrayList<ArrayList<Double>>();
		dataDisc = new ArrayList<ArrayList<String>>();
		attrList = new ArrayList<String>();
		states = new ArrayList<Integer>();
		SolrMetricLoad metricLoad = new SolrMetricLoad(start, new Mean(), option, hostname);
		SolrMetricCpu metricCpu = new SolrMetricCpu(start, new Mean(), option, hostname);
		SolrMetricMem metricMem = new SolrMetricMem(start, new Mean(), option, hostname);
		SolrMetricPid metricPid = new SolrMetricPid(start, new Mean(), option, hostname);
		MetricAppCpu metricAppCpu = new MetricAppCpu(metricPid);
		MetricAppMem metricAppMem = new MetricAppMem(metricPid);
		
		attrList_ = new ArrayList<String>();
		attrList.add("load");
		attrList_.add("_"+0);
		states.add(metricLoad.stateNum+1);
		attrList.add("cpu");
		attrList_.add("_"+1);
		states.add(metricCpu.stateNum+1);
		attrList.add("memory");
		attrList_.add("_"+2);
		states.add(metricMem.stateNum+1);
		for (int i = 0; i < metricAppCpu.attrList.size(); i++) {
			attrList.add(metricAppCpu.attrList.get(i));
			attrList_.add("c"+(3+i));
			states.add(metricAppCpu.stateNums.get(i)+1);
		}
		for (int i = 0; i < metricAppMem.attrList.size(); i++) {
			attrList.add(metricAppMem.attrList.get(i));
			attrList_.add("m"+(3+i));
			states.add(metricAppMem.stateNums.get(i)+1);
		}
//		attrList_ = new ArrayList<String>();
//		for (int i = 0; i <attrList.size(); i++) {
//			attrList_.add("_"+i);
//		}
		
		data.add((ArrayList<Double>) metricLoad.load);
		dataDisc.add((ArrayList<String>) metricLoad.loadDisc);
		data.add((ArrayList<Double>) metricCpu.cpu);
		dataDisc.add((ArrayList<String>) metricCpu.cpuDisc);
		data.add((ArrayList<Double>) metricMem.mem);
		dataDisc.add((ArrayList<String>) metricMem.memDisc);

		for (int i = 0; i < metricAppCpu.app.size(); i++) {
			data.add(metricAppCpu.app.get(i));
			dataDisc.add(metricAppCpu.appDisc.get(i));
		}
		for (int i = 0; i < metricAppMem.app.size(); i++) {
			data.add(metricAppMem.app.get(i));
			dataDisc.add(metricAppMem.appDisc.get(i));
		}
		
		parentMap = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < 3; i++) {
			parentMap.add(new ArrayList<Integer>());
		}
		parentMap.get(0).add(1);
		parentMap.get(0).add(2);
		for (int i = 0; i < metricAppCpu.attrList.size(); i++) {
			parentMap.get(1).add(i+3);
		}
		for (int i = 0; i < metricAppMem.attrList.size(); i++) {
			parentMap.get(2).add(3+metricAppCpu.attrList.size()+i);
		}
		
//		averPairList = new ArrayList<Pair<String, Double>>();
//		for (int i = 0; i < metricAppCpu.attrList.size(); i++) {
//			averPairList.add(new Pair<String, Double> (metricAppCpu.attrList.get(i), metricAppCpu.appAver.get(i)));
//		}
//		SortPairList sort = new SortPairList();
//		sort.sort(averPairList);
		
		WriteCSV csvWriter = new WriteCSV();
		csvWriter.writeArrayCSV(data, attrList, ".", "data.csv");
		WriteCAS casWriter = new WriteCAS();
		casWriter.writeCAS(dataDisc, attrList_, "metric.cas");
		
	}
	
	public int getCommon(float[] dist) {
		int pos = 0;
		double max_val = dist[0];
		for (int i = 1; i < dist.length; i++) {
			if (dist[i] > max_val) {
				pos = i;
				max_val = dist[i];
			}
		}
		return pos;
	}
	
}
