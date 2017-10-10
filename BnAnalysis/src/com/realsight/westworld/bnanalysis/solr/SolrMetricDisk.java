package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
		
		List<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < group_num; i++) {
			data.add(new ArrayList<Double>());
		}
		
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
		List<Count> disk_List = facet.getValues();
		
		SolrDocumentList docs = response.getResults();
		System.out.println("diskio docs.size: " + docs.size());
		
		double pre_double = (Float) docs.get(0).get("system_diskio_write_bytes_f") 
		          + (Float) docs.get(0).get("system_diskio_read_bytes_f");
		System.out.println(pre_double);
		for (int i = 1; i < docs.size(); i++) {
			long timestamp_tmp = ((Date) docs.get(i).get("rs_timestamp_tdt")).getTime();
			int x = (int) ((timestamp_tmp-start)/option.interval);
			if (x >= group_num) continue;
			double tmp_double = (Float) docs.get(i).get("system_diskio_write_bytes_f") 
					          + (Float) docs.get(i).get("system_diskio_read_bytes_f");
			System.out.println(((Date) docs.get(i).get("rs_timestamp_tdt")).toLocaleString() + ": " + tmp_double);
			data.get(x).add(tmp_double - pre_double);
			pre_double = tmp_double;
		}
		
		for (int i = 0; i < group_num; i++) {
			disk.add(stat.run(data.get(i)));
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
		
//		for (Double it : metric.disk) {
//			System.out.println(it);
//		}
	}

}
