package com.realsight.westworld.tsp.lib.model.htm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextValue implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6675500572833028313L;
	private List<Integer> fromOrto;
	private int lenFact;
	private Map<Integer, Integer> contexIDs;
	private List<Integer> facts;
	
	public ContextValue(int lenFact) {
		this.fromOrto = new ArrayList<Integer>();
		this.lenFact = lenFact;
		this.setContexIDs(new HashMap<Integer, Integer>());
		this.facts = new ArrayList<Integer>();
	}
	
	public List<Integer> getFacts() {
		return facts;
	}
	public List<Integer> getFromOrto() {
		return fromOrto;
	}
	public void setFromOrto(List<Integer> fromOrto) {
		this.fromOrto = fromOrto;
	}
	public int getLenFact() {
		return lenFact;
	}
	public void setFacts(List<Integer> facts) {
		this.facts = facts;
	}
	public void setLenFact(int lenFact) {
		this.lenFact = lenFact;
	}
	public Map<Integer, Integer> getContexIDs() {
		return contexIDs;
	}
	public void setContexIDs(Map<Integer, Integer> contexIDs) {
		this.contexIDs = contexIDs;
	}
	
}
