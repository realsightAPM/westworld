package com.realsight.brain.timeseries.lib.model.kalman.strategy;

import com.realsight.brain.timeseries.lib.model.kalman.action.Action;
import com.realsight.brain.timeseries.lib.model.kalman.context.KalmanContext;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;

public interface KalmanStrategy {
	public void onStart(MultipleDoubleSeries series);
	public void onTick(KalmanContext context);
	public void onEnd();
	public double onForecast(KalmanContext context);
	public DoubleSeries getError();
	public DoubleSeries getVariance();
	public DoubleSeries getModel();
	public Action getCoint();
}
