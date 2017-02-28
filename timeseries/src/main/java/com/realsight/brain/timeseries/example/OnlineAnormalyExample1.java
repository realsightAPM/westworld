package com.realsight.brain.timeseries.example;


import java.io.File;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.api.OnlineAnormalyDetectionAPI;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.data.TimeseriesData;

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
		String localPath = Paths.get(root, "target", "data", 
				"315.1c701d3e20125b8909c8bc40aa4cc1e0.ActiveThreadsNum.train").toString();
		TimeseriesData td = new TimeseriesData(localPath);
		DoubleSeries nSeries = td.getPropertySeries("value");
		double minValue = nSeries.min();
		double maxValue = nSeries.max();
		OnlineAnormalyDetectionAPI detection = new OnlineAnormalyDetectionAPI(minValue, maxValue);
		DoubleSeries anormalys = detection.detectorSeries(nSeries.subSeries(0, 3000));
		String resultDir = Util.writeCsv(new MultipleDoubleSeries(nSeries, anormalys)).toString();
		System.out.println("anormaly result dir is : " + resultDir);
	}
}
