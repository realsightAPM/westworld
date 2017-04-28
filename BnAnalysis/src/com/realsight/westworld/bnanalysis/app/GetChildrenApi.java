package com.realsight.westworld.bnanalysis.app;

import java.util.List;

import com.realsight.westworld.bnanalysis.service.NeticaApi;

public class GetChildrenApi {

	public GetChildrenApi() {
		
	}
	
	public List<String> getChildren(String var) throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		List<String> res = netica.getChildren(var);
		netica.finalize();
		return res;
	}
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		GetChildrenApi get = new GetChildrenApi();
		List<String> res = get.getChildren("session_count");
		for (String it : res) {
			System.out.println(it);
		}
	}

}
