package com.realsight.brain.timeseries.lib.model.htm.context;

import java.util.ArrayList;
import java.util.List;

public class CrossedContext {
	private final int MEMORY_MAX_SIZE = 100;
	private List<Long> memory;
	private int numActivate;
	private int zeroLevel;
	private int leftHash;
	private int rightHash;
	
	public CrossedContext(int numActivate, int zeroLevel, int leftHash, int rightHash) {
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
	public int getNumActivate() {
		return numActivate;
	}
	public void setNumActivate(int numActivate) {
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
