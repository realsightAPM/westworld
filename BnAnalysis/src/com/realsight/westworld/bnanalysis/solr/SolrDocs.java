package com.realsight.westworld.bnanalysis.solr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.basic.Pair;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SolrDocs {
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}

	public List<SolrOneDoc> docs;
	public String solr_url;
	public String collection;
	public SolrDocs() {}
	
	public SolrDocs(String _solr_url, String _collection) {
		solr_url = _solr_url;
		collection = _collection;
		docs = new ArrayList<SolrOneDoc> ();
	}
	
	public void addDoc(SolrOneDoc doc) {
		docs.add(doc);
	}
	
	public void writeCloud() {
		WriteSolrCloud writer = new WriteSolrCloud();
		writer.writeDocs(solr_url, collection, docs);
	}
	
	public void testCloud() {
		
		long gap = (long) (1000*3600*24);
		String rs_start = "2017-09-11T16:00:00Z";
		long start_time = TimeUtil.timeConversion2(rs_start);
		SolrOneDoc resulter = new SolrOneDoc();
		
		Date theDate = Calendar.getInstance().getTime();
		resulter.addResult(new Pair<String, Object> ("option_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", theDate.toString()));
		resulter.addResult(new Pair<String, Object> ("solr_reader_url_s", solr_url + "metrics/"));
		resulter.addResult(new Pair<String, Object> ("solr_writer_url_s", "http://10.0.67.14:8080/solr/" + "rca/"));
		resulter.addResult(new Pair<String, Object> ("starttime_l", start_time));
		resulter.addResult(new Pair<String, Object> ("gap_l", gap));
		resulter.addResult(new Pair<String, Object> ("interval_l", 60000));
		resulter.addResult(new Pair<String, Object> ("core_l", 4));
		List<String> fqList = new ArrayList<String>();
		fqList.add("type_s:metricsets");
		resulter.addResult(new Pair<String, Object> ("fq_ss", fqList));
		List<String> hostNames = new ArrayList<String>();
		hostNames.add("BC-VM-1418df5b51e34dfabcc357c96cce26b5");
		resulter.addResult(new Pair<String, Object> ("hostname_ss", hostNames));
		
		for (int i = 0; i < 2000; i++) {
			addDoc(resulter);
		}
		
	}
	
	public static void main(String[] args) {
		SolrDocs solrCloud = new SolrDocs("10.0.67.31:2181,10.0.67.32:2181,10.0.67.33:2181", "nifi");
		solrCloud.testCloud();
		
		int cnt = 0;
		
		while (true) {
			
			solrCloud.writeCloud();
			System.out.println("count: " + (++cnt));
		}
	}
}
