package com.realsight.westworld.tsp.api;

import com.realsight.westworld.tsp.lib.model.AnormalyDetection;
import com.realsight.westworld.tsp.lib.model.htm.AnormalyHierarchy;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

import Jama.Matrix;

public class OnlineAnormalyDetectionAPI extends AnormalyDetection{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1887304050004978018L;
	
	private DoubleSeries mSeries = new DoubleSeries("metric value");
	private static final int len = 200;
	
	public void update(DoubleSeries mSeries) {
		HTM = AnormalyHierarchy.build(mSeries);
	}
	
	public Entry<Double> detection(double value, Long timestamp) {
		if ((HTM==null) && (mSeries.size()<len)) {
			mSeries.add(new Entry<Double>(value, timestamp));
			return null;
		} else if (HTM == null){
			HTM = AnormalyHierarchy.build(mSeries, true);
			return null;
		}
		double score = HTM.learn(new Matrix(1, 1, value));
		return (new Entry<Double>(score, timestamp));
	}
}
