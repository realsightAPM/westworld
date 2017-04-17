package com.bnAnalysis;

import java.util.List;

import com.application.RootCause;
import com.basic.Pair;
import com.basic.SimuLoad;

public class SimuRootCause {

	public SimuRootCause() {
		
	}
	
	public void run(String origin_csv, String simuStr, double times, String var) throws Exception {
//		String simuStr = "session_count";
//		double times = 20;"inputjava_data1.csv", simuStr, times, "http_times"
		
		SimuLoad simu = new SimuLoad(origin_csv, simuStr, times);   // 只是用来生成模拟的.tsv文件
		SimuCPT simuCPT = new SimuCPT();
		simuCPT.setSimuCPT(simuStr);
		NeticaApi netica = simuCPT.loadSimuCPT();
	    RootCause simuCause = new RootCause(var, netica);
	    
	    List<Pair<String, Double>> rank = simuCause.causeRank;
		System.out.println("\nsimu："+ simuStr + "*" + times + "后" + var + "期望：");
		for (int i = 0; i < rank.size(); i++) {
			System.out.println(rank.get(i).first + "\t" + rank.get(i).second);
		}
		netica.finalize();
	}
}
