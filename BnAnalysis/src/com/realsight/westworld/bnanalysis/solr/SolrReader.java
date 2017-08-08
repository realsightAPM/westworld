package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.io.WriteCSV;

import org.apache.solr.client.solrj.response.FacetField.Count;

public class SolrReader extends SolrReaderObject {
	
	
	
	public SolrReader(){}
	
	public void runRead(SolrDocument option, String time_field, long start, long end, Statistic stat, String... fq) {
		
		SolrClient solr = new HttpSolrClient.Builder(option.get("solr_reader_url_s").toString()).build();
		
		String[] res_list = option.get("res_list_s").toString().split(",");
		String[] var_list = option.get("indexList_s").toString().split(",");
		
		long interval = (long) option.get("interval_l");
		long gap = (long) option.get("gap_l");
		int group_num = (int) (gap/interval);
		
		HashMap<String, HashMap<String, ArrayList<ArrayList<Double>>>> resource = new HashMap<String, HashMap<String, ArrayList<ArrayList<Double>>>>();
		for (int i = 0; i < res_list.length; i++) {
			resource.put(res_list[i], new HashMap<String, ArrayList<ArrayList<Double>>>());
		}
		for (int i = 0; i < var_list.length; i++) {
			String[] tmp = var_list[i].split(":");
			resource.get(tmp[0]).put(tmp[1], new ArrayList<ArrayList<Double>>());
			for (int j = 0; j < group_num; j++) {
				resource.get(tmp[0]).get(tmp[1]).add(new ArrayList<Double>());
			}
		}
		
		SolrQuery[] solrQuery = new SolrQuery[res_list.length];
		for (int i = 0; i < solrQuery.length; i++) {
			solrQuery[i] = new SolrQuery();
		}
		for (int i = 0; i < solrQuery.length; i++) {
			solrQuery[i].setQuery("*:*");
			String[] fq_list = new String[fq.length+2];
			for (int j = 0; j < fq.length; j++) {
				fq_list[j] = fq[j];
			}
			
			String rs_start = TimeUtil.formatUnixtime2(start);
			String rs_end = TimeUtil.formatUnixtime2(end);
			
			fq_list[fq.length] = time_field + ":[" + rs_start + " TO " + rs_end + "]";
			
			System.out.println("fq=" + fq_list[fq.length]);
			
			fq_list[fq.length+1] = "res_id:" + res_list[i];
			solrQuery[i].setFilterQueries(fq_list);
			solrQuery[i].setRows(2000000);
//			solrQuery[i].setSort("timestamp_l", ORDER.asc);
			solrQuery[i].setSort(time_field, ORDER.asc);
		}
		
		
		queryList = new ArrayList<String>();
		queryMap = new HashMap<String, Integer>();
		queryArray = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < var_list.length; i++) {
			queryList.add(var_list[i]);
			queryMap.put(var_list[i], i);
			queryArray.add(new ArrayList<Double>());
		}
		
		QueryResponse[] response = new QueryResponse[solrQuery.length];
		for (int i = 0; i < response.length; i++) {
			response[i] = new QueryResponse();
		}
		SolrDocumentList[] docs = new SolrDocumentList[solrQuery.length];
		for (int i = 0; i < docs.length; i++) {
			docs[i] = new SolrDocumentList();
		}
		
		while (true) {
			try {
				for (int i = 0; i < response.length; i++) {
					response[i] = solr.query(solrQuery[i]);
				}
				break;
			} catch (Exception e) {
				System.out.print("网络read异常");
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
//				long timestamp_tmp = (long) docs[i].get(j).get("timestamp_l");
				
				long timestamp_tmp = ((Date) docs[i].get(j).get(time_field)).getTime(); 
				int x = (int) ((timestamp_tmp-start)/interval);
				for (String it : resource.get(res_list[i]).keySet()) {
					double tmp_double = 0;
					
					if (docs[i].get(j).get(it) instanceof Double) {
						tmp_double = (Double) docs[i].get(j).get(it);
					} else if (docs[i].get(j).get(it) instanceof Float) {
						tmp_double = (Float) docs[i].get(j).get(it);
					} else if (docs[i].get(j).get(it) instanceof Integer) {
						tmp_double = (Integer) docs[i].get(j).get(it);
					}
					else if (docs[i].get(j).get(it) instanceof Long) {
							tmp_double = (Long) docs[i].get(j).get(it);
					} else if (docs[i].get(j).get(it) instanceof String){
						try {
							tmp_double = Double.parseDouble(docs[i].get(j).get(it).toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (x >= group_num) continue;
					resource.get(res_list[i]).get(it).get(x).add(tmp_double);
				}
			}
		}
		
		for (String it : resource.keySet()) {
			int cnt_1 = 0;
			for (String it2 : resource.get(it).keySet()) {
				String tmp_str = it + ":" + it2;
				for (int i = 0; i < group_num; i++) {
					
					queryArray.get(queryMap.get(tmp_str)).add(stat.run(resource.get(it).get(it2).get(i)));
				}
				cnt_1++;
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
	
	public static void main(String[] args) throws IOException {
		
	}
}
