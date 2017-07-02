package com.realsight.westworld.engine.io;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
	public Map<String, Integer> urlHash;
	public Map<String, Integer> urlHashCount;
	public List<String> urlList;
	public List<ArrayList<Double>> urlArray;
	
	public ReadSolr() {
		getSolrArray();
	}
	
	public void getSolrArray() {
		urlHash = new HashMap<String, Integer> ();
		urlHashCount = new HashMap<String, Integer> ();
		urlList = new ArrayList<String>();
		urlArray = new ArrayList<ArrayList<Double>>();
		String solrStr = "http://10.4.55.171:8983/solr/test";
		SolrClient solr = new HttpSolrClient.Builder(solrStr).build();
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFields("rs_timestamp_tdt");
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.addFacetField("query_s");
		solrQuery.setFacetLimit(100);
		Calendar start = new GregorianCalendar(2017, 5, 17, 0, 0, 0);
		start.add(Calendar.HOUR, 8);
		Calendar end = new GregorianCalendar(2017, 5, 22, 0, 0, 0);
		end.add(Calendar.HOUR, 8);
		solrQuery.addDateRangeFacet("rs_timestamp_tdt", start.getTime(), end.getTime(), "+1HOUR");
		
		try {
			QueryResponse response = solr.query(solrQuery);
			FacetField facet = response.getFacetField("query_s");
			List<Count> urlCountList = facet.getValues();
			for (int i = 0; i < urlCountList.size(); i++) {
				urlHash.put(urlCountList.get(i).getName(), i);
				urlHashCount.put(urlCountList.get(i).getName(), (int) urlCountList.get(i).getCount());
				urlList.add(urlCountList.get(i).getName());
			}
			int cnt = 0;
			for (String it : urlList) {
				cnt++;
				System.out.println("count: " + cnt);
				ArrayList<Double> timeList = new ArrayList<Double>();
				solrQuery.setFilterQueries("query_s:" + "\"" + it + "\"");
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
				System.out.print("("+i+"): " );
				for (int j = 0; j < urlArray.get(i).size(); j++) {
					System.out.print(urlArray.get(i).get(j)+" ");
				}
				System.out.println();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ReadSolr solr = new ReadSolr();
	}
}
