package com.realsight.westworld.bnanalysis.basic;

public class Discretization {

	public Discretization() {}
	
	public int run(double x, double... interval) {
		int pos = 0;
		for (; pos < interval.length && x > interval[pos]; pos++);
		return pos;
	}
	
}
