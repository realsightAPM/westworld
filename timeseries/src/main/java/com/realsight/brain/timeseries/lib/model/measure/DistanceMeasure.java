package com.realsight.brain.timeseries.lib.model.measure;

import javax.validation.UnexpectedTypeException;

import com.realsight.brain.timeseries.lib.model.measure.algorithm.KullbackLeibleer;
import com.realsight.brain.timeseries.lib.model.measure.algorithm.MeasureAlgorithm;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;

public class DistanceMeasure {
	private int numBit = 3;
	private MeasureAlgorithm measureAlgorithm = MeasureAlgorithm.KULLBACK_LEIBLEER;
	
	private DistanceMeasure(MeasureAlgorithm measureAlgorithm) {
		this.measureAlgorithm = measureAlgorithm;
	}
	
	public double distance(DoubleSeries lSeries, DoubleSeries rSeries){
		if ( measureAlgorithm == MeasureAlgorithm.KULLBACK_LEIBLEER){
			return KullbackLeibleer.distance(lSeries, rSeries, numBit);
		}
		throw new UnexpectedTypeException("no this method.");
	}
	
	public static DistanceMeasure build(MeasureAlgorithm measureAlgorithm) {
		return new DistanceMeasure(measureAlgorithm);
	}
}
