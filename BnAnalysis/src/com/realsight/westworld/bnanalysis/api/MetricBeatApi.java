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
import com.realsight.westworld.bnanalysis.solr.SolrMetricApp;
import com.realsight.westworld.bnanalysis.solr.SolrMetricCpu;
import com.realsight.westworld.bnanalysis.solr.SolrMetricLoad;
import com.realsight.westworld.bnanalysis.solr.SolrResults;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import norsys.netica.Node;

public class MetricBeatApi {
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}

	public List<ArrayList<Double>> data;
	public List<ArrayList<String>> dataDisc;
	public List<String> attrList;
	public List<ArrayList<Integer>> parentMap;
	public List<String> attrList_;
	public List<Integer> states;
	public List<Pair<String, Double>> averPairList;
	
	public MetricBeatApi(MetricOption option) throws IOException {
		data = new ArrayList<ArrayList<Double>>();
		dataDisc = new ArrayList<ArrayList<String>>();
		attrList = new ArrayList<String>();
		states = new ArrayList<Integer>();
		SolrMetricLoad metricLoad = new SolrMetricLoad(option.startTime, new Mean(), option);
		SolrMetricCpu metricCpu = new SolrMetricCpu(option.startTime, new Mean(), option);
		SolrMetricApp metricApp = new SolrMetricApp(option.startTime, new Mean(), option);
		
		attrList.add("load");
		states.add(metricLoad.stateNum+1);
		attrList.add("cpu");
		states.add(metricCpu.stateNum+1);
		for (int i = 0; i < metricApp.attrList.size(); i++) {
			attrList.add(metricApp.attrList.get(i));
			states.add(metricApp.stateNums.get(i)+1);
		}
		attrList_ = new ArrayList<String>();
		for (int i = 0; i <attrList.size(); i++) {
			attrList_.add("_"+i);
		}
		
		data.add((ArrayList<Double>) metricLoad.load);
		dataDisc.add((ArrayList<String>) metricLoad.loadDisc);
		data.add((ArrayList<Double>) metricCpu.cpu);
		dataDisc.add((ArrayList<String>) metricCpu.cpuDisc);
		averPairList = new ArrayList<Pair<String, Double>>();
		for (int i = 0; i < metricApp.app.size(); i++) {
			data.add(metricApp.app.get(i));
			dataDisc.add(metricApp.appDisc.get(i));
		}
		
		parentMap = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < 2; i++) {
			parentMap.add(new ArrayList<Integer>());
		}
		parentMap.get(0).add(1);
		for (int i = 0; i < metricApp.attrList.size(); i++) {
			parentMap.get(1).add(i+2);
		}
		
		System.out.println("CPU平均占用率：");
		for (int i = 0; i < metricApp.attrList.size(); i++) {
			averPairList.add(new Pair<String, Double> (metricApp.attrList.get(i), metricApp.appAver.get(i)));
		}
		SortPairList sort = new SortPairList();
		sort.sort(averPairList);
		for (int i = 0; i < averPairList.size(); i++) {
			System.out.println(averPairList.get(i).first + ": " + averPairList.get(i).second);
		}
		
		WriteCSV csvWriter = new WriteCSV();
		csvWriter.writeArrayCSV(data, attrList, ".", "data.csv");
		WriteCAS casWriter = new WriteCAS();
		casWriter.writeCAS(dataDisc, attrList_, "metric.cas");
		
	}
	
	int getCommon(float[] dist) {
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
	
	
	public static void main(String[] args) throws Exception {
		MetricOption option = new MetricOption("http://10.0.67.14:8080/solr/option", "bn_metrics7");
		NeticaApi netica = new NeticaApi();
		MetricBeatApi metric = new MetricBeatApi(option);
		netica.buildMetricBeat(metric);
		
		SolrResults resulter = new SolrResults(option.writeUrl);
		
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "example1"));
		long time_now = Calendar.getInstance().getTimeInMillis();
		resulter.addResult(new Pair<String, Object> ("timestamp_l", time_now));
		resulter.addResult(new Pair<String, Object> ("start_timestamp_l", time_now));
		resulter.addResult(new Pair<String, Object> ("nodes_s", netica.getGoNodes().toString()));
		resulter.addResult(new Pair<String, Object> ("edges_s", netica.getGoLinks().toString()));
		
		String query_list = "";
		for (int i = 0; i < metric.attrList.size(); i++) {
			query_list += metric.attrList.get(i);
			if (i < metric.attrList.size()-1)
				query_list += "^";
		}
		resulter.addResult(new Pair<String, Object> ("query_list_s", query_list));
		resulter.write(); // 同步
		
		for (int i = 2; i < metric.attrList_.size(); i++) {
			
			
			Node node = netica.net.getNode(metric.attrList_.get(i));
			float[] beliefs = node.getBeliefs();
			
			int common = metric.getCommon(beliefs);
			
			double res1 = netica.getTheExpeption(metric.attrList_.get(i), common, "_1");
			
			double res = 0, sum = 0;
			for (int j = common+1; j < beliefs.length; j++){
				sum += beliefs[j];
			}
			
			for (int j = common+1; j < beliefs.length; j++) {
				res += netica.getTheExpeption(metric.attrList_.get(i), j, "_1");
			}
			System.out.println(metric.attrList.get(i) + " : " + (res-res1*(beliefs.length-1) < 0 ? 0 : res-res1*(beliefs.length-1)));
		}
	}
}
