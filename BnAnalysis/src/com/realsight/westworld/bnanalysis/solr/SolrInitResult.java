package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;

import com.realsight.westworld.bnanalysis.basic.Pair;

public class SolrInitResult {

	public SolrInitResult() {}
	
	public void runInit(SolrDocument option, long start) throws SolrServerException, IOException {
		SolrResults resulter = new SolrResults((String) option.get("solr_writer_url_s"));
		
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", option.get("bn_name_s")));
//		resulter.addResult(new Pair<String, Object> ("rs_timestamp", date.getTime()));
		long time_now = Calendar.getInstance().getTimeInMillis();
		resulter.addResult(new Pair<String, Object> ("timestamp_l", time_now));
		resulter.addResult(new Pair<String, Object> ("start_timestamp_l", start));
		
		resulter.addResult(new Pair<String, Object> ("edges_s", "[]"));
		String str = "";
		
		String node_str = "";
		String[] var_list = option.get("indexList_s").toString().split(",");
		for (int i = 0; i < var_list.length; i++) {
			str += "^"+var_list[i].split(":")[1];
			node_str += ",{\"key\": " + "\"" + i + "\"}";
			resulter.addResult(new Pair<String, Object> (""+i+"_s", i + ":2"));
		}
		resulter.addResult(new Pair<String, Object> ("nodes_s", "[" + node_str.substring(1) + "]"));
		resulter.addResult(new Pair<String, Object> ("query_list_s", str.substring(1)));
		resulter.write(); // Í¬²½
	}
}
