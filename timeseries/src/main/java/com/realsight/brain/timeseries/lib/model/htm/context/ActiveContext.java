package com.realsight.brain.timeseries.lib.model.htm.context;

public class ActiveContext implements Comparable<ActiveContext> {
	private int contextID;
	private int numActivate;
	private int leftHash;
	private int rightHash;
	
	public ActiveContext(int contextID, int numActivate, int leftHash, int rightHash) {
		this.setContextID(contextID);
		this.numActivate = numActivate;
		this.leftHash = leftHash;
		this.rightHash = rightHash;
	}
	public int getNumActivate() {
		return numActivate;
	}
	public void setNumActivate(int numActivate) {
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
