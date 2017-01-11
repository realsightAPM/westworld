package com.realsight.brain.timeseries.test;


import java.io.File;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.api.OnlineAnormalyDetectionAPI;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.data.TestData;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/**
 * @author Sun Muxin
 * 
 */ 
public class OnlineAnormalyExample1 {
	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static void main(String[] args) throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String localPath = Paths.get(root, "target", "data", "1_9_test_data.csv").toString();
		TestData td = new TestData(localPath);
		DoubleSeries cpuSeries = td.getPropertySeries("cpu");
		DoubleSeries httpSeries = td.getPropertySeries("http_times");
		DoubleSeries sessionSeries = td.getPropertySeries("session_count");
		DoubleSeries threadSeries = td.getPropertySeries("thread_count");
		DoubleSeries memorySeries = td.getPropertySeries("used_memory");
		DoubleSeries clientSeries = td.getPropertySeries("client");
		System.out.println(httpSeries.min()+"\t"+memorySeries.max()+"\t"+memorySeries.mean()+"\t"+
				memorySeries.variance());
		System.out.println(clientSeries);
		clientSeries.normly();
		cpuSeries.normly();
		httpSeries.normly();
		sessionSeries.normly();
		threadSeries.normly();
		memorySeries.normly();
		MultipleDoubleSeries tmp = new MultipleDoubleSeries(cpuSeries, httpSeries, sessionSeries, threadSeries, memorySeries);
		DoubleSeries series = cpuSeries;
		double minValue = series.min();
		double maxValue = series.max();
		OnlineAnormalyDetectionAPI detection = new OnlineAnormalyDetectionAPI(minValue, maxValue);
		DoubleSeries anormalys = detection.detectorSeries(series, 0.85);
		Plot.plot("anormalys and cpu", anormalys, clientSeries, series);
		String resultDir = Util.writeCsv(tmp).toString();
		System.out.println("anormaly result dir is : " + resultDir);
	}
}
