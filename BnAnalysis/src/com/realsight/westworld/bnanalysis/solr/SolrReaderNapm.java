package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.realsight.westworld.bnanalysis.Dao.Statistic;

public class SolrReaderNapm extends SolrReaderObject {

	@Override
	public void runRead(SolrDocument option, String time_field, long start, long end, Statistic stat, String... fq) {
		// TODO Auto-generated method stub
		SolrClient solr = new HttpSolrClient.Builder(option.get("solr_reader_url_s").toString()).build();
		
		long interval = (long) option.get("interval_l");
		long gap = (long) option.get("gap_l");
		int group_num = (int) (gap/interval);
		
		List<String> index_list = (ArrayList<String>) option.get("index_ss");
		List<String> values = (ArrayList<String>) option.get("value_ss");
		String[] var_list = new String[index_list.size()];
		String[] value_list = new String[index_list.size()];
		for (int i = 0; i < var_list.length; i++) {
			String[] str_tmp = index_list.get(i).split(":");
			var_list[i] = str_tmp[1];
		}
		
		List<ArrayList<ArrayList<Double>>> resource = new ArrayList<ArrayList<ArrayList<Double>>>();
		for (int i = 0; i < var_list.length; i++) {
			resource.add(new ArrayList<ArrayList<Double>> ());
			for (int j= 0; j < group_num; j++) {
				resource.get(i).add(new ArrayList<Double> ());
			}
		}
		
		SolrQuery[] solrQuery = new SolrQuery[var_list.length];
		for (int i = 0; i < solrQuery.length; i++) {
			solrQuery[i] = new SolrQuery();
		}
		
		queryList = new ArrayList<String>();
		queryMap = new HashMap<String, Integer>();
		queryArray = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < var_list.length; i++) {
			queryList.add(":"+var_list[i]);
			queryMap.put(var_list[i], i);
			queryArray.add(new ArrayList<Double>());
		}
		
		for (int i = 0; i < var_list.length; i++) {
			String[] tmp = values.get(i).split(":");
			value_list[queryMap.get(tmp[0])] = tmp[1];
		}
		
		for (int i = 0; i < var_list.length; i++) {
			solrQuery[i].setQuery("*:*");
			String[] fq_list = new String[fq.length+2];
			for (int j = 0; j < fq.length; j++) {
				fq_list[j] = fq[j];
			}
			String rs_start = TimeUtil.formatUnixtime2(start);
			String rs_end = TimeUtil.formatUnixtime2(end);
			
			fq_list[fq.length] = time_field + ":[" + rs_start + " TO " + rs_end + "]";
			fq_list[fq.length+1] = index_list.get(i);
			solrQuery[i].setFilterQueries(fq_list);
			solrQuery[i].setFields(value_list[i], time_field);
			solrQuery[i].setRows(2000000);
//			solrQuery[i].setSort("timestamp_l", ORDER.asc);
			solrQuery[i].setSort(time_field, ORDER.asc);
		}
		
		
		
		QueryResponse[] response = new QueryResponse[solrQuery.length];
		SolrDocumentList[] docs = new SolrDocumentList[solrQuery.length];
		
		while (true) {
			try {
				for (int i = 0; i < response.length; i++) {
					response[i] = solr.query(solrQuery[i]);
				}
				break;
			} catch (Exception e) {
				System.out.print("ÍøÂçreadÒì³£");
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
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (int i = 0; i < docs.length; i++) {
			docs[i] = response[i].getResults();
			System.out.println("docs.size(): " + docs[i].size());
			for (int j = 0; j < docs[i].size(); j++) {
				long timestamp_tmp = ((Date) docs[i].get(j).get(time_field)).getTime();
				
				int x = (int) ((timestamp_tmp-start)/interval);
				double tmp_double = 0;
				
				if (docs[i].get(j).get(value_list[i]) instanceof Double) {
					tmp_double = (Double) docs[i].get(j).get(value_list[i]);
				} else if (docs[i].get(j).get(value_list[i]) instanceof Float) {
					tmp_double = (Float) docs[i].get(j).get(value_list[i]);
				} else if (docs[i].get(j).get(value_list[i]) instanceof Integer) {
					tmp_double = (Integer) docs[i].get(j).get(value_list[i]);
				}
				else if (docs[i].get(j).get(value_list[i]) instanceof Long) {
						tmp_double = (Long) docs[i].get(j).get(value_list[i]);
				} else if (docs[i].get(j).get(value_list[i]) instanceof String){
					try {
						tmp_double = Double.parseDouble(docs[i].get(j).get(value_list[i]).toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (x >= group_num) continue;
				resource.get(i).get(x).add(tmp_double);
			}
		}
		
		for (int i = 0; i < var_list.length; i++) {
			for (int j = 0; j < group_num; j++) {
				queryArray.get(i).add(stat.run(resource.get(i).get(j)));
			}
		}
		
		attrList = new String[var_list.length];
		for (int i = 0; i < var_list.length; i++) {
			attrList[i] = "_" + (new Integer(i)).toString();
		}
		
	}

}
