package com.realsight.brain.timeseries.api;

import com.realsight.brain.timeseries.lib.model.anomaly.AnormalyDetection;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;

public class OnlineAnormalyDetectionAPI extends AnormalyDetection{
	public OnlineAnormalyDetectionAPI(DoubleSeries nSeries, double minValue, double maxValue) {
		super(nSeries, minValue, maxValue);
	}
	
	public OnlineAnormalyDetectionAPI(double minValue, double maxValue) {
		this(null, minValue, maxValue);
	}
	
	public OnlineAnormalyDetectionAPI(DoubleSeries nSeries) {
		super(nSeries);
	}
}
