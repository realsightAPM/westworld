package com.algorithm;

import static org.junit.Assert.*;

import org.junit.Test;

public class InformationTest {

//	@Test
	public void testInfo() throws Exception {
		Information info = new Information();
		/* Default:
		 * Information info = new Information("read.csv");
		 */
		
		info.writeCSV();
		
		System.out.println("\nFinish the test: if create the infoMatrix.csv");
	}

}
