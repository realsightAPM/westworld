package com.realsight.westworld.tsp.lib.model.htm.context;

import java.io.Serializable;

public class ActiveContext implements Comparable<ActiveContext>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6531219945765919093L;
	private int contextID;
	private double numActivate;
	private int leftHash;
	private int rightHash;
	
	public ActiveContext(int contextID, double numActivate, int leftHash, int rightHash) {
		this.setContextID(contextID);
		this.numActivate = numActivate;
		this.leftHash = leftHash;
		this.rightHash = rightHash;
	}
	public double getNumActivate() {
		return numActivate;
	}
	public void setNumActivate(double numActivate) {
		this.numActivate = numActivate;
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
	public int getContextID() {
		return contextID;
	}
	public void setContextID(int contextID) {
		this.contextID = contextID;
	}
	
	@Override
	public int compareTo(ActiveContext o) {
		// TODO Auto-generated method stub
		if ( this.numActivate > ((ActiveContext) o).getNumActivate() )
			return -1;
		if ( this.numActivate < ((ActiveContext) o).getNumActivate() )
			return 1;
		return 0;
	}
}
