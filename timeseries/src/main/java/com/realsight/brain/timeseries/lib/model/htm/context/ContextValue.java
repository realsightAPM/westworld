package com.realsight.brain.timeseries.lib.model.htm.context;

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
	
	public ContextValue(int lenFact) {
		this.fromOrto = new ArrayList<Integer>();
		this.lenFact = lenFact;
		this.setContexIDs(new HashMap<Integer, Integer>());
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
