package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class BanjoApiTest {

	@Test
	public void test() throws Exception {
		BanjoApi banjo = new BanjoApi("inputjava_data1.csv", 3);
		/* default:
		 * BanjoApi banjo = new BanjoApi("read.csv", 2);
		 */
		
		System.out.println("Finish the test: if banjo build the bayesian network.");
	}

}
