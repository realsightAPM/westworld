package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class NeticaBuildTest {

	@Test
	public void testNeticaBuild() throws Exception {
		NeticaBuild buildNet = new NeticaBuild();
		buildNet.build("inputjava_data1.csv");
	}

}
