package com.realsight.westworld.tsp.api;

import java.util.HashMap;
import java.util.Map;

import com.realsight.westworld.tsp.lib.model.LoggerAnalysis;

public class OnlineLoggerAnalysis extends LoggerAnalysis{
	
	private static final Map<String, OnlineLoggerAnalysis> instances = 
			new HashMap<String, OnlineLoggerAnalysis>();
	
	private OnlineLoggerAnalysis() {
	}

	private static synchronized void syncInit(String name) {
		if (instances.containsKey(name)) return ;
		instances.put(name, new OnlineLoggerAnalysis());
	}

	public static OnlineLoggerAnalysis getInstance(String name) {
		syncInit(name);
		if (instances.containsKey(name))
			return instances.get(name);
		return null;
	}
	
	public static void main(String[] args){
		OnlineLoggerAnalysis.getInstance("213").insert("s", ".", "b", 1L);
		System.out.println(OnlineLoggerAnalysis.getInstance("213").getRank(0).size());
	}
}
