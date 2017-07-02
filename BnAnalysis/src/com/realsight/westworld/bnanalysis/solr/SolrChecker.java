package com.realsight.westworld.bnanalysis.solr;

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
	
	public int check(SolrDocument option, long end, String... fq) {
		
		SolrClient solr = new HttpSolrClient.Builder((String) option.get("solr_reader_url_s")).build();
		SolrQuery solrQuery = new SolrQuery();
		
		
		solrQuery.setQuery("*:*");
		String[] fq_list = new String[fq.length+1];
		for (int j = 0; j < fq.length; j++) {
			fq_list[j] = fq[j];
		}
		fq_list[fq.length] = "timestamp_l:[" + end + " TO " + " *]";
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
			}
		}
		docs = response.getResults();
	
		return docs.size();
	}
	
}
