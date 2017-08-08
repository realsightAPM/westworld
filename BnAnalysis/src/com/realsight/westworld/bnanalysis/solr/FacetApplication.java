package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

public class FacetApplication {
	public Map<String, Integer> pidHash;
	public Map<String, Integer> pidHashCount;
	public List<String> pidList;
	public List<ArrayList<Double>> pidArray;

	public List<String> getApp() {
		
		pidHash = new HashMap<String, Integer> ();
		pidHashCount = new HashMap<String, Integer> ();
		pidList = new ArrayList<String>();
		pidArray = new ArrayList<ArrayList<Double>>();
		
		String solrStr = "http://10.0.67.14:8080/solr/metrics";
		SolrClient solr = new HttpSolrClient.Builder(solrStr).build();
		
		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery.setQuery("*:*");
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.addFacetField("system_process_name_s");
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
		
		FacetField facet = response.getFacetField("system_process_name_s");
		List<Count> pidCountList = facet.getValues();
		for (int i = 0; i < pidCountList.size(); i++) {
			pidHash.put(pidCountList.get(i).getName(), i);
			pidHashCount.put(pidCountList.get(i).getName(), (int) pidCountList.get(i).getCount());
			pidList.add(pidCountList.get(i).getName());
//			System.out.println(pidCountList.get(i) + ": " + pidCountList.get(i).getCount());
		}
		return pidList;
	}
	
	public static void main(String[] args) {
		FacetApplication facetApp = new FacetApplication();
		List<String> list = facetApp.getApp();
		
		for (int i = 0; i < list.size(); i++) {
			System.out.println("应用：" + list.get(i));
		}
	}
}
