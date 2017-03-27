package com.realsight.brain.timeseries.test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.realsight.brain.timeseries.api.VideoCutAPI;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.util.data.TimeseriesData;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;

/**
 * @author Sun Muxin
 * 
 */ 
public class VideoCutExample {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public void main() throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String dataPath = Paths.get(root, "data").toString();
		TimeseriesData data = new TimeseriesData(dataPath+File.separator+"seq1.matwith.csv");
		List<DoubleSeries> mseries = new ArrayList<DoubleSeries>();
		MultipleDoubleSeries nSeries = data.getPropertyDoubleSeries();;
		VideoCutAPI p = new VideoCutAPI(nSeries);
		p.run(nSeries);
	}
	
	public static void main(String[] args) throws Exception {
		new VideoCutExample().main();
	}
}
