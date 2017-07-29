package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrOptionKeeper {

	public Set optionSet;
	public String url;
	
	private SolrOptionKeeper() {}
	
	public SolrOptionKeeper(String url) {
		optionSet = new HashSet<String>();
		this.url = url;
	}
	
	public String runKeeper() {
		SolrClient solr = new HttpSolrClient.Builder(url).build();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries("option_s:bn");
		solrQuery.setRows(10000);
		QueryResponse response;
		
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (Exception e) {
				System.out.println("Õ¯¬Á“Ï≥£");
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
		SolrDocumentList docs = response.getResults();
		for (int i = 0; i < docs.size(); i++) {
			String id_s = docs.get(i).get("id").toString();
			Object res = docs.get(i).get("bn_name_s");
			if (!optionSet.contains(id_s) && res != null) {
				optionSet.add(id_s);
				return res.toString();
			}
		}
		return null;
	}
}
