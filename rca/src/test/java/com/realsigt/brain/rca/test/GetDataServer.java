package com.realsigt.brain.rca.test;

import org.restlet.resource.ResourceException;

import com.realsight.brain.rca.test.data.*;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GetDataServer {
	public static void main(String[] args) throws ResourceException, IOException {
		ScheduledExecutorService scheduledLogThreadPool = Executors.newScheduledThreadPool(5);
		ScheduledExecutorService scheduledCpuThreadPool = Executors.newScheduledThreadPool(5);
		ScheduledExecutorService scheduledMemThreadPool = Executors.newScheduledThreadPool(5);
		scheduledLogThreadPool.scheduleAtFixedRate( new LogData(), 0, 10, TimeUnit.MINUTES);
		scheduledCpuThreadPool.scheduleAtFixedRate( new CpuData(), 1, 5, TimeUnit.MINUTES);
		scheduledMemThreadPool.scheduleAtFixedRate( new MemData(), 2, 5, TimeUnit.MINUTES);
	}
}
