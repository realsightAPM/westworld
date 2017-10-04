package com.realsight.westworld.bnanalysis.solr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.data.Stat;
import org.slf4j.LoggerFactory;

import com.realsight.westworld.bnanalysis.io.ReadTxt;
import com.realsight.westworld.bnanalysis.rpc.RemoteExecuteCommand;
import com.realsight.westworld.bnanalysis.zookeeper.ZkKeeper;

import ch.qos.logback.classic.Level;

public class SolrCloudKeeper {
	
	private static ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}
	
	public List<String> zkList;
	public List<String> solrList;
	public Map<String, String> pwdMap;
	
	public SolrCloudKeeper() {
		pwdMap = new HashMap<String, String>();
		ReadTxt readTxt = new ReadTxt();
		zkList = readTxt.readByLine("zk_hosts.txt");
		solrList = readTxt.readByLine("solr_hosts.txt");
		List<String> pwdList = readTxt.readByLine("solr_pwd.txt");
		for (String it : pwdList) {
			String[] tmp = it.split(" ");
			pwdMap.put(tmp[0], tmp[1]);
		}
	}

	List<String> gone(String zkServer) {
		ZkClient zkClient = null;
		List<String> solrs = null;
		String path = "/live_nodes";
		while (true) {
			try {
				zkClient= new ZkClient(zkServer, 5000);
				solrs = zkClient.getChildren(path);
				break;
			} catch (Exception e) {
				Date myDate = Calendar.getInstance().getTime();
				System.out.println("\n" + myDate.toString() + ": zookeeper连接异常");
				try {
					Thread.sleep(1000*6);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ZkKeeper zkKeeper = new ZkKeeper();
				zkKeeper.runKeeper();
			} finally {
				if (zkClient != null) {
					zkClient.close();
				}
			}
		}
		
		Set<String> runningSet = new HashSet<String>();
		List<String> res = new ArrayList<String>();
		
		for (String it : solrs) {
			runningSet.add(it.split("_")[0]);
//			System.out.println(it.split("_")[0]);
		}
		for (String it : solrList) {
			if (!runningSet.contains(it)) {
				res.add(it);
			}
		}
		return res;
	}
	
	public void runKeeper() {
		String command, zkServer = "";
		Map<String, String> portMap;
		portMap = new HashMap<String, String>();
		portMap.put("8983", "1");
		portMap.put("7574", "2");
		for (int i = 0; i < zkList.size(); i++) {
			zkServer += zkList.get(i);
			if (i+1 < zkList.size()) zkServer += ",";
		}
//		System.out.println(zkServer);
		List<String> goneList = gone(zkServer);
		if (goneList.isEmpty()) {
			System.out.print("ok");
		} else {
			System.out.println("\n开始重启：");
		}
		Date myDate = Calendar.getInstance().getTime();
		for (String it : goneList) {
			System.out.println(myDate.toString() + ": 重启" + it);
			String[] tmp = it.split(":");
			String host = tmp[0];
			String port = tmp[1];
			RemoteExecuteCommand rec=new RemoteExecuteCommand(host, "root", pwdMap.get(host));
			command = "/usr/share/solr/bin/solr restart -force -cloud -s /usr/share/solr/example/cloud/node" 
					+ portMap.get(port) + "/solr" + " -p " + port + " -z " + zkServer;
			String response = rec.execute(command);
			System.out.println("输出结果：");
			System.out.println(response);
		}
	}
	
	public static void main(String[] args) {
		
		PropertyConfigurator.configure( "log4j.properties" );
//		Logger logger  =  Logger.getLogger(SolrCloudKeeper.class);
//		logger.debug( " debug " );
//		logger.error( " error " );
		
		SolrCloudKeeper keeper = new SolrCloudKeeper();
		
		while (true) {
			keeper.runKeeper();
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
