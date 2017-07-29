package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrChecker {

	public SolrChecker() {
		
	}
	
	public int check(SolrDocument option, String time_field, long end, String... fq) {
		
		SolrClient solr = new HttpSolrClient.Builder((String) option.get("solr_reader_url_s")).build();
		SolrQuery solrQuery = new SolrQuery();
		
		
		solrQuery.setQuery("*:*");
		String[] fq_list = new String[fq.length+1];
		for (int j = 0; j < fq.length; j++) {
			fq_list[j] = fq[j];
		}
		String rs_end = TimeUtil.formatUnixtime2(end);
		fq_list[fq.length] = time_field + ":[" + rs_end + " TO " + " *]";
		
		solrQuery.setFilterQueries(fq_list);
		QueryResponse response = new QueryResponse();
		SolrDocumentList docs = new SolrDocumentList();
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (Exception e) {
				System.out.print("ÍøÂçcheckÒì³£");
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
	
		return docs.size();
	}
	
}
