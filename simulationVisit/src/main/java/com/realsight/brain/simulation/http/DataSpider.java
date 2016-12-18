package com.realsight.brain.simulation.http;

import java.util.HashMap;
import java.util.Map;

import com.realsight.brain.simulation.globalinfo.HttpMethod;
import com.realsight.brain.simulation.globalinfo.Part;
import com.realsight.brain.simulation.globalinfo.Period;
import com.realsight.brain.simulation.http.DataProducer;


public class DataSpider {
	private static final String URL = "http://121.42.185.24:8080/jeecms";
	
	public void start(){
		Map<String,String> params = new HashMap<String,String>();
		params.put("part", Part.HEAPHISTORY.getName());
		params.put("period", Period.JOUR.getName());
		
		DataProducer dataProducer = new DataProducer(URL, HttpMethod.GET, params);
		dataProducer.start();
	}
	
	
}
