package com.realsight.westworld.tsp.test;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

import com.realsight.westworld.tsp.api.OnlineTimeseriesPredictionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.data.VolumeData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

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
		VolumeData a = new VolumeData(dataPath+File.separator+"volume_20min_1_0.csv");
		VolumeData b = new VolumeData(dataPath+File.separator+"volume_20min_1_1.csv");
		VolumeData c = new VolumeData(dataPath+File.separator+"volume_20min_2_0.csv");
		VolumeData d = new VolumeData(dataPath+File.separator+"volume_20min_3_0.csv");
		VolumeData e = new VolumeData(dataPath+File.separator+"volume_20min_3_1.csv");
		DoubleSeries aSeries = a.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
		DoubleSeries bSeries = b.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
		DoubleSeries cSeries = c.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
		DoubleSeries dSeries = d.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
		DoubleSeries eSeries = e.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
		MultipleDoubleSeries mSeries = new MultipleDoubleSeries("ts", 
				aSeries, bSeries); // , cSeries, dSeries, eSeries
		Plot.plot(mSeries);
		OnlineTimeseriesPredictionAPI p = new OnlineTimeseriesPredictionAPI();
		p.run(mSeries);
//		TimeseriesData in = new TimeseriesData(dataPath+File.separator+"IN.csv");
//		TimeseriesData out = new TimeseriesData(dataPath+File.separator+"OUT.csv");
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
//		double ss = 0;
//		int[] I = new int[]{12, 14, 20, 22, 27, 29, 34, 45, 50, 51, 52, 53, 58, 59, 60, 68, 69, 73, 81, 89};
////		for (int i = 1; i <= 16*8; i++) {
//		for (int i : I){
//			DoubleSeries inSeries = in.getPropertyDoubleSeries(String.valueOf(i), new SimpleDateFormat("yyyyMMddHH"));
//			DoubleSeries outSeries = out.getPropertyDoubleSeries(String.valueOf(i), new SimpleDateFormat("yyyyMMddHH"));
//			MultipleDoubleSeries nSeries = new MultipleDoubleSeries("ts", inSeries, outSeries);
//			OnlineTimeseriesPredictionAPI p = new OnlineTimeseriesPredictionAPI();
//			List<Double> a = p.run(nSeries);
//			double sum = 0.0;
//			System.out.print(a.size());
////			System.out.println("");
//			for (int j = 0; j < a.size(); j++){
//				sum += (a.get(j));
////				System.out.println(a.get(j));
//			}
//			System.out.println("{ " + i + " -> " + (sum/20) + " }");
//			ss += sum/20;
//		}
//		ss /= I.length;
//		System.out.println(Math.sqrt(ss));
	}
	
	public static void main(String[] args) throws Exception {
		new PredictionExample().main();
	}
}
