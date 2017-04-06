package com.application;

import com.bnAnalysis.NeticaApi;

public class NeticaBuild {

	public NeticaBuild() {
		
	}
	
	public void build(String original_csv) throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.buildNet(original_csv, 2, 3);
		netica.finalize();
	}
	
}
