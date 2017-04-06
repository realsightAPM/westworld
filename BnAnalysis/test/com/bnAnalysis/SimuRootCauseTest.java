package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimuRootCauseTest {

	@Test
	public void testSimuRootCause() throws Exception {
		SimuRootCause root = new SimuRootCause();
		root.run("inputjava_data1.csv", "session_count", 10, "http_times");
	}
}
