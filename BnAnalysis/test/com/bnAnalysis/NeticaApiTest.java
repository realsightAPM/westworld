package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class NeticaApiTest {

	@Test
	public void testNeticaApi() throws Exception {
		NeticaApi netica = new NeticaApi();
		/*
		 * default:
		 * NeticaApi netica = new NeticaApi("read.csv");
		 */
		
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
		
		System.out.println("\nFinish the test: if NeticaApi build Learned_netica.dne");
	}

}
