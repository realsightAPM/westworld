package com.realsight.westworld.tsp.lib.model.anomaly;

import java.io.Serializable;

import com.realsight.westworld.tsp.lib.model.htm.AnormalyHierarchy;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;

import Jama.Matrix;
import com.realsight.westworld.tsp.lib.series.TimeSeries;

public abstract class AnormalyDetection implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3804428275026575934L;
	
	protected AnormalyHierarchy anormalyHTM = null;
	
	public AnormalyDetection(MultipleDoubleSeries nSeries) {
		anormalyHTM = AnormalyHierarchy.build(nSeries);
	}
	
	public TimeSeries.Entry<Double> detection(Matrix value, Long timestamp) {
		double score = anormalyHTM.detectorAnomaly(value, timestamp);
		return (new TimeSeries.Entry<Double>(score, timestamp));
	}
}
