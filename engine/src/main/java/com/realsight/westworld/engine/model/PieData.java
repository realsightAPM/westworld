package com.realsight.westworld.engine.model;

public class PieData {

	private double Value;
	private String name;
	
	public PieData() {
		
	}

	public PieData(double value, String name) {
//		super();
		Value = value;
		this.name = name;
	}

	public double getValue() {
		return Value;
	}

	public void setValue(double value) {
		Value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "PieData [Value=" + Value + ", name=" + name + "]";
	}
	
	
}
