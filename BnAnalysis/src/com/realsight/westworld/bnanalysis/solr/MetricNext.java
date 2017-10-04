package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class MetricNext {

	public MetricNext() {}
	
	public boolean goNext(MetricOption option, long end) {
		
		SolrClient solr = new HttpSolrClient.Builder(option.readUrl).build();
		SolrQuery solrQuery = new SolrQuery();

		solrQuery.setQuery("*:*");
		String[] fq_list = new String[option.fq.size()+1];
		for (int j = 0; j < option.fq.size(); j++) {
			fq_list[j] = option.fq.get(j);
		}
		String rs_end = TimeUtil.formatUnixtime2(end);
		fq_list[option.fq.size()] = "rs_timestamp_tdt" + ":[" + rs_end + " TO " + " *]";
		
		solrQuery.setFilterQueries(fq_list);
		solrQuery.setFields("rs_timestamp_tdt");
		QueryResponse response = new QueryResponse();
		SolrDocumentList docs = new SolrDocumentList();
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (Exception e) {
				System.out.print("check异常");
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
		
		docs = response.getResults();
	
		return docs.size() > 0;
	}
}
