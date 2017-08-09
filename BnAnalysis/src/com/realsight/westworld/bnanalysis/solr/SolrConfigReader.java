package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrConfigReader {
	
	public SolrDocument option;
	public long start_time, gap;
	public String[] fq;
	public void runRead(String url, String bn_name) {
		SolrClient solr = new HttpSolrClient.Builder(url).build();
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries("bn_name_s:" + bn_name,//8a8a83a95cc8a3d4015cc8a985190003.19f750081da1104aa21ecb78d800a889",
				                   "option_s:bn");
		solrQuery.setRows(1);
		option = null;
		while (true) {
			try {
				QueryResponse response = solr.query(solrQuery);
				SolrDocumentList docs = response.getResults();
				option = docs.get(0);
				break;
			} catch (Exception e) {
				System.out.println("读取option异常");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		start_time = (long) option.get("starttime_l");
		gap = (long) option.get("gap_l");
		fq = option.get("fq_s").toString().split("&");
	}
}
