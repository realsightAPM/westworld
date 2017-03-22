package com.bnAnalysis;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.basic.Pair;
import com.basic.ReadCSV;

import norsys.netica.*;

public class RootCause {
	                                                                                                                                      
	public NeticaApi netica;
	public List<Pair<String, Double>> causeRank;

	public RootCause() throws Exception {}
	
	public RootCause(String target_var) throws Exception {
		loadNetica();
		getCauseRankOf(target_var);
	}
	
	public RootCause(SimuLoad simuLoad, String target_var) throws Exception {
		simuNetica(simuLoad);
		getCauseRankOf(target_var);
	}
	
	public void loadNetica() throws Exception {
		netica = new NeticaApi();
		netica.loadNet();
	}
	
	public void buildNetica(String original_csv, String target_var) throws Exception {
		netica = new NeticaApi();
		netica.buildNet(original_csv, 2, 3);
	}
	
	public void simuNetica(SimuLoad simuLoad) throws Exception {
		netica = simuLoad.netica;
	}
	
	public void getCauseRankOf(String target_var) throws Exception {
//		netica = new NeticaApi();
//		netica.buildNet(original_csv, 2, 3);
//		netica.loadNet();
		
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
			double belief = netica.getExeption(tmpNode.toString()+":"+netica.getState(tmpNode.getNumStates()-1), target_var);
			rankMap.put(-belief, tmpNode.toString());
		}
		
		causeRank = new ArrayList<Pair<String, Double>>();
		
		for (Double it : rankMap.keySet()) {
			causeRank.add(new Pair<String, Double> (rankMap.get(it), -it));
		}
	}
	
	
	public static void main (String[] args) throws Throwable{
	    RootCause rootCause = new RootCause("http_times");
	    
		
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
		
		rootCause.netica.finalize();
		
		String simuStr = "session_count";
		double times = 1;
		
		SimuLoad simu = new SimuLoad("inputjava_data1.csv", simuStr, times);
	    RootCause simuCause = new RootCause(simu,"http_times");
		
	    List<Double> newDist = simu.getNewDistribution(simuStr);
	    
		rank = simuCause.causeRank;
		System.out.println("\nsimu："+ simuStr + "*" + times + "后http_times期望：");
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		
		System.out.println("新的"+simuStr+"分布");
		
		for(int i = 0; i < newDist.size(); i++) {
			System.out.println(""+((char)('a'+i)) + ": " + newDist.get(i));
		}
		
	}
}
