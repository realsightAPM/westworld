package com.realsight.westworld.tsp.lib.model.htm.context;

import java.io.Serializable;

public class CrossedContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4985948833978382789L;
	
	private double TP;
	private double FP;
	private int zeroLevel;
	private int leftHash;
	private int rightHash;
	
	public CrossedContext(double TP, double FP, int zeroLevel, int leftHash, int rightHash) {
		this.TP = TP;
		this.FP = FP;
		this.zeroLevel = zeroLevel;
		this.leftHash = leftHash;
		this.rightHash = rightHash;
	}
	
	public double getNumActivate() {
		return TP / (TP+FP);
	}
	public void addNumActivate() {
		TP = TP+1;
	}
	public void subNumActivate() {
		FP = FP+1;
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
