package com.realsight.westworld.engine.model;

public class Inferance {

	private String infer;
	
	public Inferance() {
		
	}

	public Inferance(String infer) {
//		super();
		this.infer = infer;
	}

	public String getInfer() {
		return infer;
	}

	public void setInfer(String infer) {
		this.infer = infer;
	}

	@Override
	public String toString() {
		return "Inferance [infer=" + infer + "]";
	}
	
	
}
