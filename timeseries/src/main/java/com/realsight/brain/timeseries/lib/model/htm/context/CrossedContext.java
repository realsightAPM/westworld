package com.realsight.brain.timeseries.lib.model.htm.context;

public class CrossedContext {
	private int numActivate;
	private int zeroLevel;
	private int leftHash;
	private int rightHash;
	
	public CrossedContext(int numActivate, int zeroLevel, int leftHash, int rightHash) {
		this.numActivate = numActivate;
		this.zeroLevel = zeroLevel;
		this.leftHash = leftHash;
		this.rightHash = rightHash;
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
}
