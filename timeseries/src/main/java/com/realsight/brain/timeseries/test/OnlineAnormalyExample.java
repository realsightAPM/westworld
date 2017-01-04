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
public class OnlineAnormalyExample {
	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static void main(String[] args) throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String localPath = Paths.get(root, "target", "data", "test.csv").toString();
		TestData td = new TestData(localPath);
		DoubleSeries cpuSeries = td.getPropertySeries("cpu");
		DoubleSeries httpSeries = td.getPropertySeries("http_times");
		DoubleSeries sessionSeries = td.getPropertySeries("session_count");
		DoubleSeries threadSeries = td.getPropertySeries("thread_count");
		DoubleSeries memorySeries = td.getPropertySeries("used_memory");
		cpuSeries.normly();
		httpSeries.normly();
		sessionSeries.normly();
		threadSeries.normly();
		memorySeries.normly();
		Plot.plot("apm test", cpuSeries, sessionSeries, memorySeries);
//		double minValue = nSeries.min();
//		double maxValue = nSeries.max();
//		OnlineAnormalyDetectionAPI detection = new OnlineAnormalyDetectionAPI(minValue, maxValue);
//		DoubleSeries anormalys = detection.detectorSeries(nSeries.subSeries(0, 3000));
//		String resultDir = Util.writeCsv(new MultipleDoubleSeries(nSeries, anormalys)).toString();
//		System.out.println("anormaly result dir is : " + resultDir);
	}
}
