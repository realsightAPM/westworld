package com.realsight.westworld.bnanalysis.api;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.io.ReadCSV;

import norsys.netica.*;

public class RootCause {
	
	// �����netica����һ�����磬�ͽ����Ǹ�������
	public List<Pair<String, Double>> causeRank;

	public RootCause() {}
	
	public RootCause(String target_var, NeticaApi netica) throws Exception {
		getCauseRankOf(target_var, netica);
	}
	
	private void getCauseRankOf(String target_var, NeticaApi netica) throws Exception {
		netica = new NeticaApi();
//		netica.buildNet(original_csv, 2, 3);
		netica.loadNet();
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
			//��Ϊ���״̬
			double belief = netica.getExeption(tmpNode.toString()+":"+netica.getState(tmpNode.getNumStates()-1), target_var);
			rankMap.put(belief, tmpNode.toString());
		}
		
		causeRank = new ArrayList<Pair<String, Double>>();
		
		for (Double it : rankMap.keySet()) {
			causeRank.add(new Pair<String, Double> (rankMap.get(it), it));
		}
	}
	
	
	public static void main (String[] args) throws Throwable{
		
	}
}
