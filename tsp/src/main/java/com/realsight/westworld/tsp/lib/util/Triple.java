package com.realsight.westworld.tsp.lib.util;

import java.io.Serializable;

public class Triple <F, S, T> implements Comparable<Triple<F, S, T>>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6790701026148285960L;
	
	private F first;
	private S second;
	private T third;
	
	public Triple(F first, S second, T third){
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public F getFirst() {
		return this.first;
	}
	
	public void setFirst(F first) {
		this.first = first;
	}
	
	public S getSecond() {
		return this.second;
	}
	
	public void setSecond(S second) {
		this.second = second;
	}
	
	public T getThird() {
		return this.third;
	}

	public void setThird(T third) {
		this.third = third;
	}
	
	public int compareTo(Triple<F, S, T> o) {
		// TODO Auto-generated method stub
		if (o.getFirst().getClass().equals(Double.class)) {
			return ((Double) this.getFirst()).compareTo((Double) o.getFirst());
		}
		return 0;
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() ^ (second.hashCode()>>1) ^ (third.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this == obj) return true;
		if(obj instanceof Triple){
			Triple<?, ?, ?> triple =(Triple<?, ?, ?>)obj;
			if(triple.first.equals(this.first) 
					&& triple.second.equals(this.second)
					&& triple.third.equals(this.third)) return true;  
		}
		return false;  
	}
	
	@Override
	public String toString() {
		String str = "first->" + first.toString() + ", second->" + second.toString();
		return str;
	}

}
