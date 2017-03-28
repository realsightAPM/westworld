package com.realsight.westworld.tsp.api;

import java.util.ArrayList;
import java.util.List;

import com.realsight.westworld.tsp.lib.model.anomaly.AnormalyDetection;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

import Jama.Matrix;
import com.realsight.westworld.tsp.lib.series.TimeSeries;

public class VideoCutAPI extends AnormalyDetection{
	
	private List<Matrix> list = new ArrayList<Matrix>();
		
	public VideoCutAPI(MultipleDoubleSeries nSeries) {
		super(nSeries);
	}
	
	public double todayValue(Matrix value, Long timestamp) {
		list.add(value);
		return detection(value, timestamp).getItem();
	}
	
	public List<Double> run(MultipleDoubleSeries nSeries) {
		int a_id = 0;
		int len = 100;
		DoubleSeries scores = new DoubleSeries("as");
		List<Double> res = new ArrayList<Double>();
		for ( int i = 0; i < nSeries.size(); i++ ) {
			Matrix value = Util.toVec(nSeries.get(i).getItem().iterator());
			long timestamp = nSeries.get(i).getInstant();
			double as = todayValue(value, timestamp);
//			if (as < 0.9) as = 0;
			
			if (as > 0.8 && i-len>a_id) {
				a_id = i;
				scores.add(new TimeSeries.Entry<Double>(as, timestamp));
				System.out.println(i + " " + as);
			} else {
				scores.add(new TimeSeries.Entry<Double>(0.0, timestamp));
			}
//			if(i%500 == 0) System.out.print(i + " -> ");
		}
//		System.out.println(sum);
		Plot.plot("video cut", scores);
		return res;
	}
}
