package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

import com.realsight.westworld.bnanalysis.service.NeticaApi;

public class NeticaApiTest {

//	@Test
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
		
		NeticaApi netica = new NeticaApi();
		
		/*** begin: build netica. args: input_file, num_threads, num_bins ***/
		netica.buildNet("inputjava_data1.csv", 2, 3);
		/*** end: build netica ***/
		
		/*** begin: load netica. the dne file must have been built ***/
//		netica.loadNet();
		/*** end: load netica ***/
		
		netica.printRangeMap();
		
//		for (String it : netica.rangeMap.keySet()) {
//			netica.printChildren(it);
//		}
//		
//		double res = netica.getInfer("session_count:c,cpu:c", "http_times:c");
//		System.out.println("\n条件概率为：" + res);
//		
//		res = netica.getInfer("cpu:c", "http_times:c");
//		System.out.println("\n条件概率为：" + res);
		
		System.out.println("\nFinish the test: if NeticaApi build Learned_netica.dne");
	}
}
