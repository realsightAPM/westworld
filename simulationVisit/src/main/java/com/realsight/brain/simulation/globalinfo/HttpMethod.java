package com.realsight.brain.simulation.globalinfo;

public enum HttpMethod implements EnumInfo{
	GET("get"),
	POST("post");
	
	private final String name; 
	HttpMethod(String name){
		this.name = name;
	}
	public String getName() {	
		return name;
	}
	
}
