package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class MetricOption {

	public String readUrl;
	public String writeUrl;
	public long startTime;
	public long interval;
	public long gap;
	public long core;
	public List<String> fq;
	public List<String> hostNames;
	
	private MetricOption() {}
	
	public MetricOption(String optionUrl, String bn_name) {
		
		SolrClient solr = new HttpSolrClient.Builder(optionUrl).build();
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries("bn_name_s:"+bn_name);
		solrQuery.setRows(1);
		
		QueryResponse response = null;
		
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (SolrServerException | IOException e) {
				System.out.println("网络read异常");
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		SolrDocumentList docs = response.getResults();
		SolrDocument doc = docs.get(0);
		
		readUrl = (String) doc.get("solr_reader_url_s");
		writeUrl = (String) doc.get("solr_writer_url_s");
		startTime = (long) doc.get("starttime_l");
		interval = (long) doc.get("interval_l");
		gap = (long) doc.get("gap_l");
		fq = (List<String>) doc.get("fq_ss");
		core = (long) doc.get("core_l");
		hostNames = (List<String>) doc.get("hostname_ss");
	}
	
	public static void main(String[] args) {
		MetricOption option = new MetricOption("http://10.0.67.14:8080/solr/option", "bn_metrics11");
//		System.out.println(option.fq.size());
//		System.out.println(option.fq.get(0));
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTimeInMillis(option.startTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(sdf.format(rightNow.getTime()));
	}
	
}
