package com.realsight.westworld.bnanalysis.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.solr.client.solrj.SolrServerException;

import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.solr.SolrOptionKeeper;
import com.realsight.westworld.bnanalysis.solr.SolrReader;
import com.realsight.westworld.bnanalysis.solr.SolrOneDoc;

public class BnOptionServer {

	private BnOptionServer() {}

	public BnOptionServer(String url, String time_field) {
		BnServer.url = url;
		BnServer.time_field = time_field;
	}

	public void runServer() {
		
		

		SolrOptionKeeper keeper = new SolrOptionKeeper(BnServer.url);
		ExecutorService threadPool = Executors.newCachedThreadPool();

		while (true) {
			System.out.println("每分钟检测一次配置：" + BnServer.url);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("结束睡眠");
			String bn_name = keeper.runKeeper();
//			System.out.println("配置检测结束 ");
			System.out.println("运行配置：" + bn_name);
			if (bn_name != null) {
				threadPool.execute(new BnServer(bn_name, new SolrReader()));
			}
		}
	}
	
	private void writeExample(String rca_url) throws SolrServerException, IOException {
		SolrOneDoc resulter = new SolrOneDoc(rca_url);
		
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "example"));
		long time_now = Calendar.getInstance().getTimeInMillis();
		resulter.addResult(new Pair<String, Object> ("timestamp_l", time_now));
		resulter.addResult(new Pair<String, Object> ("start_timestamp_l", time_now));
		String nodes_s = "[{\"key\": \"0\"}, {\"key\": \"3\"}, {\"key\": \"11\"}, {\"key\": \"1\"}, {\"key\": \"9\"}, {\"key\": \"10\"}, {\"key\": \"7\"}, {\"key\": \"4\"}, {\"key\": \"8\"}, {\"key\": \"2\"}, {\"key\": \"5\"}, {\"key\": \"6\"}]";
		resulter.addResult(new Pair<String, Object> ("nodes_s", nodes_s));
		String edges_s = "[{\"from\": \"0\", \"to\": \"3\"}, {\"from\": \"0\", \"to\": \"4\"}, {\"from\": \"0\", \"to\": \"9\"}, {\"from\": \"3\", \"to\": \"7\"}, {\"from\": \"3\", \"to\": \"10\"}, {\"from\": \"11\", \"to\": \"1\"}, {\"from\": \"11\", \"to\": \"5\"}, {\"from\": \"11\", \"to\": \"10\"}, {\"from\": \"1\", \"to\": \"4\"}, {\"from\": \"1\", \"to\": \"6\"}, {\"from\": \"1\", \"to\": \"10\"}, {\"from\": \"9\", \"to\": \"7\"}, {\"from\": \"9\", \"to\": \"8\"}, {\"from\": \"10\", \"to\": \"7\"}, {\"from\": \"7\", \"to\": \"4\"}, {\"from\": \"7\", \"to\": \"8\"}, {\"from\": \"4\", \"to\": \"5\"}, {\"from\": \"4\", \"to\": \"8\"}, {\"from\": \"8\", \"to\": \"2\"}, {\"from\": \"2\", \"to\": \"5\"}, {\"from\": \"2\", \"to\": \"6\"}, {\"from\": \"5\", \"to\": \"6\"}]";
		resulter.addResult(new Pair<String, Object> ("edges_s", edges_s));
		
		resulter.addResult(new Pair<String, Object> (""+0+"_s", "7:0.018012522254139185,10:0.02564102690666914,2:0.02868338581174612,8:0.09999395161867142,6:0.12057067081332207,5:0.12929749488830566,4:0.13261864334344864,3:1.7134260386228561"));
		resulter.addResult(new Pair<String, Object> (""+1+"_s", "7:0.025641025975346565,3:0.02564102690666914,10:0.025641031563282013,2:0.028804518282413483,8:0.10294726118445396,6:0.12335248291492462,5:0.12929553538560867,4:0.13261962309479713"));
		resulter.addResult(new Pair<String, Object> (""+2+"_s", "10:0.02978713344782591,3:0.032679054886102676,0:0.03296343982219696,1:0.03309546783566475,4:0.03730527125298977,7:0.037575443275272846,8:0.03797468449920416,5:0.03972035087645054,6:0.1434381604194641"));
		resulter.addResult(new Pair<String, Object> (""+3+"_s", "7:0.017994819674640894,10:0.0265357019379735,2:0.02939986903220415,8:0.09653913229703903,6:0.11646588519215584,5:0.12472128123044968,4:0.12787092849612236,0:1.7857143133878708"));
		resulter.addResult(new Pair<String, Object> (""+4+"_s", "10:0.19199196249246597,2:0.24492404609918594,3:0.923143595457077,0:0.989630788564682,1:0.9896537959575653,6:1.3018862307071686,5:1.4347281455993652,7:1.4728628993034363,8:1.876285582780838"));
		resulter.addResult(new Pair<String, Object> (""+5+"_s", "10:0.21316564828157425,2:0.3425947576761246,3:0.9995433241128922,0:1.0710604190826416,1:1.071067899465561,6:1.4941367506980896,8:1.5539085865020752,4:1.5801300406455994,7:1.593470424413681"));
		resulter.addResult(new Pair<String, Object> (""+6+"_s", "10:0.2042299583554268,3:0.8955990076065063,0:0.9583486318588257,1:0.992915689945221,2:0.9938413202762604,8:1.367348700761795,4:1.3824404776096344,7:1.4020559787750244,5:1.426581233739853"));
		resulter.addResult(new Pair<String, Object> (""+7+"_s", "3:0.0714285746216774,0:0.07447093725204468,1:0.10792402178049088,10:0.10792403668165207,2:0.13927491009235382,6:0.7425909042358398,5:0.8100436329841614,4:0.827333390712738,8:0.8679182529449463"));
		resulter.addResult(new Pair<String, Object> (""+8+"_s", "10:0.1827288717031479,2:0.2374018207192421,3:0.6620648801326752,0:0.7091881185770035,1:0.731054276227951,6:1.2258175611495972,5:1.341997891664505,7:1.4703157246112823,4:1.785443127155304"));
		resulter.addResult(new Pair<String, Object> (""+9+"_s", "6:0.0"));
		resulter.addResult(new Pair<String, Object> (""+10+"_s", "6:0.0"));
		resulter.addResult(new Pair<String, Object> (""+11+"_s", "6:0.0"));
		
		String query_list = "JAVAEE_Durations_sum^JAVAEE_Memory_used^JAVAEE_Http_global^JAVAEE_Cpu_time^PHP_memory_cacheuse^PHP_disk_use^PHP_CPU_used^PHP_disk_percent^PHP_memory_use^DOTNET_User_Time^DOTNET_Disk_Time^DOTNET_Bytes_Total_sec";
		
		resulter.addResult(new Pair<String, Object> ("query_list_s", query_list));
		resulter.write(); // 同步		
	}

	public static void main(String[] args) {

		Path root = Paths.get(System.getProperty("user.dir")).getParent();
		Path propertyPath = Paths.get(root.toString(), "config", "rsapmml.properties");
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(propertyPath.toFile()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = property.getProperty("option_solr_url");
		String time_field = property.getProperty("time_field");
		String rca_url = property.getProperty("result_solr_url");
		
		if (time_field == null) {
			time_field = "rs_timestamp";
		}
		
		System.out.println("option_solr_url: " + url + "; time_field: " + time_field);

		BnOptionServer optionServer = new BnOptionServer(url, time_field);
		if (rca_url != null) {
			try {
				optionServer.writeExample(rca_url);
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		optionServer.runServer();
	}
}
