package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;

public class FacetPid {

	public List<String> pidList;
	
	private FacetPid(){}
	
	public FacetPid(String url) {
		
		pidList = new ArrayList<String>();
		
		SolrClient solr = new HttpSolrClient.Builder(url).build();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.addFacetField("system_process_pid_f");
		solrQuery.setFacetLimit(10000);
		
		QueryResponse response = null;
		
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (SolrServerException | IOException e) {
				// TODO Auto-generated catch block
				System.out.println("网络 read process id 异常");
				e.printStackTrace();
			}
		}
		
		FacetField facet = response.getFacetField("system_process_pid_f");
		List<Count> pidCountList = facet.getValues();
		for (int i = 0; i < pidCountList.size(); i++) {
			pidList.add(pidCountList.get(i).getName());
		}
	}
}
