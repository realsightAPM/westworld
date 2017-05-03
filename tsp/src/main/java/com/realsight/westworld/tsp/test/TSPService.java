package com.realsight.westworld.tsp.test;

import java.io.File;
import java.nio.file.Paths;

import com.realsight.westworld.tsp.api.OnlineTimeseriesPredictionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries;
import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

import Jama.Matrix;

public class TSPService {

	public TSPService() {
		
	}
	
	public TSPService(String csv_file) throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String dataPath = Paths.get(root, "data").toString();
		TimeseriesData in = new TimeseriesData(dataPath+File.separator+"test.csv");
		MultipleDoubleSeries nSeries = in.getPropertyDoubleSeries().subSeries(300, 350);
//		MultipleDoubleSeries nSeries = new MultipleDoubleSeries("213", in.getPropertyDoubleSeries("cpu").subSeries(300, 350));//.subSeries(300, 350)
		Plot.plot(nSeries);
		OnlineTimeseriesPredictionAPI p = new OnlineTimeseriesPredictionAPI();
		DoubleSeries real = new DoubleSeries("real");
		DoubleSeries pre = new DoubleSeries("pre");
		for (int iter = 0; iter < 20; iter ++) {
			System.err.println("epoch is " + iter);
			p.train(nSeries);
		}
		for ( int i = 0; i < nSeries.size(); i++ ) {
			Matrix value = Util.toVec(nSeries.get(i).getItem().iterator());
			long timestamp = nSeries.get(i).getInstant();
			if (i > nSeries.size()/2){
				Matrix t_value = p.prediction();
//				System.out.println(t_value);
				if (t_value != null) {
					System.out.println("");
					System.out.print("{ " + t_value.get(0, 0) + " & " + value.get(0, 0) + "}");
					real.add(new TimeSeries.Entry<Double>(value.get(4, 0), timestamp));
					pre.add(new TimeSeries.Entry<Double>(t_value.get(4, 0), timestamp));
					continue;
				}
			}
			p.todayValue(value, timestamp);
		}
		Plot.plot("222", real, pre);
	}
}
