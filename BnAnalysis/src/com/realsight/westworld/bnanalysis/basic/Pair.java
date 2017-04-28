package com.realsight.westworld.bnanalysis.basic;

public class Pair<A, B> {

	public A first;
	public B second;
	
	public Pair() {
		first = null;
		second = null;
	}
	
	public Pair(A x, B y) {
		first = x;
		second = y;
	}
}
