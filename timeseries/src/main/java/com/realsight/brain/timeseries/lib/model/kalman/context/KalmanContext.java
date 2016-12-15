package com.realsight.brain.timeseries.lib.model.kalman.context;

import java.util.List;

import com.realsight.brain.timeseries.lib.series.TimeSeries;

public interface KalmanContext {
	public Long getTime();

	public double getLastValue(String instrument);

	public List<TimeSeries.Entry<Double>> getHistory(String instrument);

	public List<String> getInstruments();
}
