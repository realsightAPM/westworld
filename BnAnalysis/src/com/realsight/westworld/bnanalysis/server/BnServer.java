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
import com.realsight.westworld.bnanalysis.io.WriteCSV;
import com.realsight.westworld.bnanalysis.service.NeticaApi;
import com.realsight.westworld.bnanalysis.solr.SolrReader;
import com.realsight.westworld.bnanalysis.solr.SolrReaderMetricBeat;
import com.realsight.westworld.bnanalysis.solr.SolrReaderObject;
import com.realsight.westworld.bnanalysis.solr.SolrChecker;
import com.realsight.westworld.bnanalysis.solr.SolrConfigReader;
import com.realsight.westworld.bnanalysis.solr.SolrInitResult;
import com.realsight.westworld.bnanalysis.solr.SolrResults;
import com.realsight.westworld.bnanalysis.solr.SorlReaderSingle;
import com.realsight.westworld.bnanalysis.statistic.Mean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import norsys.netica.NeticaException;

public class BnServer implements Runnable{
	
	public static String url = null;             // 配置文件路径
	public static String time_field = null;      // 时间域
	public String bn_name;
	public SolrReaderObject reader;
	public NeticaApi netica;
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}
	
	private BnServer() {}
	
	public BnServer(String bn_name, SolrReaderObject reader) {
		this.bn_name = bn_name;          // 用于找配置
		this.reader = reader;   
//		Thread.currentThread().setName(bn_name);
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
		conf.runRead(BnServer.url, bn_name); // 同步
		long cnt = 0, start, end;
		
		SolrInitResult initResult = new SolrInitResult();       // 推初始数据没有边
		initResult.runInit(conf.option, conf.start_time);
		
		SolrChecker checker = new SolrChecker();
		while (true) {
			start = conf.start_time + conf.gap*cnt;
			end = start + conf.gap;
			int res = checker.check(conf.option, BnServer.time_field, end, conf.fq);
			
			System.out.println("阶段" + cnt + "数据判断：" + res);
			Thread.sleep(2000);
			if (res > 0) {
				Mean mean = new Mean();
				reader.runRead(conf.option, time_field, start, end, mean, conf.fq); // 同步
				netica = buildNet(reader);
				writeSolr(conf.option, start);
				System.out.println("这是第"+cnt+"段");
			} else {
				System.out.println("等待积累" + (conf.gap/1000/3600) + "小时的数据：" + bn_name);
				Thread.sleep(1800*1000);
			}
			cnt++;
		}
	}
	
	private static synchronized NeticaApi buildNet(SolrReaderObject readObj) throws Exception {
		WriteCSV writer = new WriteCSV();
		try {
			writer.writeArrayCSV(readObj.queryArray, readObj.attrList, ".", "data.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NeticaApi netica_obj = new NeticaApi();
		netica_obj.buildNet("data.csv", 1, 3);
		return netica_obj;
	}
	
	private void writeSolr(SolrDocument option, long start) throws SolrServerException, IOException, NeticaException {
		SolrResults resulter = new SolrResults((String) option.get("solr_writer_url_s"));
		
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", option.get("bn_name_s")));
//		resulter.addResult(new Pair<String, Object> ("rs_timestamp", date.getTime()));
		long time_now = Calendar.getInstance().getTimeInMillis();
		resulter.addResult(new Pair<String, Object> ("timestamp_l", time_now));
		resulter.addResult(new Pair<String, Object> ("start_timestamp_l", start));
		resulter.addResult(new Pair<String, Object> ("nodes_s", netica.getGONodes().toString()));
		resulter.addResult(new Pair<String, Object> ("edges_s", netica.getGoLinks().toString()));
		String str = "";
		
		for (int i = 0; i < reader.queryList.size(); i++) {
			str += "^"+reader.queryList.get(i).split(":")[1];
			try {
				List<Pair<String, Double>> cause = netica.getCauseRankOf("_"+i);
				System.out.println(i+"的根源" + cause.size() + "个");
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
		BnServer.url = "http://10.4.53.205:9983/solr/option";
		BnServer.time_field = "rs_timestamp_tdt";
//		Thread thread1 = new Thread(new BnServer("bn_show", new SolrReader()));
		Thread thread1 = new Thread(new BnServer("bn_napm", new SolrReaderMetricBeat()));
		thread1.start();
		
		
		
//		BnServer.url = "http://10.4.55.171:8983/solr/option/";
//		Thread thread2 = new Thread(new BnServer("bn_test", new SorlReaderSingle()));
//		thread2.start();
		
		
	}
}
