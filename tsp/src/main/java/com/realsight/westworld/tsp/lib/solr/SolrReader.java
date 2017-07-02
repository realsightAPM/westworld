package com.realsight.westworld.tsp.lib.solr;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.StatsParams;
import org.json.JSONException;
import org.json.JSONObject;

public class SolrReader{
	private final String SOLR_URL;
	private final HttpSolrClient SOLR_Client;
	private String[] filters = null;
	private String[] queryFields = null;
	private String sortField = null;
	private String stats_field = null;
	private String stats_facet = null;
	private boolean asc = false; 
	private final int rows;
	private int start = 0;
	private Iterator<SolrDocument> solr_doc_iter = null;
	private Map<String, FieldStatsInfo> fieldStatsInfo = null;
	
	public String getSOLR_URL() {
		return SOLR_URL;
	}
	
	public SolrReader(String SOLR_URL, int rows, String stats_field, String stats_facet, String ... filters) {
		if (SOLR_URL == null) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}
		
		if (rows <= 0) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}
		
		this.rows = rows;
		this.SOLR_URL = SOLR_URL;
		this.stats_field = stats_field;
		this.stats_facet = stats_facet;
		this.filters = filters;
		this.SOLR_Client = new HttpSolrClient.Builder(this.SOLR_URL).build();
		this.SOLR_Client.setParser(new XMLResponseParser());
	}
	
	public SolrReader(String SOLR_URL, String ... filters) {
		this(SOLR_URL, 100, null, null, filters);
	}
	
	private boolean updateResultList(){
		if (this.SOLR_Client == null) {
			throw new IllegalArgumentException(
					"SOLR_Client can not be null.");
		}
		
		SolrQuery params = new SolrQuery();
		params.setQuery("*:*");
		if (this.queryFields != null) {
			params.setFields(queryFields);
		}
		if (this.stats_field!=null && !this.stats_field.equals("")) {
			params.set(StatsParams.STATS, true);
			params.set(StatsParams.STATS_FIELD, this.stats_field);
			if (this.stats_facet!=null && !this.stats_facet.equals("")) {
				params.set(StatsParams.STATS_FACET, this.stats_facet);
			}
		}
		params.setFilterQueries(filters);
		params.setRows(rows);
		params.setStart(start);
		
		System.err.println(SOLR_URL + " " + params.toQueryString());
		
		if (sortField != null) {
			params.setSort(sortField, this.asc?ORDER.asc:ORDER.desc);
		}
		try {
			QueryResponse response = SOLR_Client.query(params);
			SolrDocumentList solr_doc_list = response.getResults();
			this.fieldStatsInfo = response.getFieldStatsInfo();
			if (solr_doc_list.isEmpty()){
				solr_doc_iter = null;
				queryFields = null;
				sortField = null;
				return false;
			}
			solr_doc_iter = solr_doc_list.iterator();
			return true;
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public Map<String, FieldStatsInfo> getFieldStatsInfo() {
		this.hasNextResponse();
		return this.fieldStatsInfo;
	}
	public void close() throws IOException {
		// TODO Auto-generated method stub
		this.SOLR_Client.close();
	}
	
	public void setSort(String sortField, boolean asc) {
		this.sortField = sortField;
		this.asc = asc;
	}
	
	public synchronized boolean hasNextResponse() {
		if (solr_doc_iter == null){
			return updateResultList();
		}
		if (solr_doc_iter.hasNext())
			return true;
		return updateResultList();
	}
	
	public synchronized String nextResponse() {
		if (hasNextResponse() == false)
			return null;
		SolrDocument solr_doc = solr_doc_iter.next();
		JSONObject json = new JSONObject();
		for (String name : solr_doc.getFieldNames()) {
			try {
				json.put(name, solr_doc.getFieldValue(name));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.start += 1;
		return json.toString();
	}
}
