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

public class PredictionAPI extends AnormalyDetection{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5226623086002164960L;
	
	private List<Matrix> list = new ArrayList<Matrix>();
	private List<Matrix> us = new ArrayList<Matrix>();
	
	public PredictionAPI(MultipleDoubleSeries nSeries) {
		super(nSeries);
	}
	
	public double todayValue(Matrix value, Long timestamp) {
		list.add(value);
		us.add(value);
		return detection(value, timestamp).getItem();
	}
	
	public Matrix prediction(List<Matrix> us) {
		double max_p = -1.0;
		Matrix res = null;
		for (Matrix u : us) {
			double p = anormalyHTM.predict(-1L, u);
			if (max_p > p) continue;
			max_p = p;
			res = u;
		}
		return res;
	}
	
	public Double prediction() {
		double max_p = -1.0;
		Matrix res = null;
		for (Matrix u : us) {
			double p = anormalyHTM.predict(-1L, u);
			if (max_p > p) continue;
			max_p = p;
			res = u;
		}
		while(us.size() > 48) {
			us.remove(0);
		}
		return res.get(0, 0);
	}
	
	public List<Double> run(MultipleDoubleSeries nSeries) {
		@SuppressWarnings("unchecked")
		List<Matrix>[] uss = (List<Matrix>[]) new List<?>[(7*24)];
		
		for (int i = 0; i < uss.length; i++) {
			uss[i] = new ArrayList<Matrix>();
		}
		DoubleSeries p0 = new DoubleSeries("p0");
		DoubleSeries q0 = new DoubleSeries("q0");
		List<Double> res = new ArrayList<Double>();
		for ( int i = 0; i < nSeries.size(); i++ ) {
			Matrix value = Util.toVec(nSeries.get(i).getItem().iterator());
			long timestamp = nSeries.get(i).getInstant();
			if(i > 0) {
				Double t_value = prediction();
				double sse = Math.pow(t_value - value.get(0, 0),2);
//				double e1 = (t_value-value.get(0, 0));
//				double e2 = (t_value.get(1, 0)-value.get(1, 0)) / (value.get(1, 0)+1);
				res.add(Math.abs(sse));
				p0.add(new TimeSeries.Entry<Double>(t_value, timestamp));
				q0.add(new TimeSeries.Entry<Double>(value.get(0, 0), timestamp));
			} 
			uss[i%(7*24)].add(value);
			
			double score = todayValue(value, timestamp);
//			if(score < 0.7) score = score * 0.1;
//			System.out.print(String.format("[%d,%.1f],", timestamp, score));
			if(i%500 == 0) System.out.print(i + " -> ");
		}
//		System.out.println(sum);
		Plot.plot("prediction", q0, p0);
		return res;
	}
}
