package com.bnAnalysis;


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

	public RootCause() throws Exception {
		getCauseRankOf("http_times");
	}
	
	public RootCause(String target_var) throws Exception {
		getCauseRankOf(target_var);
	}
	
	public void getCauseRankOf(String target_var) throws Exception {
		netica = new NeticaApi();
//		netica.buildNet(original_csv, 2, 3);
		netica.loadNet();
		
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
	
	
	public static void main (String[] args) throws Exception{
	    RootCause rootCause = new RootCause();
		
//		System.out.println("\n输出根源性的期望值：");
		List<Pair<String, Double>> rank = rootCause.causeRank;
//		for (int i = 0; i < rank.size(); i++) {
//			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
//		}
//		
		System.out.println("\n输出边缘性风险：");
		rank = rootCause.causeRank;
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		
	}
}
