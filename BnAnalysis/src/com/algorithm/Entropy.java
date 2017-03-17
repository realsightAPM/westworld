package com.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.basic.Pair;

public class Entropy {

	public Probability prob;
	public Double[] entropyList;
	public Double[][] entropyUnionMatrix;
	public Double[][] entropyConditionalMatrix;
	
	public Entropy() throws Exception{
		initialize("read.csv", 3);
	}
	
	public Entropy(String original_csv, int num_bins) throws Exception {
		initialize(original_csv, num_bins);
	}
	
	private void initialize(String original_csv, int num_bins) throws Exception {
		prob = new Probability(original_csv, num_bins);
		List<Pair> attrPairList = new ArrayList<Pair>();
		
		entropyList = new Double[prob.separate.numAttr];
		entropyUnionMatrix = new Double[prob.separate.numAttr][prob.separate.numAttr];
		entropyConditionalMatrix = new Double[prob.separate.numAttr][prob.separate.numAttr];
		
		for (int i = 0; i < prob.separate.numAttr; i++) {
			attrPairList.add(new Pair(i, prob.separate.mapList.get(i).length));
		}
		
		/*** 熵数组 ***/
		for (int i = 0; i < prob.separate.numAttr; i++) {
			entropyList[i] = getH(attrPairList.get(i));
		}
		
		/*** 联合熵数组 ***/
		for (int i = 0; i < prob.separate.numAttr; i++) {
			for (int j = 0; j < prob.separate.numAttr; j++) {
				entropyUnionMatrix[i][j] = getUnionH(attrPairList.get(i), attrPairList.get(j));
			}
		}
		
		/*** 条件熵数组 ***/
		for (int i = 0; i < prob.separate.numAttr; i++) {
			for (int j = 0; j < prob.separate.numAttr; j++) {
				entropyConditionalMatrix[i][j] = getConditionalH(attrPairList.get(i), attrPairList.get(j));
			}
		}
	}
	
	private double H(double x) {
		return - x * Math.log(x);
	}
	
	private double H(double x, double y) {
		return - x * Math.log(y);
	}
	
	private double getH(Pair<Integer, Integer> attr) {     // first为属性编号，second为该属性值的个数
		return getH(attr.first, attr.second);
	}
	
	private double getH(int attr, int interval_size) {
		double sum = 0;
		for (int i = 0; i < interval_size; i++) {
			double x = prob.getProbability(attr, i);
			if (x < 0.000001)
				continue;
			sum += H(x);
		}
		return sum;
	}
	
	/*** 这个地方通用的方法不好写，暂时为两个变量的算法。对于互信息已经足够，对于条件互信息也只需要3个变量就够了 ***/
	
	private double getUnionH(Pair<Integer, Integer> attr_1, Pair<Integer, Integer> attr_2) {
		double sum = 0;
		for (int i = 0; i < attr_1.second; i++) {
			for (int j = 0; j < attr_2.second; j++) {
				double x = prob.getUnionProbability(new Pair(attr_1.first, i), new Pair(attr_2.first, j));
				if (x < 0.000001)
					continue;
				sum += H(x);
			}
		}
		return sum;
	}
	
	private double getConditionalH(Pair<Integer, Integer> attr, Pair<Integer, Integer> conattr) {
		double sum = 0;
		for (int i = 0; i < attr.second; i++) {
			for (int j = 0; j < conattr.second; j++) {
//				sum += prob.getConditionalProbability(new Pair(attr.first, i), new Pair(conattr.first, j));
				double x = prob.getUnionProbability(new Pair(attr.first, i), new Pair(conattr.first, j));
				double y = prob.getConditionalProbability(new Pair(attr.first, i), new Pair(conattr.first, j));
				if (y < 0.000001)
					continue;
				sum += H(x, y);
			}
		}
		return sum;
	}
	
	public static void main(String[] args) throws Exception {
		
		Entropy entropy = new Entropy();
		
		int numAttr = entropy.prob.separate.numAttr;
		
		for (int i = 0; i < numAttr; i++) {
			System.out.print(entropy.entropyList[i]+"\t");
		}
		System.out.println("\n");
		
		for (int i = 0; i < numAttr; i++) {
			for (int j = 0; j < numAttr; j++) {
				System.out.print(entropy.entropyUnionMatrix[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		
		for (int i = 0; i < numAttr; i++) {
			for (int j = 0; j < numAttr; j++) {
				System.out.print(entropy.entropyConditionalMatrix[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
}
