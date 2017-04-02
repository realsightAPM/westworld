package com.realsight.westworld.tsp.api;

import com.realsight.westworld.tsp.lib.model.anomaly.AnormalyDetection;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;

public class OnlineAnormalyDetectionAPI extends AnormalyDetection{
	public OnlineAnormalyDetectionAPI(MultipleDoubleSeries nSeries) {
		super(nSeries);
	}
}
