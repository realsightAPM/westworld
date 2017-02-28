package Algorithm;

import java.util.ArrayList;
import java.util.List;

import Initialization.Pair;

public class Entropy {

	public Probability prob;
	
	public Entropy() throws Exception{
		prob = new Probability("read.csv");
	}
	
	public Entropy(String original_csv) throws Exception {
		prob = new Probability(original_csv);
	}
	
	double H(double x) {
		return - x * Math.log(x);
	}
	
	double H(double x, double y) {
		return - x * Math.log(y);
	}
	
	public double getH(Pair attr) {     // first为属性编号，second为该属性值的个数
		double sum = 0;
		for (int i = 0; i < attr.second; i++) {
			double x = prob.getProbability(new Pair(attr.first, i));
			sum += H(x);
		}
		return sum;
	}
	
	/*** 这个地方通用的方法不好写，暂时为两个变量的算法 ***/
	
	public double getUnionH(Pair attr1, Pair attr2) {
		double sum = 0;
		List<Pair> unionList = new ArrayList<Pair>();
		for (int i = 0; i < attr1.second; i++) {
			for (int j = 0; j < attr2.second; j++) {
				List<Pair> tmpPair = new ArrayList<Pair>();
				tmpPair.add(new Pair(attr1.first, i));
				tmpPair.add(new Pair(attr2.first, j));
				sum += H(prob.getUnionProbability(tmpPair));
			}
		}
		return sum;
	}
	
	public double getConditionalH(Pair attr1, Pair conattr2) {
		double sum = 0;
		for (int i = 0; i < attr1.second; i++) {
			for (int j = 0; j < conattr2.second; j++) {
				List<Pair> unionList = new ArrayList<Pair>();
				unionList.add(new Pair(attr1.first, i));
				List<Pair> conList = new ArrayList<Pair>();
				conList.add(new Pair(conattr2.first, j));
				sum += H(prob.getConditionalProbability(unionList, conList));
			}
		}
		return sum;
	}
	
	public static void main(String[] args) {
		double x = 2.71828;
		
		System.out.println(Math.log(x));
	}
	
}
