package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.io.WriteCSV;

public class SorlReaderSingle extends SolrReaderObject {

	public SorlReaderSingle(){};
	
	@Override
	public void runRead(SolrDocument option, String time_field, long start, long end, Statistic stat, String... fq) {
		// TODO Auto-generated method stub
		System.out.println("SolrReaderSingle");
		
		SolrClient solr = new HttpSolrClient.Builder((String) option.get("solr_reader_url_s")).build();
		
		String[] var_list = option.get("indexList_s").toString().split("\\^");
		System.out.println(option.get("indexList_s").toString());
		System.out.println(var_list.length);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String query_field = option.get("query_field_s").toString();  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		String value_field = option.get("value_field_s").toString();  // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		System.out.println(query_field + "  " + value_field);
		
		long interval = (long) option.get("interval_l");
		long gap = (long) option.get("gap_l");
		int group_num = (int) (gap/interval);
		
		HashMap<String, ArrayList<ArrayList<Double>>> resource = new HashMap<String, ArrayList<ArrayList<Double>>>();
		for (int i = 0; i < var_list.length; i++) {
			resource.put(var_list[i], new ArrayList<ArrayList<Double>>());
			for (int j = 0; j < group_num; j++) {
				resource.get(var_list[i]).add(new ArrayList<Double>());                //+++++++++++++++++++++++++++++++++++++++++
			}
		}
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		String[] fq_list = new String[fq.length+1];
		for (int j = 0; j < fq.length; j++) {
			fq_list[j] = fq[j];
		}
		fq_list[fq.length] = "timestamp_l:[" + start + " TO " + end + "]";
		solrQuery.setFilterQueries(fq_list);
		solrQuery.setRows(100000);
		solrQuery.setSort("timestamp_l", ORDER.asc);
		solrQuery.setFields(query_field, value_field, "timestamp_l");
		
		queryList = new ArrayList<String>();
		queryMap = new HashMap<String, Integer>();
		queryArray = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < var_list.length; i++) {
			queryList.add(var_list[i]);
			queryMap.put(var_list[i], i);
			queryArray.add(new ArrayList<Double>());
		}
		
		QueryResponse response = new QueryResponse();
		SolrDocumentList docs = new SolrDocumentList();
		
		
		
		while (true) {
			try {
				response = solr.query(solrQuery);
				break;
			} catch (Exception e) {
				System.out.print("网络read异常");
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		
		try {
			solr.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		docs = response.getResults();
		
		System.out.println("数据量："+docs.size());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		for (int i = 0; i < docs.size(); i++) {
			String var = docs.get(i).get(query_field).toString();
			if (!queryMap.keySet().contains(var)) {
				continue;
			}
			long timestamp_tmp = (long) docs.get(i).get("timestamp_l");
			int x = (int) ((timestamp_tmp-start)/interval);
			double tmp_double = 0;
			if (docs.get(i).get(value_field) instanceof Double) {
				tmp_double = (Double) docs.get(i).get(value_field);
			} else if (docs.get(i).get(value_field) instanceof Float) {
				tmp_double = (Float) docs.get(i).get(value_field);
			} else if (docs.get(i).get(value_field) instanceof Integer) {
				tmp_double = (Integer) docs.get(i).get(value_field);
			} else if (docs.get(i).get(value_field) instanceof Long) {
				tmp_double = (Long) docs.get(i).get(value_field);
			} else if (docs.get(i).get(value_field) instanceof String){
				try {
					tmp_double = Double.parseDouble((String) docs.get(i).get(value_field));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (x >= group_num) continue;
			resource.get(var).get(x).add(tmp_double);
		}
		
		for (String it : queryMap.keySet()) {
			for (int i = 0; i < group_num; i++) {
				queryArray.get(queryMap.get(it)).add(stat.run(resource.get(it).get(i)));
			}
		}
		
		attrList = new String[var_list.length];
		for (int i = 0; i < var_list.length; i++) {
			attrList[i] = "_" + (new Integer(i)).toString();
		}
		
//		WriteCSV writer = new WriteCSV();
//		try {
//			writer.writeArrayCSV(queryArray, attrList, ".", "data.csv");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	
}
