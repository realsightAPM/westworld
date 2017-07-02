package com.realsight.westworld.bnanalysis.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.api.RootCause;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.service.NeticaApi;
import com.realsight.westworld.bnanalysis.solr.SolrReader;
import com.realsight.westworld.bnanalysis.solr.SolrReaderObject;
import com.realsight.westworld.bnanalysis.solr.SolrChecker;
import com.realsight.westworld.bnanalysis.solr.SolrConfigReader;
import com.realsight.westworld.bnanalysis.solr.SolrResults;
import com.realsight.westworld.bnanalysis.solr.SorlReaderSingle;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class BnServer implements Runnable{
	
	public static String url;    //配置文件路径
	public String bn_name;
	public SolrReaderObject reader;
	public NeticaApi netica;
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}
	
	
	public BnServer() {}
	
	public BnServer(String bn_name, SolrReaderObject reader) {
		this.bn_name = bn_name;          // 用于找配置
		this.reader = reader;    
	}
	
	public void run() {
		try {
			runBuild();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void runBuild() throws Exception {
		SolrConfigReader conf = new SolrConfigReader();
		conf.runRead(url, bn_name); // 同步
		
		SolrDocument option = conf.option;
		long start_time = (long) option.get("starttime_l"), cnt = 0, gap = (long) option.get("gap_l"), start, end;
		String fq_str = (String) option.get("fq_s");
		String[] fq = fq_str.split(",");
		
		SolrChecker checker = new SolrChecker();
		
		while (true) {
			start = start_time + gap*cnt;
			end = start + gap;
			int res = checker.check(option, end, fq);
			
			System.out.println("数据判断：" + res);
			Thread.sleep(2000);
			if (res > 0) {
				Mean mean = new Mean();
				reader.runRead(option, start, end, mean, fq); // 同步
				writeSolr(buildNet(netica), option, start);
				System.out.println("这是第"+cnt+"段");
			} else {
				System.out.println("等待积累" + (gap/1000/3600) + "小时的数据: " + bn_name);
				Thread.sleep(1800*1000);
			}
			cnt++;
		}
	}
	
	private static synchronized Pair<List<String>, List<String>> buildNet(NeticaApi netica) throws Exception {
		
		netica = new NeticaApi();
		netica.buildNet("log.csv", 2, 3);
//		netica.loadNet();
		Pair<List<String>, List<String>> pair = new Pair<List<String>, List<String>> (netica.getGONodes(), netica.getGoLinks());
		netica.finalize();
		return pair;
	}
	
	private void writeSolr(Pair<List<String>, List<String>> pair, SolrDocument option, long start) throws SolrServerException, IOException {
		SolrResults resulter = new SolrResults((String) option.get("solr_writer_url_s"));
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", option.get("bn_name_s")));
//		resulter.addResult(new Pair<String, Object> ("rs_timestamp", date.getTime()));
		resulter.addResult(new Pair<String, Object> ("start_timestamp_l", start));
		resulter.addResult(new Pair<String, Object> ("nodes_s", pair.first.toString()));
		resulter.addResult(new Pair<String, Object> ("edges_s", pair.second.toString()));
		String str = "";
		
		for (int i = 0; i < reader.queryList.size(); i++) {
			str += "^"+reader.queryList.get(i);
			try {
				List<Pair<String, Double>> cause = (new RootCause("_"+i, netica)).causeRank;
				System.out.println("规模： " + cause.size());
				String commit_str = "";
				for (Pair<String, Double> it : cause) {
					String tmp_str = it.first.substring(1)+":"+it.second.toString();
					commit_str += ","+tmp_str;
				}
				resulter.addResult(new Pair<String, Object> (""+i+"_s", commit_str.substring(1)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		resulter.addResult(new Pair<String, Object> ("query_list_s", str.substring(1)));
		resulter.write(); // 同步
	}
	
	
	public static void main(String[] args) throws Exception {

//		BnServer.url = "http://10.4.45.114:19983/solr/option";
//		Thread thread1 = new Thread(new BnServer("bn_test", new SolrReader()));
//		thread1.start();
		
		
		
		BnServer.url = "http://10.4.55.171:8983/solr/option/";
		Thread thread2 = new Thread(new BnServer("bn_test", new SorlReaderSingle()));
		thread2.start();
		
		
	}
}
