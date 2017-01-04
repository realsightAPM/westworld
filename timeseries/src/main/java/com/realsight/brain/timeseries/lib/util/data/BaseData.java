package com.realsight.brain.timeseries.lib.util.data;

import com.realsight.brain.timeseries.lib.series.TimeSeries;

abstract class BaseData {
	public abstract TimeSeries<?> getPropertySeries(String name);
}
