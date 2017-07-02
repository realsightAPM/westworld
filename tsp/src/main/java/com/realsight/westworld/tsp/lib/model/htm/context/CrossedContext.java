package com.realsight.westworld.tsp.lib.model.htm.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossedContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4985948833978382789L;
	
	private double TP;
	private double FP;
	private int zeroLevel;
	private int level;
	private int leftHash;
	private int rightHash;
	private List<Integer> rightFacts;
	private Map<String, Double> rewards;
	
	public CrossedContext(double TP, 
			double FP, 
			int zeroLevel,
			int level,
			int leftHash, 
			int rightHash, 
			List<Integer> rightFacts,
			Map<String, Double> rewards) {
		this.TP = TP;
		this.FP = FP;
		this.zeroLevel = zeroLevel;
		this.level = level;
		this.leftHash = leftHash;
		this.rightHash = rightHash;
		this.rightFacts = rightFacts;
		this.rewards = rewards;
	}
	
	public CrossedContext(double TP, double FP, int zeroLevel, int leftHash, int rightHash) {
		this(TP, FP, zeroLevel, 0, leftHash,rightHash, new ArrayList<Integer>(), new HashMap<String, Double>());
	}
	
	public List<Integer> getRightFacts() {
		return this.rightFacts;
	}
	public double getNumActivate() {
		return TP/(TP+FP);
	}
	public double getTP() {
		return TP;//Math.pow(2, rightSize-1);
	}
	public double getFP() {
		return FP;//Math.pow(2, rightSize-1);
	}
	public void addNumActivate() {
		TP = TP+1.0;
	}
	public void subNumActivate() {
		FP = FP+1.0;
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
	public void setRightFacts(List<Integer> rightFacts) {
		this.rightFacts = rightFacts;
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
	public Map<String, Double> getRewards() {
		return this.rewards;
	}
	public void setRewards(Map<String, Double> rewards) {
		this.rewards = rewards;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
