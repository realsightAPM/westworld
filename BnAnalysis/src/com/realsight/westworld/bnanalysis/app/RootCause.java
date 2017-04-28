package com.realsight.westworld.bnanalysis.app;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.basic.SimuLoad;
import com.realsight.westworld.bnanalysis.io.ReadCSV;
import com.realsight.westworld.bnanalysis.service.NeticaApi;
import com.realsight.westworld.bnanalysis.service.SimuCPT;

import norsys.netica.*;

public class RootCause {
	
	// 载入的netica是哪一个网络，就进行那个分析。
	public List<Pair<String, Double>> causeRank;

	public RootCause() {}
	
	public RootCause(String target_var, NeticaApi netica) throws Exception {
		getCauseRankOf(target_var, netica);
	}
	
	private void getCauseRankOf(String target_var, NeticaApi netica) throws Exception {
//		netica = new NeticaApi();
//		netica.buildNet(original_csv, 2, 3);
//		netica.loadNet();
		netica.loadRangeMap();
		
		if (!netica.rangeMap.containsKey(target_var)) {
			System.out.println("target_var if not exist");
			return;
		}
		
		Map<Double, String> rankMap = new TreeMap<Double, String> ();
		NodeList nodeList = netica.net.getNodes();
		for (int i = 0; i < nodeList.size(); i++) {
			Node tmpNode = nodeList.getNode(i);
			if (tmpNode.toString().equals(target_var)) {
				continue;
			}
			//设为最高状态
			double belief = netica.getExeption(tmpNode.toString()+":"+netica.getState(tmpNode.getNumStates()-1), target_var);
			rankMap.put(-belief, tmpNode.toString());
		}
		
		causeRank = new ArrayList<Pair<String, Double>>();
		
		for (Double it : rankMap.keySet()) {
			causeRank.add(new Pair<String, Double> (rankMap.get(it), -it));
		}
	}
	
	
	public static void main (String[] args) throws Throwable{
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
	    RootCause rootCause = new RootCause("http_times", netica);
	    
		
//		System.out.println("\n输出根源性的期望值：");
		List<Pair<String, Double>> rank = rootCause.causeRank;
//		for (int i = 0; i < rank.size(); i++) {
//			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
//		}
//		
		System.out.println("\nhttp_times期望：");
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		
		netica.finalize();
		
		String simuStr = "session_count";
		double times = 20;
		
		SimuLoad simu = new SimuLoad("inputjava_data1.csv", simuStr, times);   // 只是用来生成模拟的.tsv文件
//		netica.loadSimuNet();
		SimuCPT simuCPT = new SimuCPT();
		simuCPT.setSimuCPT(simuStr);
		netica = simuCPT.loadSimuCPT();
	    RootCause simuCause = new RootCause("http_times", netica);
	    
		rank = simuCause.causeRank;
		System.out.println("\nsimu："+ simuStr + "*" + times + "后http_times期望：");
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		netica.finalize();
		
	}
}
