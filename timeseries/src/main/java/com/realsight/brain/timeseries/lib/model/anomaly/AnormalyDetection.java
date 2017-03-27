package com.realsight.brain.timeseries.lib.model.anomaly;

import java.io.Serializable;

import com.realsight.brain.timeseries.lib.model.htm.AnormalyHierarchy;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

import Jama.Matrix;

public abstract class AnormalyDetection implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3804428275026575934L;
	
	protected AnormalyHierarchy anormalyHTM = null;
	
	public AnormalyDetection(MultipleDoubleSeries nSeries) {
		anormalyHTM = AnormalyHierarchy.build(nSeries);
	}
	
	public Entry<Double> detection(Matrix value, Long timestamp) {
		double score = anormalyHTM.detectorAnomaly(value, timestamp);
		return (new Entry<Double>(score, timestamp));
	}
}
