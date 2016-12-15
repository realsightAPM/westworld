package com.realsight.brain.timeseries.lib.model.bow.audio;

import java.util.List;

public class Word {
	private final int maxBit;
	private final double[] bit;
	private final double sum;
	public Word(List<Integer> word, int maxBit) {
		this.maxBit = maxBit;
		bit = new double[maxBit+1];
		for ( Integer value : word ) {
			this.bit[value] += 1.0;
		}
		double sum = 0.0;
		for ( int i = 0; i < this.bit.length; i++ ) {
			this.bit[i] += 0.1;
			sum += this.bit[i];
		}
		this.sum = sum;
	}
	
	public Word( double[] bit ) {
		this.maxBit = bit.length;
		this.bit = bit;
		double sum = 0.0;
		for ( int i = 0; i < this.bit.length; i++ ) {
			sum += this.bit[i];
		}
		this.sum = sum;
	}
	
	public double get(int index) {
		if(index >= this.bit.length)
			return 0.0;
		return this.bit[index];
	}
	
	public double getSum() {
		return this.sum;
	}
	
	public int size() {
		return this.maxBit;
	}
	
	public double distance(Word otherWord) {
		double res = 0.0;
		for(int i = 0; i < this.bit.length; i++) {
			res += otherWord.get(i)*Math.log(otherWord.get(i)/otherWord.getSum());
			res -= otherWord.get(i)*Math.log(this.get(i)/this.getSum());
		}
		return res;
	}
}
