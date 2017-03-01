package com.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.basic.Pair;

public class Entropy {

	public Probability prob;
	public List<Double> entropyList;
	public List<ArrayList<Double>> entropyUnionMatrix;
	public List<ArrayList<Double>> entropyConditionalMatrix;
	
	public Entropy() throws Exception{
		initialize("read.csv");
	}
	
	public Entropy(String original_csv) throws Exception {
		initialize(original_csv);
	}
	
	private void initialize(String original_csv) throws Exception {
		prob = new Probability(original_csv);
		List<Pair> attrPairList = new ArrayList<Pair>();
		entropyList = new ArrayList<Double>(prob.separate.numAttr);
		entropyUnionMatrix = new ArrayList<ArrayList<Double>>(prob.separate.numAttr);
		entropyConditionalMatrix = new ArrayList<ArrayList<Double>>(prob.separate.numAttr);
		
		for (int i = 0; i < prob.separate.numAttr; i++) {
			attrPairList.add(new Pair(i, prob.separate.mapList.get(i).length));
		}
		
		/*** 熵数组 ***/
		for (int i = 0; i < prob.separate.numAttr; i++) {
			entropyList.add(getH(attrPairList.get(i)));
		}
		
		/*** 联合熵数组 ***/
		for (int i = 0; i < prob.separate.numAttr; i++) {
			entropyUnionMatrix.add(new ArrayList<Double>(prob.separate.numAttr));
		}
		for (int i = 0; i < prob.separate.numAttr; i++) {
			for (int j = 0; j < prob.separate.numAttr; j++) {
				entropyUnionMatrix.get(i).add(getUnionH(attrPairList.get(i), attrPairList.get(j)));
			}
		}
		
		/*** 条件熵数组 ***/
		for (int i = 0; i < prob.separate.numAttr; i++) {
			entropyConditionalMatrix.add(new ArrayList<Double>(prob.separate.numAttr));
		}
		for (int i = 0; i < prob.separate.numAttr; i++) {
			for (int j = 0; j < prob.separate.numAttr; j++) {
				entropyConditionalMatrix.get(i).add(getConditionalH(attrPairList.get(i), attrPairList.get(j)));
			}
		} 
	}
	
	private double H(double x) {
		return - x * Math.log(x);
	}
	
	private double H(double x, double y) {
		return - x * Math.log(y);
	}
	
	private double getH(Pair attr) {     // first为属性编号，second为该属性值的个数
		return getH(attr.first, attr.second);
	}
	
	private double getH(int attr, int interval_size) {
		double sum = 0;
		for (int i = 0; i < interval_size; i++) {
			double x = prob.getProbability(attr, i);
			sum += H(x);
		}
		return sum;
	}
	
	/*** 这个地方通用的方法不好写，暂时为两个变量的算法。对于互信息已经足够，对于条件互信息也只需要3个变量就够了 ***/
	
	private double getUnionH(Pair attr_1, Pair attr_2) {
		double sum = 0;
		for (int i = 0; i < attr_1.second; i++) {
			for (int j = 0; j < attr_2.second; j++) {
				sum += H(prob.getUnionProbability(new Pair(attr_1.first, i), new Pair(attr_2.first, j)));
			}
		}
		return sum;
	}
	
	private double getConditionalH(Pair attr, Pair conattr) {
		double sum = 0;
		for (int i = 0; i < attr.second; i++) {
			for (int j = 0; j < conattr.second; j++) {
				List<Pair> unionList = new ArrayList<Pair>(1);
				unionList.add(new Pair(attr.first, i));
				List<Pair> conList = new ArrayList<Pair>(1);
				conList.add(new Pair(conattr.first, j));
				sum += H(prob.getUnionProbability(attr, conattr),prob.getConditionalProbability(unionList, conList));
			}
		}
		return sum;
	}
	
	public static void main(String[] args) {
		double x = 2.71828;
		
		System.out.println(Math.log(x));
	}
	
}
