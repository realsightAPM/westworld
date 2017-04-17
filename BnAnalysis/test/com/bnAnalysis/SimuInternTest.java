package com.bnAnalysis;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimuInternTest {

	@Test
	public void testSimuIntern() throws Exception {
		float[] hood = {0f, 0f, 1f};
		SimuIntern simu = new SimuIntern();
		simu.run("session_count", "http_times", hood);
	}
}
