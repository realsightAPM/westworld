package com.realsight.brain.timeseries.test;


import java.io.File;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.api.OnlineAnormalyDetectionAPI;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.data.ArtificialData;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/**
 * @author Sun Muxin
 * 
 */ 
public class OnlineAnormalyExample2 {
	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static void main(String[] args) throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String localPath = Paths.get(root, "target", "data", "TS_a.csv").toString();
		ArtificialData td = new ArtificialData(localPath);
		DoubleSeries nSeries = td.getPropertySeries("value");
		nSeries.normly();
		double minValue = nSeries.min();
		double maxValue = nSeries.max();
		OnlineAnormalyDetectionAPI detection = new OnlineAnormalyDetectionAPI(minValue, maxValue);
		
		DoubleSeries anormalys = detection.detectorSeries(nSeries.subSeries(0,  20), 0.05);
		String resultDir = Util.writeCsv(new MultipleDoubleSeries(nSeries, anormalys)).toString();
		System.out.println("anormaly result dir is : " + resultDir);
		Plot.plot("Artificial Dataset TS_b", anormalys, nSeries);
	}
}
