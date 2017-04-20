package com.realsight.westworld.tsp.lib.model;

import java.io.Serializable;

import com.realsight.westworld.tsp.lib.model.htm.AnormalyHierarchy;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

public abstract class AnormalyDetection implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3804428275026575934L;
	
	protected AnormalyHierarchy HTM = null;
	
	public AnormalyDetection() {}
	
	public abstract Entry<Double> detection(double value, Long timestamp);
}
