package com.bnAnalysis;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.basic.Pair;

import norsys.netica.Environ;
import norsys.netica.Net;
import norsys.netica.Node;
import norsys.netica.Streamer;

public class RootCause {
	
	public NeticaApi netica;
	public List<Pair<String, Double>> causeRank;
	public List<Pair<String, Double>> causeRankEdge;

	public RootCause() throws Exception {
		getCauseRank("read.csv", "http_times");
	}
	
	public RootCause(String original_csv, String target_var) throws Exception {
		getCauseRank(original_csv, target_var);
	}
	
	public void getCauseRank(String original_csv, String target_var) throws Exception {
		netica = new NeticaApi(original_csv, 2);
		List<String[]> mapList = netica.banjo.separate.mapList;
		String[] attrList = netica.banjo .separate.attrList;
		causeRank = new ArrayList<Pair<String, Double>>(attrList.length);
		
		int pos = -1;
		for (int i = 0; i < attrList.length; i++) {
			System.out.println(attrList[i]) ;
			if (attrList[i].equals(target_var)) {
				pos = i;
				break;
			}
		}
		
		
		if (pos < 0) {
			System.out.println("目标变量不在attrList中");
			return;
		}
		
		try {
		
			Environ env = new Environ (null);
	
			// Read in the net created by the BuildNet.java example program
			Net net = new Net (new Streamer ("netica_out_dir/Learned_netica.dne"));
			Node[] nodes = new Node[attrList.length];
			
			for (int i = 0; i < attrList.length; i++) {
				nodes[i] = net.getNode(attrList[i]);
			}
			
			net.compile();
			
			/*** begin cause analysis ***/
			
			char highStateChar = (char)('a' + mapList.get(pos).length-1);
			String highStateStr = ""+highStateChar;
			nodes[pos].finding().enterState(highStateStr);
			
			System.out.println("\n find the state of " + attrList[pos] + " is " + highStateStr);
			
			Map<Double, String> rankMap = new TreeMap<Double, String> ();
			for (int i = 0; i < attrList.length; i++) {
				double expection = 0;
				for (int j = 0; j < mapList.get(i).length; j++) {
					double belief = nodes[i].getBelief(""+((char)('a'+j)));
					expection += j*belief;
				}
				rankMap.put(expection, attrList[i]);
			}
			for (Object it : rankMap.keySet()) {
				causeRank.add(new Pair(rankMap.get(it), it));
			}
			nodes[pos].finding().clear();
			rankMap.clear();
			/*** end cause analysis ***/
			
			/*** begin cause edge analysis ***/
			
			causeRankEdge = new ArrayList<Pair<String, Double>>(attrList.length);
			for (int i = 0; i < attrList.length; i++) {
				char highCauseChar = (char)('a' + mapList.get(i).length-1);
				String highCauseStr = "" + highCauseChar;
				nodes[i].finding().enterState(highCauseStr);
				
				double belief = nodes[pos].getBelief(highStateStr);
				rankMap.put(belief, attrList[i]);
				nodes[i].finding().clear();
			}
			for (Object it : rankMap.keySet()) {
				causeRankEdge.add(new Pair(rankMap.get(it), it));
			}
			
			
			net.finalize();   // not strictly necessary, but a good habit
			env.finalize();
			rankMap.clear();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String[] args) throws Exception{
	    RootCause rootCause = new RootCause("inputjava_data1.csv", "http_times");
		
		System.out.println("\n输出根源性的期望值：");
		List<Pair<String, Double>> rank = rootCause.causeRank;
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		
		System.out.println("\n输出边缘性风险：");
		rank = rootCause.causeRankEdge;
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		
	}
}
