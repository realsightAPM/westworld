package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import com.realsight.westworld.bnanalysis.basic.Pair;

public class SolrConfigWriter {

	public SolrConfigWriter() {
		
	}
	
	public void writeConf() throws SolrServerException, IOException {
		String solr_url = "http://localhost:8080/solr/apm3";
		WriteSolr writeSolr = new WriteSolr();
		List<Pair<String, Object>> conf_list= new ArrayList<Pair<String, Object>> ();
		conf_list.add(new Pair<String, Object> ("option_s", "bn"));
		conf_list.add(new Pair<String, Object> ("bn_name_s", "bn_01"));
		conf_list.add(new Pair<String, Object> ("facet_field", "application_s"));
		conf_list.add(new Pair<String, Object> ("facet_range", "time_stamp"));
		Calendar start = new GregorianCalendar(2017, 3, 20, 1, 31, 30);
		start.add(Calendar.HOUR, 8);
		Calendar end = new GregorianCalendar(2017,3,25,1,46,30);
		end.add(Calendar.HOUR, 8);
		conf_list.add(new Pair<String, Object> ("facet_start", start.getTimeInMillis()));
		conf_list.add(new Pair<String, Object> ("facet_end", end.getTimeInMillis()));
		conf_list.add(new Pair<String, Object> ("facet_gap", "+HOUR"));
		writeSolr.writeOneDoc(solr_url, conf_list);
	}
	
	public static void main(String[] args) throws SolrServerException, IOException {
		SolrConfigWriter solrConf = new SolrConfigWriter();
		solrConf.writeConf();
	}
}
