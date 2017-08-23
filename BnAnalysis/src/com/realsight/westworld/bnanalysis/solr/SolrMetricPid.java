package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.basic.Discretization;

public class SolrMetricPid {
	public List<ArrayList<Double>> pid;
	public List<ArrayList<String>> pidDisc;
	public List<String> attrList;
	public List<String> app_list, pid_list;
	public Map<String, HashSet<String>> pidMap;
	public Map<String, Integer> map;
	
	private SolrMetricPid() {}
	
	public SolrMetricPid(long start, Statistic stat, MetricOption option) {
		app_list = new FacetApplication(option.readUrl).appList;
		pid_list = new FacetPid(option.readUrl).pidList;
		pidMap = new HashMap<String, HashSet<String>> ();
		for (String it : app_list) {
			pidMap.put(it, new HashSet<String> ());
		}
		
		map = new HashMap<String, Integer>();
		for (int i = 0; i < pid_list.size(); i++) {
			map.put(pid_list.get(i), i);
		}
		
		pid = new ArrayList<ArrayList<Double>>();
		pidDisc = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < pid_list.size(); i++) {
			pid.add(new ArrayList<Double>());
		}
		
		SolrClient solr = new HttpSolrClient.Builder(option.readUrl).build();
		int group_num = (int) (option.gap/option.interval);
		
		List<ArrayList<ArrayList<Double>>> data = new ArrayList<ArrayList<ArrayList<Double>>>();    // 原始数据
		for (int i = 0; i < pid_list.size(); i++) {
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
		
		List<String> fq = new ArrayList<String>(option.fq);
		fq.add("metricset_name_s:process");
		fq.add("rs_timestamp_tdt:[" + rs_start + " TO " + rs_end + "]");
		
		String[] fq_str = new String[fq.size()];
		for (int i = 0; i < fq_str.length; i++) {
			fq_str[i] = fq.get(i);
		}
		
		solrQuery.setFilterQueries(fq_str);
		solrQuery.setFields("system_process_pid_f", "system_process_name_s", "system_process_cpu_total_pct_f", "rs_timestamp_tdt");
		
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
		System.out.println("application docs.size(): " + docs.size());
		for (int j = 0; j < docs.size(); j++) {
			String val_name = ((Float) docs.get(j).get("system_process_pid_f")).toString();
			String app_name = (String) docs.get(j).get("system_process_name_s");
//			System.out.println("app名字：" + app_name);
//			if (!pidMap.keySet().contains(app_name)) continue;
			pidMap.get(app_name).add(val_name);
//			if (!map.keySet().contains(val_name)) continue;
			long timestamp_tmp = ((Date) docs.get(j).get("rs_timestamp_tdt")).getTime();
			
			int x = (int) ((timestamp_tmp-start)/option.interval);
			if (x >= group_num) continue;
			double tmp_double = (Float) docs.get(j).get("system_process_cpu_total_pct_f");
//				System.out.println(val_name+" "+map.get(val_name)+" "+x);
			data.get(map.get(val_name)).get(x).add(tmp_double);
		}
		
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < group_num; j++) {
				pid.get(i).add(stat.run(data.get(i).get(j)));
			}
		}
		
		Discretization disc = new Discretization();
		
		for (int i = 0; i < data.size(); i++) {
			pidDisc.add(new ArrayList<String> ());
			for (int j = 0; j < group_num; j++) {
				int pos = disc.run(pid.get(i).get(j), 0.05, 0.10, 0.2);
				pidDisc.get(i).add(""+((char)('a'+pos)));
			}
		}
		
	}
	
}
