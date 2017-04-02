package com.realsight.westworld.tsp.lib.model.htm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CrossedContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4985948833978382789L;
	
	private final int MEMORY_MAX_SIZE = 0;
	private List<Long> memory;
	private double numActivate;
	private int zeroLevel;
	private int leftHash;
	private int rightHash;
	
	public CrossedContext(double numActivate, int zeroLevel, int leftHash, int rightHash) {
		this.memory = new ArrayList<Long>();
		this.numActivate = numActivate;
		this.zeroLevel = zeroLevel;
		this.leftHash = leftHash;
		this.rightHash = rightHash;
	}
	

	public List<Long> getMemory() {
		return memory;
	}
	public void setMemory(List<Long> memory) {
		this.memory = memory;
	}
	public double getNumActivate() {
		return numActivate;
	}
	public void setNumActivate(double numActivate) {
		this.numActivate = numActivate;
	}
	public int getZeroLevel() {
		return zeroLevel;
	}
	public void setZeroLevel(int zeroLevel) {
		this.zeroLevel = zeroLevel;
	}
	public int getLeftHash() {
		return leftHash;
	}
	public void setLeftHash(int leftHash) {
		this.leftHash = leftHash;
	}
	public int getRightHash() {
		return rightHash;
	}
	public void setRightHash(int rightHash) {
		this.rightHash = rightHash;
	}
	
	public void beActivated(Long timestamp) {
		this.memory.add(timestamp);
		if ( this.memory.size() > this.MEMORY_MAX_SIZE ) {
			this.memory.remove(0);
		}
	}
}
