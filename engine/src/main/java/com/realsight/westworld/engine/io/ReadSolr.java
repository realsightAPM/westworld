package com.realsight.westworld.engine.io;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;


public class ReadSolr {

	public ReadSolr() {
		
	}
	
	public List<ArrayList<Double>> getSolrArray() {
		Map<String, Integer> urlHash = new LinkedHashMap<String, Integer> ();
		List<ArrayList<Double>> urlArray = new ArrayList<ArrayList<Double>>();
		String solrStr = "http://localhost:8080/solr/apm3";
		SolrClient solr = new HttpSolrClient.Builder(solrStr).build();
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFields("rs_timestamp");
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.addFacetField("application_s");
		solrQuery.setFacetLimit(50);
		Calendar start = new GregorianCalendar(2017, 3, 20, 1, 31, 30);
		start.add(Calendar.HOUR, 8);
		Calendar end = new GregorianCalendar(2017,3,25,1,46,30);
		end.add(Calendar.HOUR, 8);
		solrQuery.addDateRangeFacet("rs_timestamp", start.getTime(), end.getTime(), "+1HOUR");
		
		try {
			QueryResponse response = solr.query(solrQuery);
			FacetField facet = response.getFacetField("application_s");
			List<Count> urlList = facet.getValues();
			for (int i = 0; i < urlList.size(); i++) {
				urlHash.put(urlList.get(i).getName(), i);
			}
			
			for (String it : urlHash.keySet()) {
				ArrayList<Double> timeList = new ArrayList<Double>();
				solrQuery.setFilterQueries("application_s:" + "\"" + it + "\"");
				QueryResponse resp = solr.query(solrQuery);
				List<RangeFacet> facet_date = resp.getFacetRanges();
				RangeFacet range = facet_date.get(0);
				List<RangeFacet.Count> count_list = range.getCounts();
				for (RangeFacet.Count it2 : count_list) {
//					System.out.println(it2.getValue() + ": " + it2.getCount());
					timeList.add((double) it2.getCount());
				}
				urlArray.add(timeList);
			}
			
			for (int i = 0; i < urlArray.size(); i++) {
				for (int j = 0; j < urlArray.get(i).size(); j++) {
					System.out.print(urlArray.get(i).get(j)+" ");
				}
				System.out.println();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return urlArray;
	}
	
	public static void main(String[] args) {
		ReadSolr solr = new ReadSolr();
		solr.getSolrArray();
	}
}
