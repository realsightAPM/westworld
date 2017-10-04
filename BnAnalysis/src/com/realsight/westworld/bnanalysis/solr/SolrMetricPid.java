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
	public List<ArrayList<Double>> pidCPU;
	public List<ArrayList<Double>> pidMem;
	public List<String> app_list, pid_list;
	public Map<String, HashSet<String>> pidMap;
	public Map<String, Integer> map;
	public List<Boolean> flag;
	List<ArrayList<Double>> app_cpu_tmp;
	List<ArrayList<Double>> app_mem_tmp;
	public int group_num;
	
	private SolrMetricPid() {}
	
	public SolrMetricPid(long start, Statistic stat, MetricOption option, String hostname) {
		app_list = new FacetApplication(option.readUrl).appList;
		pid_list = new FacetPid(option.readUrl).pidList;
		pidMap = new HashMap<String, HashSet<String>> ();
		for (String it : app_list) {                                    // 每个应用所对应的进程集
			pidMap.put(it, new HashSet<String> ());
		}
		
		map = new HashMap<String, Integer>();
		for (int i = 0; i < pid_list.size(); i++) {
			map.put(pid_list.get(i), i);
		}
		
		pidCPU = new ArrayList<ArrayList<Double>>();
		pidMem = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < pid_list.size(); i++) {
			pidCPU.add(new ArrayList<Double>());
			pidMem.add(new ArrayList<Double>());
		}
		
		SolrClient solr = new HttpSolrClient.Builder(option.readUrl).build();
		group_num = (int) (option.gap/option.interval);
		
		List<ArrayList<ArrayList<Double>>> dataCPU = new ArrayList<ArrayList<ArrayList<Double>>>();    // 原始数据
		List<ArrayList<ArrayList<Double>>> dataMem = new ArrayList<ArrayList<ArrayList<Double>>>();    // 原始数据
		for (int i = 0; i < pid_list.size(); i++) {
			dataCPU.add(new ArrayList<ArrayList<Double>> ());
			dataMem.add(new ArrayList<ArrayList<Double>> ());
			for (int j= 0; j < group_num; j++) {
				dataCPU.get(i).add(new ArrayList<Double> ());
				dataMem.get(i).add(new ArrayList<Double> ());
			}
		}
		
		// 查询
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		String rs_start = TimeUtil.formatUnixtime2(start);
		String rs_end = TimeUtil.formatUnixtime2(start+option.gap);
		
		List<String> fq = new ArrayList<String>(option.fq);
		fq.add("beat_name_s:"+hostname);
		fq.add("metricset_name_s:process");
		fq.add("rs_timestamp_tdt:[" + rs_start + " TO " + rs_end + "]");
		
		String[] fq_str = new String[fq.size()];
		for (int i = 0; i < fq_str.length; i++) {
			fq_str[i] = fq.get(i);
		}
		
		solrQuery.setFilterQueries(fq_str);
		solrQuery.setFields("system_process_pid_f",
				            "system_process_name_s",
				            "system_process_cpu_total_pct_f",
				            "system_process_memory_rss_pct_f",
				            "rs_timestamp_tdt");
		
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
			double cpu_double = (Float) docs.get(j).get("system_process_cpu_total_pct_f");
			double mem_double = (Float) docs.get(j).get("system_process_memory_rss_pct_f");
//				System.out.println(val_name+" "+map.get(val_name)+" "+x);
			dataCPU.get(map.get(val_name)).get(x).add(cpu_double);
			dataMem.get(map.get(val_name)).get(x).add(mem_double);
		}
		
		for (int i = 0; i < pid_list.size(); i++) {
			for (int j = 0; j < group_num; j++) {
				pidCPU.get(i).add(stat.run(dataCPU.get(i).get(j)));
				pidMem.get(i).add(stat.run(dataMem.get(i).get(j)));
			}
		}
		
		app_cpu_tmp = new ArrayList<ArrayList<Double>>();
		
		for (int i = 0; i < app_list.size(); i++) {
			app_cpu_tmp.add(new ArrayList<Double>());
		}
		
		flag = new ArrayList<Boolean>();
		for (int i = 0; i < app_list.size(); i++) {
			flag.add(false);
		}
		
		for (int i = 0; i < app_list.size(); i++) {
			for (int j = 0; j < group_num; j++) {
				double sum = 0;
				for (String it : pidMap.get(app_list.get(i))) {
					sum += pidCPU.get(map.get(it)).get(j);
				}
				if (sum/option.core > 0.001) flag.set(i, true);
				app_cpu_tmp.get(i).add(sum/option.core);
			}
		}
		
		app_mem_tmp = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < app_list.size(); i++) {
			app_mem_tmp.add(new ArrayList<Double>());
		}
		
		for (int i = 0; i < app_list.size(); i++) {
			for (int j = 0; j < group_num; j++) {
				double sum = 0;
				for (String it : pidMap.get(app_list.get(i))) {
					sum += pidMem.get(map.get(it)).get(j);
				}
				if (sum > 0.001) flag.set(i, true);
				app_mem_tmp.get(i).add(sum);
			}
		}
	}
	
}
