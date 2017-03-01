package com.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.basic.Pair;
import com.basic.Separate;

public class Probability {

	public Separate separate;
	
	public Probability() throws Exception {          // read the default file "read.csv";
		separate = new Separate();
	}
	
	public Probability(String original_csv) throws Exception {  // read the file original_csv;
		Separate sep = new Separate(original_csv);
	}
	
	public double getProbability(int attr, int state) {
		return getProbability(new Pair(attr, state));
	}
	
	public double getProbability(Pair pair) {
		int sum = 0;
		for (int i = 0; i < separate.numInst; i++) {
			if (separate.sepData.get(pair.first).get(i) == pair.second)
				sum ++;
		}
		return sum*1.0/separate.numInst;
	}
	
	public double getUnionProbability(Pair p1, Pair p2) {
		List<Pair> towPair = new ArrayList<Pair>(2);
		towPair.add(p1);
		towPair.add(p2);
		return getUnionProbability(towPair);
	}
	
	public double getUnionProbability(List<Pair> unionList) {
		int sum = 0, unionLen = unionList.size();
		for (int i = 0; i < separate.numInst; i++) {
			int tmpSum = 0;
			for (int j = 0; j < unionLen; j++) {
				if (separate.sepData.get(unionList.get(j).first).get(i) == unionList.get(j).second)
					tmpSum ++;
			}
			if (unionLen == tmpSum)
				sum ++;
		}
		return sum*1.0/separate.numInst;
	}
	
	public double getConditionalProbability(Pair p1, Pair p2) {
		List<Pair> list_1 = new ArrayList<Pair>(1);
		List<Pair> list_2 = new ArrayList<Pair>(1);
		
		list_1.add(p1);
		list_2.add(p2);
		return getConditionalProbability(list_1, list_2);
	}
	
	public double getConditionalProbability(List<Pair> unionList, List<Pair> conList) {
		int sum = 0, conSum = 0;
		int unionLen = unionList.size();
		int conLen = conList.size();
		for (int i = 0; i < separate.numInst; i++) {
			int tmpSum = 0;
			for (int j = 0; j < conLen; j++) {
				if (separate.sepData.get(conList.get(j).first).get(i) == conList.get(j).second)
					tmpSum ++;
			}
			if (tmpSum == conLen) {
				conSum ++;
				int tmpSum2 = 0;
				for (int k = 0; k < unionLen; k++) {
					if (separate.sepData.get(unionList.get(k).first).get(i) == unionList.get(k).second)
						conSum ++;
				}
				if (tmpSum2 == unionLen)
					sum ++;
			}
		}
		return sum*1.0/conSum;
	}
}
