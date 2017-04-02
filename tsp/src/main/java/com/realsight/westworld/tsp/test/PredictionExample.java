package com.realsight.westworld.tsp.test;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import com.realsight.westworld.tsp.api.PredictionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.util.data.VolumeData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;

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
		System.out.println("Hello");
		String root = new File(System.getProperty("user.dir")).getPath();
		String dataPath = Paths.get(root, "data").toString();
		VolumeData ts = new VolumeData("/Users/jiajia/Develop/data/a.csv");
		DoubleSeries inSeries = ts.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
		//Plot.plot("sb", inSeries);
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
