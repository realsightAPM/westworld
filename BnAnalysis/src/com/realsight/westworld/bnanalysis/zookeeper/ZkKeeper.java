package com.realsight.westworld.bnanalysis.zookeeper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realsight.westworld.bnanalysis.io.ReadTxt;
import com.realsight.westworld.bnanalysis.rpc.RemoteExecuteCommand;

public class ZkKeeper {

	List<String> zkList;
	Map<String, String> pwdMap;
	
	public ZkKeeper() {
		pwdMap = new HashMap<String, String>();
		ReadTxt read = new ReadTxt();
		zkList = read.readByLine("zk_hosts.txt");
		List<String> pwdList = read.readByLine("solr_pwd.txt");
		for (String it : pwdList) {
			String[] tmp = it.split(" ");
			pwdMap.put(tmp[0], tmp[1]);
		}
	}
	
	public boolean okMode(String response) {
		int a = response.indexOf("follower");
		int b = response.indexOf("leader");
		return (a > -1 || b > -1);
	}
	
	public boolean zkIsOk(RemoteExecuteCommand rec) {
		String command = "/usr/share/zookeeper/bin/zkServer.sh status";
		String response = rec.execute(command);
		return okMode(response);
	}
	
	public String zkRestart(RemoteExecuteCommand rec) {
		String command = "/usr/share/zookeeper/bin/zkServer.sh restart";
		String response = rec.execute(command);
		return response;
	}
	
	public void runKeeper() {
		for (String zkServer : zkList) {
			String host = zkServer.split(":")[0];
			RemoteExecuteCommand rec=new RemoteExecuteCommand(host, "root", pwdMap.get(host));
			if (zkIsOk(rec)) {
				System.out.println(host + " is " + "ok");
			} else {
				System.out.println(host + " is " + "down");
				String response = zkRestart(rec);
				System.out.println(response);
			}
		}
	}
	
	public static void main(String[] args) {
		ZkKeeper zk = new ZkKeeper();
		zk.runKeeper();
	}
	
}
