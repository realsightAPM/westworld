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
		separate = new Separate(original_csv);
	}
	
	public double getProbability(int attr, int state) { //ok
		return getProbability(new Pair<Integer, Integer>(attr, state));
	}
	
	public double getProbability(Pair<Integer, Integer> pair) {       //ok
		int sum = 0;
		for (int i = 0; i < separate.numInst; i++) {
			if (separate.sepData.get(pair.first).get(i) == pair.second)
				sum ++;
		}
		return sum*1.0/separate.numInst;
	}
	
	public double getUnionProbability(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) { //ok
		List<Pair<Integer, Integer>> towPair = new ArrayList<Pair<Integer, Integer>>(2);
		towPair.add(p1);
		towPair.add(p2);
		return getUnionProbability(towPair);
	}
	
	public double getUnionProbability(List<Pair<Integer, Integer>> unionList) { //ok
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
	
	public double getConditionalProbability(Pair<Integer, Integer> attr, Pair<Integer, Integer> conattr) { // ok
		
		int sum = 0, consum = 0;
		
		for (int i = 0; i < separate.numInst; i++) {
			if (separate.sepData.get(conattr.first).get(i) == conattr.second) {
				consum++;
				if (separate.sepData.get(attr.first).get(i) == attr.second) {
					sum++;
				}
			}
		}
		
		return 1.0*sum/consum;
	}
	
//	public double getConditionalProbability(List<Pair> unionList, List<Pair> conList) {
//		int sum = 0, conSum = 0;
//		int unionLen = unionList.size();
//		int conLen = conList.size();
//		for (int i = 0; i < separate.numInst; i++) {
//			int tmpSum = 0;
//			for (int j = 0; j < conLen; j++) {
//				if (separate.sepData.get(conList.get(j).first).get(i) == conList.get(j).second)
//					tmpSum ++;
//			}
//			if (tmpSum == conLen) {
//				conSum ++;
//				int tmpSum2 = 0;
//				for (int k = 0; k < unionLen; k++) {
//					if (separate.sepData.get(unionList.get(k).first).get(i) == unionList.get(k).second)
//						conSum ++;
//				}
//				if (tmpSum2 == unionLen)
//					sum ++;
//			}
//		}
//		return sum*1.0/conSum;
//	}
	
	public static void main(String[] args) throws Exception {
		
		Probability prob = new Probability();
		
		List<String[]> mapList = prob.separate.mapList;
		
//		for (int i = 0; i < mapList.size(); i++) {
//			String[] attrMap = mapList.get(i);
//			double sum = 0;
//			for (int j = 0; j < attrMap.length; j++) {
//				double x = prob.getProbability(new Pair(i, j));
//				sum += x;
//				System.out.print(x + "\t");
//			}
//			System.out.println("\n" + sum);
//		}
		
		int attr_1 = 0, attr_2 = 2;
		
		double sum = 0;
		for (int i = 0; i < mapList.get(attr_1).length; i++) {
			for (int j = 0; j < mapList.get(attr_2).length; j++) {
				double x = prob.getUnionProbability(new Pair(attr_1, i), new Pair(attr_2, j));
				sum += x;
				System.out.print(x + "\t");
			}
			System.out.println();
		}
		System.out.println("sum = " + sum);
	}
	
}
