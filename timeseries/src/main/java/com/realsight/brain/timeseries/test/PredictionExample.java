package com.realsight.brain.timeseries.test;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.realsight.brain.timeseries.api.PredictionAPI;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.util.Pair;
import com.realsight.brain.timeseries.lib.util.data.TimeseriesData;
import com.realsight.brain.timeseries.lib.util.data.VolumeData;
import com.realsight.brain.timeseries.lib.util.plot.Plot;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;

/**
 * @author Sun Muxin
 * 
 */ 
public class PredictionExample {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public void main() throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String dataPath = Paths.get(root, "data").toString();
		VolumeData ts = new VolumeData(dataPath+File.separator+"a.csv");
		DoubleSeries inSeries = ts.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
		Plot.plot("sb", inSeries);
		MultipleDoubleSeries nSeries = new MultipleDoubleSeries("ts", inSeries);
		PredictionAPI p = new PredictionAPI(nSeries);
		p.run(nSeries);
//		TimeseriesData in = new TimeseriesData(dataPath+File.separator+"sss.csv");
////		TimeseriesData out = new TimeseriesData(dataPath+File.separator+"OUT.csv");
//		DoubleSeries inSeries = in.getPropertyDoubleSeries("diweijiage");
////		DoubleSeries outSeries = out.getPropertySeries(String.valueOf(20), new SimpleDateFormat("yyyyMMddHH"));
//		MultipleDoubleSeries nSeries = new MultipleDoubleSeries("ts", inSeries);
//		Plot.plot("sb", inSeries);
//		System.out.println(inSeries.size());
//		PredictionAPI p = new PredictionAPI(nSeries);
//		List<Double> as = p.run(nSeries);
//		double sum = 0;
//		for(Double a : as) {
//			sum += a;
//		}
//		System.out.println((sum/as.size()));
//		double[] b = new double[10];
//		for (int i = 1; i <= 16*8; i++) {
//			DoubleSeries inSeries = in.getPropertyDoubleSeries(String.valueOf(i), new SimpleDateFormat("yyyyMMddHH"));
//			DoubleSeries outSeries = out.getPropertyDoubleSeries(String.valueOf(i), new SimpleDateFormat("yyyyMMddHH"));
//			MultipleDoubleSeries nSeries = new MultipleDoubleSeries("ts", inSeries, outSeries);
//			PredictionAPI p = new PredictionAPI(nSeries);
//			List<Double> a = p.run(nSeries);
//			double sum = 0.0;
//			for (int j = 0; j < b.length; j++){
//				b[j] += a.get(j);
//				sum += (b[j]/i);
//			}
//			System.out.println("{ " + i + " -> " + (sum/10) + " }");
//		}
	}
	
	public static void main(String[] args) throws Exception {
		new PredictionExample().main();
	}
}
