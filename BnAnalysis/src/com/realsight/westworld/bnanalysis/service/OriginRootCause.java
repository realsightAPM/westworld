package com.realsight.westworld.bnanalysis.service;

import java.util.List;

import com.realsight.westworld.bnanalysis.api.RootCause;
import com.realsight.westworld.bnanalysis.basic.Pair;

public class OriginRootCause {

	public OriginRootCause() {
		
	}
	
	public List<Pair<String, Double>> run(String var) throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
	    RootCause rootCause = new RootCause(var, netica);
	    
		List<Pair<String, Double>> rank = rootCause.causeRank;
		System.out.println("\nhttp_timesÆÚÍû£º");
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		
		netica.finalize();
		
		return rank;
	}
}
