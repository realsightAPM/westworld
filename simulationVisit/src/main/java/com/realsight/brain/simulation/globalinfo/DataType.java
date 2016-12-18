package com.realsight.brain.simulation.globalinfo;

public enum DataType implements EnumInfo{
	JSON("json"),
	PDF("pdf"),
	XML("xml");
	
	
	private final String name;
	
	DataType(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
