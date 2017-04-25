package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class OriginRootCauseTest {

//	@Test
	public void testOriginRootCause() throws Exception {
		OriginRootCause root = new OriginRootCause();
		root.run("http_times");
	}

}
