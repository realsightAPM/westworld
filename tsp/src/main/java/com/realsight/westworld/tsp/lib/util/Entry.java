package com.realsight.westworld.tsp.lib.util;

import java.io.Serializable;

public class Entry <F, S> implements Comparable<Entry<F, S>>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6790701026148285960L;
	
	private F first;
	private S second;
	
	public Entry(F first, S second){
		this.first = first;
		this.second = second;
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

	public int compareTo(Entry<F, S> o) {
		// TODO Auto-generated method stub
		if (o.getFirst().getClass().equals(Double.class)) {
			return ((Double) this.getFirst()).compareTo((Double) o.getFirst());
		}
		return 0;
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() ^ (second.hashCode()>>1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this == obj) return true;
		if(obj instanceof Entry){
			Entry<?, ?> entry =(Entry<?, ?>)obj;
			if(entry.first.equals(this.first) && entry.second.equals(this.second)) return true;  
		}
		return false;  
	}
	
	@Override
	public String toString() {
		String str = "first->" + first.toString() + ", second->" + second.toString();
		return str;
	}

}
