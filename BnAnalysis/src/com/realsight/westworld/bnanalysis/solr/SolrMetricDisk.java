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
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.basic.Discretization;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SolrMetricDisk {
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}

	public List<Double> disk;
	public List<String> diskDisc;
	public int stateNum;
	
	private SolrMetricDisk() {}
	
	public SolrMetricDisk(long start, Statistic stat, MetricOption option, String hostname) {
		
		disk = new ArrayList<Double>();
		diskDisc = new ArrayList<String>();
		
		SolrClient solr = new HttpSolrClient.Builder(option.readUrl).build();
		
		int group_num = (int) (option.gap/option.interval);
		
		
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		
		String rs_start = TimeUtil.formatUnixtime2(start);
		String rs_end = TimeUtil.formatUnixtime2(start+option.gap);
		
		List<String> fq = new ArrayList<String>(option.fq);
		fq.add("beat_name_s:"+hostname);
		fq.add("metricset_name_s:diskio");
		fq.add("rs_timestamp_tdt:[" + rs_start + " TO " + rs_end + "]");
		
		String[] fq_str = new String[fq.size()];
		for (int i = 0; i < fq_str.length; i++) {
			fq_str[i] = fq.get(i);
		}
		
		solrQuery.setFilterQueries(fq_str);
		solrQuery.setFields("system_diskio_name_s" ,"system_diskio_write_bytes_f", "system_diskio_read_bytes_f", "rs_timestamp_tdt");
		
		solrQuery.setRows(2000000);
		solrQuery.setSort("rs_timestamp_tdt", ORDER.asc);
		
		solrQuery.setFacet(true);
		solrQuery.addFacetField("system_diskio_name_s");
		solrQuery.setFacetLimit(1000);
		
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
		
		FacetField facet = response.getFacetField("system_diskio_name_s");
		List<Count> diskList = facet.getValues();
		
		Map<String, Integer> diskMap = new HashMap<String, Integer>();
		Map<String, Integer> diskCount = new HashMap<String, Integer>();
		for (int i = 0; i < diskList.size(); i++) {
			diskMap.put(diskList.get(i).getName(), i);
			diskCount.put(diskList.get(i).getName(), 0);
		}
		
		List<ArrayList<ArrayList<Double>>> data = new ArrayList<ArrayList<ArrayList<Double>>> ();
		
		for (int i = 0; i < diskList.size(); i++) {
			data.add(new ArrayList<ArrayList<Double>>());
			for (int j = 0; j < group_num; j++) {
				data.get(i).add(new ArrayList<Double>());
			}
		}
				
		for (Count it : diskList) {
			System.out.println("磁盘名："+it.getName());
		}
		
		SolrDocumentList docs = response.getResults();
		System.out.println("diskio docs.size: " + docs.size());
		
		
		
		for (int i = 0; i < docs.size(); i++) {
			long timestamp_tmp = ((Date) docs.get(i).get("rs_timestamp_tdt")).getTime();
			String disk_str = (String) docs.get(0).get("system_diskio_name_s");
			int x = (int) ((timestamp_tmp-start)/option.interval);
			if (x >= group_num) continue;
			
			double tmp_double = (Float) docs.get(i).get("system_diskio_write_bytes_f")
			          + (Float) docs.get(i).get("system_diskio_read_bytes_f");
			
			data.get(diskMap.get(disk_str)).get(x).add(tmp_double);
		}
		
		
		for (int i = 0; i < group_num; i++) {
			double tmp = 0;
			for (int j = 0; j < diskList.size(); j++) {
				tmp += stat.run(data.get(j).get(i));
			}
			disk.add(tmp);
		}
		
		for (int i = disk.size()-1; i > 0; i--) {
			double tmp = disk.get(i);
			disk.set(i, tmp - disk.get(i-1));
		}
		
		if (disk.size() > 1) {
			disk.set(0, disk.get(1));
		}
		
		Discretization disc = new Discretization();
		stateNum = 0;
		for (int i = 0; i < group_num; i++) {
			int pos = disc.run(disk.get(i), 1000000, 2000000, 3000000);
			if (pos > stateNum) stateNum = pos;
			diskDisc.add("" + ((char)('a'+pos)));
		}
	}
	
	public static void main(String[] args) {
		MetricOption option = new MetricOption("http://10.0.67.14:8080/solr/option", "bn_metrics15");
		SolrMetricDisk metric = new SolrMetricDisk(option.startTime, new Mean(), option, option.hostNames.get(0));
		
		for (Double it : metric.disk) {
			System.out.println(it);
		}
		
	}

}
