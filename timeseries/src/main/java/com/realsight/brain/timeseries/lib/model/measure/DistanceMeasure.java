package com.realsight.brain.timeseries.lib.model.measure;

import javax.activation.UnsupportedDataTypeException;

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
		try {
			throw new UnsupportedDataTypeException("no this method.");
		} catch (UnsupportedDataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}
	
	public static DistanceMeasure build(MeasureAlgorithm measureAlgorithm) {
		if ( measureAlgorithm == null )
			measureAlgorithm = MeasureAlgorithm.KULLBACK_LEIBLEER;
		return new DistanceMeasure(measureAlgorithm);
	}
	
	public static DistanceMeasure build() {
		MeasureAlgorithm measureAlgorithm = MeasureAlgorithm.KULLBACK_LEIBLEER;
		return new DistanceMeasure(measureAlgorithm);
	}
}
