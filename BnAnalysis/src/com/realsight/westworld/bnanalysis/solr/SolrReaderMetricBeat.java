package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.Dao.Statistic;
import com.realsight.westworld.bnanalysis.io.WriteCSV;
import com.realsight.westworld.bnanalysis.server.BnServer;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SolrReaderMetricBeat extends SolrReaderObject {
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}

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
			value_list[i] = tmp[1];
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
			for (int k = 0; k < fq_list.length; k++) {
				System.out.print(fq_list[k]+"  ");
			}
			System.out.println();
			solrQuery[i].setFilterQueries(fq_list);
			solrQuery[i].setFields(value_list[i], time_field);
			System.out.println("过滤区：" + value_list[i]);
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
//		
//		try {
//			solr.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		for (int i = 0; i < docs.length; i++) {
			docs[i] = response[i].getResults();
			
			System.out.println("docs.size(): " + docs[i].size());
			
			for (String it : docs[i].get(0).keySet()) {
				System.out.print(it + " ");
			}
			System.out.println();
			
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
//				if (value_list[i].indexOf("idle") > -1) {
//					resource.get(i).get(x).add(1 - tmp_double);
//				} else {
					resource.get(i).get(x).add(tmp_double);
//				}
				
			}
		}
		
		
		/********************获取应用进程资源消耗**********************/
		
		List<String> app_list = new FacetApplication("http://10.0.67.14:8080/solr/metrics").appList;          // 获取的应用列表
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		for (int i = 0; i < app_list.size(); i++) {
			queryList.add("process:"+app_list.get(i));
			map.put(app_list.get(i), i);
			queryArray.add(new ArrayList<Double>());
		}
		
		List<ArrayList<ArrayList<Double>>> process = new ArrayList<ArrayList<ArrayList<Double>>>();    // 原始数据
		for (int i = 0; i < app_list.size(); i++) {
			process.add(new ArrayList<ArrayList<Double>> ());
			for (int j= 0; j < group_num; j++) {
				process.get(i).add(new ArrayList<Double> ());
			}
		}
		
		   // 查询
		SolrQuery queryApp = new SolrQuery();
		queryApp.setQuery("*:*");
		String[] fq_list = new String[fq.length+2];
		for (int j = 0; j < fq.length; j++) {
			fq_list[j] = fq[j];
		}
		String rs_start = TimeUtil.formatUnixtime2(start);
		String rs_end = TimeUtil.formatUnixtime2(end);
		
		fq_list[fq.length] = time_field + ":[" + rs_start + " TO " + rs_end + "]";
		fq_list[fq.length+1] = "metricset_name_s:process";
		for (int k = 0; k < fq_list.length; k++) {
			System.out.print(fq_list[k]+"  ");
		}
		System.out.println();
		queryApp.setFilterQueries(fq_list);
		List<String> tmp_array = (List<String>) option.get("processControl_ss");
		String[] fl = new String[tmp_array.size()+1];
		for (int i = 0; i < tmp_array.size(); i++) {
			fl[i] = tmp_array.get(i);
		}
		fl[tmp_array.size()] = time_field;
		queryApp.setFields(fl);
		queryApp.setRows(2000000);
		queryApp.setSort(time_field, ORDER.asc);
		  // End 查询
		
		QueryResponse process_response = new QueryResponse();
		SolrDocumentList process_docs = new SolrDocumentList();
		
		while (true) {
			try {
				process_response = solr.query(queryApp);
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
		
			process_docs = process_response.getResults();
			System.out.println("process_docs.size(): " + process_docs.size());
			for (int j = 0; j < process_docs.size(); j++) {
				String val_name = (String) process_docs.get(j).get("system_process_name_s");
				
				long timestamp_tmp = ((Date) process_docs.get(j).get(time_field)).getTime();
				
				int x = (int) ((timestamp_tmp-start)/interval);
				double tmp_double = 0;
				
				if (process_docs.get(j).get("system_process_cpu_total_pct_f") instanceof Double) {
					tmp_double = (Double) process_docs.get(j).get("system_process_cpu_total_pct_f");
				} else if (process_docs.get(j).get("system_process_cpu_total_pct_f") instanceof Float) {
					tmp_double = (Float) process_docs.get(j).get("system_process_cpu_total_pct_f");
				} else if (process_docs.get(j).get("system_process_cpu_total_pct_f") instanceof Integer) {
					tmp_double = (Integer) process_docs.get(j).get("system_process_cpu_total_pct_f");
				}
				else if (process_docs.get(j).get("system_process_cpu_total_pct_f") instanceof Long) {
						tmp_double = (Long) process_docs.get(j).get("system_process_cpu_total_pct_f");
				} else if (process_docs.get(j).get("system_process_cpu_total_pct_f") instanceof String){
					try {
						tmp_double = Double.parseDouble(process_docs.get(j).get("system_process_cpu_total_pct_f").toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (x >= group_num) continue;
//				System.out.println(val_name+" "+map.get(val_name)+" "+x);
				process.get(map.get(val_name)).get(x).add(tmp_double);
			}
		
		/************************* END *************************/
		
		
		for (int i = 0; i < var_list.length; i++) {
			for (int j = 0; j < group_num; j++) {
				queryArray.get(i).add(stat.run(resource.get(i).get(j)));
			}
		}
		
		for (int i = 0; i < app_list.size(); i++) {
			for (int j = 0; j < group_num; j++) {
				queryArray.get(var_list.length+i).add(stat.run(process.get(i).get(j)));
			}
		}
		
		attrList = new String[var_list.length+app_list.size()];
		for (int i = 0; i < attrList.length; i++) {
			attrList[i] = "_" + (new Integer(i)).toString();
		}
		
		for (int i = 0; i < queryList.size(); i++) {
			System.out.println(i + "  " + queryList.get(i));
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		SolrConfigReader conf = new SolrConfigReader();
		conf.runRead("http://10.0.67.14:8080/solr/option", "bn_metrics2");
		SolrReaderMetricBeat reader = new SolrReaderMetricBeat();
		System.out.println(conf.fq);
		reader.runRead(conf.option, "rs_timestamp_tdt", conf.start_time, conf.start_time+conf.gap, new Mean(), conf.fq);
		WriteCSV writer = new WriteCSV();
		writer.writeArrayCSV(reader.queryArray, reader.attrList, ".", "data.csv");
	}

}
