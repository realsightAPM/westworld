package com.application;

import static org.junit.Assert.*;

import org.junit.Test;

import com.realsight.westworld.bnanalysis.api.NeticaBuild;

public class NeticaBuildTest {

	@Test
	public void testNeticaBuild() throws Exception {
		NeticaBuild buildNet = new NeticaBuild();
//		buildNet.build("inputjava_data1.csv");
		buildNet.build("log.csv");
	}

}
