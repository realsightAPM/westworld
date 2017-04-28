package com.application;

import static org.junit.Assert.*;

import org.junit.Test;

import com.realsight.westworld.bnanalysis.app.NeticaBuild;

public class NeticaBuildTest {

//	@Test
	public void testNeticaBuild() throws Exception {
		NeticaBuild buildNet = new NeticaBuild();
		buildNet.build("inputjava_data1.csv");
	}

}
