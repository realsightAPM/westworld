package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class NeticaApiTest {

	@Test
	public void testNeticaApi() throws Exception {
		
		/*** VM arguments: ***/
		/*
		 * -ea
		 * -Djava.library.path=C:/NeticaJ_504/bin
		 */
		
		/*** Environment ***/
		/*
		 * PATH
		 * C:/NeticaJ_504/bin;%PATH%
		 */
		
		/*** begin: build netica ***/
//		NeticaApi netica = new NeticaApi("inputjava_data1.csv", 2);
		/*** end: build netica ***/
		
		/*** begin: load netica ***/
		NeticaApi netica = new NeticaApi();
		/*** end: load netica ***/
		
		netica.printRangeMap();
		
		for (String it : netica.rangeMap.keySet()) {
			netica.printChildren(it);
		}
		
		System.out.println("\nFinish the test: if NeticaApi build Learned_netica.dne");
	}

}
