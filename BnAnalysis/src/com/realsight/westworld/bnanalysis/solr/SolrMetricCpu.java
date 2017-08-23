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
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.basic.Discretization;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SolrMetricCpu {

	public List<Double> cpu;
	public List<String> cpuDisc;
	public int stateNum;
	
	private SolrMetricCpu() {}
	
	public SolrMetricCpu(long start, Statistic stat, MetricOption option) {
		
		cpu = new ArrayList<Double>();
		cpuDisc = new ArrayList<String>();
		
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
		fq.add("metricset_name_s:cpu");
		fq.add("rs_timestamp_tdt:[" + rs_start + " TO " + rs_end + "]");
		
		String[] fq_str = new String[fq.size()];
		for (int i = 0; i < fq_str.length; i++) {
			fq_str[i] = fq.get(i);
		}
		
		solrQuery.setFilterQueries(fq_str);
		solrQuery.setFields("system_cpu_idle_pct_f", "rs_timestamp_tdt");
		
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
		System.out.println("cpu docs.size: " + docs.size());
		for (int i = 0; i < docs.size(); i++) {
			long timestamp_tmp = ((Date) docs.get(i).get("rs_timestamp_tdt")).getTime();
			int x = (int) ((timestamp_tmp-start)/option.interval);
			if (x >= group_num) continue;
			double tmp_double = (Float) docs.get(i).get("system_cpu_idle_pct_f");
			data.get(x).add(1-tmp_double);
		}
		
		for (int i = 0; i < group_num; i++) {
			cpu.add(stat.run(data.get(i)));
		}
		
		Discretization disc = new Discretization();
		stateNum = 0;
		for (int i = 0; i < group_num; i++) {
			int pos = disc.run(cpu.get(i), 0.2, 0.4, 0.6, 0.8);
			if (pos > stateNum) stateNum = pos;
			cpuDisc.add("" + ((char)('a'+pos)));
		}
	}
	
	public static void main(String[] args) {
		MetricOption option = new MetricOption("http://10.0.67.14:8080/solr/option", "bn_metrics3");
		SolrMetricCpu metricCpu = new SolrMetricCpu(option.startTime, new Mean(), option);
		
		for (Double it : metricCpu.cpu) {
			System.out.println(it);
		}
	}
	
}
