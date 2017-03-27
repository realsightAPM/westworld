package com.realsight.brain.timeseries.api;

import com.realsight.brain.timeseries.lib.model.anomaly.AnormalyDetection;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;

public class OnlineAnormalyDetectionAPI extends AnormalyDetection{
	public OnlineAnormalyDetectionAPI(MultipleDoubleSeries nSeries) {
		super(nSeries);
	}
}
