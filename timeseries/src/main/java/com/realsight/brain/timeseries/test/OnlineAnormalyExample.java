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
		System.out.println(cpuSeries.min()+"\t"+cpuSeries.max()+"\t"+cpuSeries.mean()+"\t"+
				cpuSeries.variance());
		cpuSeries.normly();
		httpSeries.normly();
		sessionSeries.normly();
		threadSeries.normly();
		memorySeries.normly();
		Plot.plot("apm test", cpuSeries, sessionSeries, memorySeries, httpSeries, threadSeries);
		DoubleSeries series = threadSeries;
		double minValue = series.min();
		double maxValue = series.max();
		OnlineAnormalyDetectionAPI detection = new OnlineAnormalyDetectionAPI(minValue, maxValue);
		DoubleSeries anormalys = detection.detectorSeries(series);
		Plot.plot("apm test", anormalys, httpSeries, series);
		String resultDir = Util.writeCsv(new MultipleDoubleSeries(series, anormalys)).toString();
		System.out.println("anormaly result dir is : " + resultDir);
	}
}
