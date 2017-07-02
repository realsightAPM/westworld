package com.realsight.westworld.tsp.lib.model;

import java.io.Serializable;

import com.realsight.westworld.tsp.lib.model.htm.VideoHierarchy;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

import Jama.Matrix;

public abstract class VideoCut implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3804428275026575934L;
	
	protected VideoHierarchy HTM = null;
	
	public VideoCut() {}
	
	public abstract Entry<Double> detection(Matrix value, Long timestamp);
	public abstract void sleep();
}
