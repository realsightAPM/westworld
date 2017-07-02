package com.realsight.westworld.tsp.test;

import com.realsight.westworld.tsp.api.OnlineAnomalyDetectionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

public class ShowExample {
	
	public static void run() throws Exception{
		DoubleSeries nSeries = new DoubleSeries("demo");
		nSeries.add(new Entry<Double>(2.0, 1L));
		nSeries.add(new Entry<Double>(2.0, 1L));
		nSeries.add(new Entry<Double>(2.0, 1L));
		nSeries.add(new Entry<Double>(2.0, 1L));
		nSeries.add(new Entry<Double>(2.0, 1L));
		OnlineAnomalyDetectionAPI oad = new OnlineAnomalyDetectionAPI(0.0, 8.0);
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double value = nSeries.get(i).getItem();
			System.out.println(value);
			Long timestamp = nSeries.get(i).getInstant();
			oad.detection(value, timestamp);
		}
	}
	
	public static void main(String[] args) throws Exception {
		ShowExample.run();
	}
}
