package com.realsight.brain.timeseries.lib.util;

public class Pair <T,S> implements Comparable<Pair<Double, String>>{
	private T a;
	private S b;
	
	public Pair(T a, S b){
		this.a = a;
		this.b = b;
	}
	
	public T getA() {
		return a;
	}
	public void setA(T a) {
		this.a = a;
	}
	public S getB() {
		return b;
	}
	public void setB(S b) {
		this.b = b;
	}

	@Override
	public int compareTo(Pair<Double, String> o) {
		// TODO Auto-generated method stub
		if((Double) this.a < o.a){
			return -1;
		}
		if((Double) this.a > o.a){
			return 1;
		}
		return 0;
	}
	
	@Override
	public int hashCode() {
		return a.hashCode() ^ (b.hashCode()>>1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this == obj) return true;
		if(obj instanceof Pair){
			Pair<?, ?> pair =(Pair<?, ?>)obj;
			if(pair.a.equals(this.a) && pair.b.equals(this.b)) return true;  
		}
		return false;  
	}
}
