package com.realsight.westworld.tsp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.model.AnormalyDetection;
import com.realsight.westworld.tsp.lib.model.htm.AnomalyHierarchy;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries;
import com.realsight.westworld.tsp.lib.util.Triple;

import Jama.Matrix;

public class OnlineAnomalyDetectionAPI extends AnormalyDetection{
	
	/**
	 * @author 13976
	 */
	
	private static final long serialVersionUID = -1887304050004978018L;
	private DoubleSeries mSeries = new DoubleSeries("metric value");
	private static Random rng = new Random();
	private final int len = 500 + rng.nextInt(1000);
	private static final double eps = 1e-5;
	
	public OnlineAnomalyDetectionAPI() {}
	public OnlineAnomalyDetectionAPI(Double minValue, Double maxValue) {
		HTM = AnomalyHierarchy.build(minValue, maxValue);
	}
	
	public void update(DoubleSeries mSeries) {
		HTM = AnomalyHierarchy.build(mSeries);
	}
	
	public List<Triple<Double, Double, Double>> predict() {
		List<Triple<Double, Double, Double>> res = new ArrayList<Triple<Double, Double, Double>>();
		if (HTM == null)
			return res;
		double sumP = 0.0;
		for (int i = 0; i < HTM.getNumBit(); i++) {
			double from = HTM.getMin_value() + i*HTM.getScope();
			double to = HTM.getMin_value() + (i+1)*HTM.getScope();
			double value = (from+to)/2;
			double p = HTM.predict(new Matrix(1, 1, value))+eps;
			res.add(new Triple<Double, Double, Double>(from, to, p));
			sumP += p;
		}
		for (Triple<Double, Double, Double> r : res) {
			double p = r.getThird()/sumP;
			r.setThird(p);
		}
		return res;
	}
	
	public TimeSeries.Entry<Double> detection(double value, Long timestamp) {
//		System.err.println(value);
		if (Double.isNaN(value))
			return (new TimeSeries.Entry<Double>(0.0, timestamp));
		if ((HTM==null) && (mSeries.size()<len)) {
			mSeries.add(new TimeSeries.Entry<Double>(value, timestamp));
			return null;
		} else if (HTM == null){
			HTM = AnomalyHierarchy.build(mSeries, true);
			return null;
		}
		double score = HTM.learn(new Matrix(1, 1, value));
		return (new TimeSeries.Entry<Double>(score, timestamp));
	}
}
