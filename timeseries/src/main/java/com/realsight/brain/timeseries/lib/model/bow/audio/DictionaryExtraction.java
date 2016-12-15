package com.realsight.brain.timeseries.lib.model.bow.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;

public class DictionaryExtraction {
	private static final int stepLen = 1;
	private static final int bitNum = 1 << 5;
	private static final int wordTime = 100;
//	private static final int pyramid = 100;
	private static final int wordNum = 32;
	private static final int n = 30000;
	private static final Random rng = new Random(2234);
	private final double maxValue;
	private final double minValue;
	private final double stepValue;
	private List<Word> dictionary = null;
	private Word[] center = null;
	private int[] category = null;
	
	private int getCategory(Word word){
		int res = 0;
		double t_min_dis = Double.MAX_VALUE;
		for(int c = 0; c < wordNum; c++){
			double dis = this.center[c].distance(word);
			if(dis < t_min_dis){
				t_min_dis = dis;
				res = c;
			}
		}
		return res;
	}
	
	private List<Integer> getBit(DoubleSeries series) {
		List<Integer> bits = new ArrayList<Integer>();
		for ( int k = 0; k < series.size(); k++) {
			double value = series.get(k).getItem();
			int bit = (int) ((value-this.minValue)/this.stepValue);
			if(bit < 0) bit = 0;
			if(bit > bitNum) bit = bitNum;
			bits.add(bit);
		}
		return bits;
	}
	
	public double[] getFeature(DoubleSeries series){
		double[] feature = new double[wordNum];
		for ( int j = 0; j < series.size(); j += stepLen){
			List<Integer> bits = getBit(series.subSeries(j, j+wordTime));
			int cate = getCategory(new Word(bits, bitNum));
			feature[cate] += 1.0;
		}
//		for ( int i = 0, l = 0, k = 0; i < pyramid; i+=1, k += wordNum ) {
//			int r = l + series.size()/pyramid;
//			while(l < r) {
//				List<Integer> bits = getBit(series.subSeries(l, l+wordTime));
//				int cate = getCategory(new Word(bits, bitNum));
//				feature[cate+k] += 1.0;
//				l += stepLen;
//			}
//		}
//		for(int k = 0; k <= pyramid; k += 1) {
//			double sumSq = 0.1;
//			for(int i = 0; i < wordNum; i++)
//				sumSq += feature[k*wordNum+i]*feature[k*wordNum+i];
//			sumSq = Math.sqrt(sumSq);
//			for(int i = 0; i < wordNum; i++)
//				feature[k*wordNum+i] /= sumSq;
//		}
		return feature;
	}
	
	private void kMeans() {
		this.center = new Word[wordNum];
		this.category = new int[dictionary.size()];
		for(int i = 0; i < wordNum; i++){
			int r = rng.nextInt(dictionary.size());
			this.center[i] = this.dictionary.get(r);
		}
		for(int iter = 0; iter < 50; iter++){
			Map<Integer, List<Integer>> t = new HashMap<Integer, List<Integer>>();
			for(int i = 0; i < wordNum; i++)
				t.put(i, new ArrayList<Integer>());
			for(int i = 0; i < dictionary.size(); i++) {
				category[i] = 0;
				for (int j = 0; j < wordNum; j++) {
					if(center[j].distance(this.dictionary.get(i)) < 
							center[category[i]].distance(this.dictionary.get(i))) {
						category[i] = j;
					}
				}
				t.get(category[i]).add(i);
//				System.out.print(i + "," + category[i] + " = " + this.center[category[i]].distance(this.dictionary.get(i)) + "\n" );
			}
			
			for(int c = 0; c < wordNum; c++) {
				double[] bit = new double[bitNum+1];
				for(int j = 0; j < bitNum+1; j++)
					bit[j] += 0.1;
				for(int i = 0; i < t.get(c).size(); i++) {
					for(int j = 0; j < bitNum+1; j++)
						bit[j] += this.dictionary.get(t.get(c).get(i)).get(j);
				}
				this.center[c] = new Word(bit);
			}
		}
		System.out.println("kmeans end.");
	}
	
	public DictionaryExtraction(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.dictionary = new ArrayList<Word>();
		this.stepValue = (this.maxValue-this.minValue) / bitNum;
		System.out.println("minValue = " + minValue + ", maxValue = " + maxValue);
	}
	
	public void addSeries(DoubleSeries series) {
//		System.out.println(series.max() + " " + series.min());
		for ( int j = 0; j < series.size(); j += stepLen){
			List<Integer> word = new ArrayList<Integer>();
			for ( int k = 0; k<wordTime && k+j<series.size(); k++) {
				double value = series.get(j+k).getItem();
				int bit = (int) ((value-this.minValue)/this.stepValue);
				if(bit < 0) bit = 0;
				if(bit > bitNum) bit = bitNum;
				word.add(bit);
			}
			this.dictionary.add(new Word(word, bitNum));
		}
	}
	
	public void run() {
		if(dictionary.size() > n) {
			Collections.shuffle(this.dictionary);
			this.dictionary = this.dictionary.subList(0, n);
		}
		System.out.println("dictionary size is " + dictionary.size());
		kMeans();
	}
}
