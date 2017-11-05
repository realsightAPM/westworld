package com.realsight.westworld.bnanalysis.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.algorithm.SortPairList;
import com.realsight.westworld.bnanalysis.api.MetricBeatApi;
import com.realsight.westworld.bnanalysis.api.NeticaApi;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.solr.MetricOption;
import com.realsight.westworld.bnanalysis.solr.SolrOneDoc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import norsys.netica.Node;

public class MetricBeatService {
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}
	
	public MetricBeatApi metric;
	public NeticaApi netica;
	public List<Pair<String, Double>> pointCpuList;
	public List<Pair<String, Double>> pointMemList;
	
	public MetricBeatService() {}
	
	public void runService(long start, MetricOption option, String hostname, Date myDate) throws Exception {
		netica = new NeticaApi();
		metric = new MetricBeatApi(start, option, hostname);
		netica.buildMetricBeat(metric);
		pointCpuList = new ArrayList<Pair<String, Double>>();
		pointMemList = new ArrayList<Pair<String, Double>>();
		
		SolrOneDoc resulter = new SolrOneDoc(option.writeUrl);
		
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", hostname));
		Calendar cal = Calendar.getInstance();
		long time_now = cal.getTimeInMillis();
		resulter.addResult(new Pair<String, Object> ("timestamp_l", time_now));
		resulter.addResult(new Pair<String, Object> ("time_s", (1900+myDate.getYear())+"/"+(1+myDate.getMonth())+"/"+myDate.getDate()));
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
		
		
		List<String> averList = new ArrayList<String>();
//		for (int i = 0; i < metric.averPairList.size(); i++) {
//			averList.add(metric.averPairList.get(i).first+":"+metric.averPairList.get(i).second);
//		}
//		resulter.addResult(new Pair<String, Object> ("averList_ss", averList));
		
		
		for (int i = 3; i < metric.attrList_.size(); i++) {
			String attr = metric.attrList_.get(i);
			String tar = "";
			if (attr.charAt(0) == 'c') {
				tar = "_1";
			} else if (attr.charAt(0) == 'm') {
				tar = "_2";
			}
			
			Node node = netica.net.getNode(attr);
			float[] beliefs = node.getBeliefs();
			int common = metric.getCommon(beliefs);
			double res1 = netica.getTheExpeption(attr, common, tar);
			double res = 0;
			for (int j = common+1; j < beliefs.length; j++) {
				res += netica.getTheExpeption(attr, j, tar);
			}
			
			if (tar.equals("_1")) {
				pointCpuList.add(new Pair<String, Double> (metric.attrList.get(i), (res-res1*(beliefs.length-1) < 0 ? 0 : res-res1*(beliefs.length-1))));
			} else if (tar.equals("_2")) {
				pointMemList.add(new Pair<String, Double> (metric.attrList.get(i), (res-res1*(beliefs.length-1) < 0 ? 0 : res-res1*(beliefs.length-1))));
			}
		}
		SortPairList sort = new SortPairList();
		sort.sort(pointCpuList);
		sort.sort(pointMemList);
		
//		for (int i = 0; i < pointPairList.size(); i++) {
//			System.out.println(pointPairList.get(i).first + ": " + pointPairList.get(i).second);
//		}
		
		List<String> pointList1 = new ArrayList<String>();
		for (int i = 0; i < pointCpuList.size(); i++) {
			pointList1.add(pointCpuList.get(i).first+"^"+pointCpuList.get(i).second);
			System.out.println(pointCpuList.get(i).first+":"+pointCpuList.get(i).second);
		}
		
		List<String> pointList2 = new ArrayList<String>();
		for (int i = 0; i < pointMemList.size(); i++) {
			pointList2.add(pointMemList.get(i).first+"^"+pointMemList.get(i).second);
			System.out.println(pointMemList.get(i).first+":"+pointMemList.get(i).second);
		}
		
		resulter.addResult(new Pair<String, Object> ("pointList1_ss", pointList1));
		resulter.addResult(new Pair<String, Object> ("pointList2_ss", pointList2));
		for (int i = 0; i < metric.attrList.size(); i++) {
			resulter.addResult(new Pair<String, Object> (metric.attrList_.get(i)+"_ff", metric.data.get(i)));
		}
		
		int group_num = (int) (option.gap/option.interval);
		
		List<String> stampList = new ArrayList<String>();
		
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTimeInMillis(option.startTime);
		
		for (int i = 0; i < group_num; i++) {
			stampList.add(rightNow.getTime().toLocaleString());
			rightNow.add(Calendar.MINUTE, 1);
			
		}
		
		resulter.addResult(new Pair<String, Object> ("stamp_list_ss", stampList));
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		resulter.addResult(new Pair<String, Object> ("data_s", sdf.format(rightNow.getTime())));
		
		resulter.write(); // 同步
	}
	
	public static void main(String[] args) throws Exception {
//		MetricBeatService service = new MetricBeatService("http://10.0.67.14:8080/solr/option", "bn_metrics11");
		
//		Calendar cal= Calendar.getInstance();
//		cal.add(Calendar.DATE, -1);
//		Date myDate = cal.getTime();
//		System.out.println(myDate.getYear() + ":" + myDate.getMonth() + ":" + myDate.getDate());
		
	}
}
