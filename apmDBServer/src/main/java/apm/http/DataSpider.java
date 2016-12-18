package apm.http;

import java.util.HashMap;
import java.util.Map;

import apm.globalinfo.DataType;
import apm.globalinfo.HttpMethod;
import apm.globalinfo.Part;
import apm.globalinfo.Period;

public class DataSpider {
	private static final String URL = "http://121.42.185.24:8080/jeecms/monitoring";
	
	public void start(){
		Map<String,String> params = new HashMap<String,String>();
		params.put("part", Part.HEAPHISTORY.getName());
		params.put("period", Period.JOUR.getName());
		params.put("format", DataType.JSON.getName());
		
		DataParse parse = new DataParse();
		DataProducer dataProducer = new DataProducer(URL,HttpMethod.GET, params,parse);
		
		dataProducer.start();
		
	}
	
	
}
