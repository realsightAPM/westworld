package com.realsight.brain.simulation.cache;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.realsight.brain.simulation.globalinfo.Part;

public class Cache {
	private final static HashMap<String,ConcurrentLinkedQueue<Object>> cache;
	static{
		Part[] parts = Part.values();
		cache = new HashMap<String,ConcurrentLinkedQueue<Object>>();
		for(Part part:parts){
			cache.put(part.getName(), new ConcurrentLinkedQueue<Object>());
		}
	}
	
	public static boolean add(String part,Object object){
		ConcurrentLinkedQueue<Object> list = cache.get(part);
		return list.add(object);
	}
}
