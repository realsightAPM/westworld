package com.realsight.westworld.bnanalysis.api;

import java.util.Map;

import com.realsight.westworld.bnanalysis.service.NeticaApi;

public class GetRangeApi {
	
	public GetRangeApi() throws Exception {
	
	}
	
	
	public Map<String, String[]> getRange() throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		Map<String, String[]> res = netica.rangeMap;
		netica.finalize();
		return res;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		GetRangeApi get = new GetRangeApi();
		Map<String, String[]> map = get.getRange();
		
		for (String it : map.keySet()) {
			System.out.print(it + ": ");
			for (String it2 : map.get(it)) {
				System.out.print(it2 + "\t");
			}
		}
		
	}

}
