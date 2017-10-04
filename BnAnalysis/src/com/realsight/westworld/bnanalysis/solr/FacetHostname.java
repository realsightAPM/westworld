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

import com.realsight.westworld.bnanalysis.basic.Pair;

public class FacetHostname {

	public List<String> hostnames;
	
	public FacetHostname(String url) {
		
		hostnames = new ArrayList<String>();
		
		SolrClient solr = new HttpSolrClient.Builder(url).build();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.addFacetField("beat_name_s");
		solrQuery.setFacetLimit(1000);
		
		QueryResponse response = null;
		
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (SolrServerException | IOException e) {
				// TODO Auto-generated catch block
				System.out.println("网络 read hostname 异常");
				e.printStackTrace();
			}
		}
		
		FacetField facet = response.getFacetField("beat_name_s");
		List<Count> host_List = facet.getValues();
		for (int i = 0; i < host_List.size(); i++) {
			hostnames.add(host_List.get(i).getName());
		}
	}
	
	public static void main(String[] args) {
		FacetHostname facet = new FacetHostname("http://10.0.67.14:8080/solr/metrics/");
		for (String it : facet.hostnames) {
			System.out.println(it);
		}
		
		String solr_url = "http://10.0.67.14:8080/solr/";
		SolrOneDoc resulter = new SolrOneDoc(solr_url + "rca/");
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "host_list1"));
		resulter.addResult(new Pair<String, Object> ("hostnames_ss", facet.hostnames));
		
		try {
			resulter.write();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
