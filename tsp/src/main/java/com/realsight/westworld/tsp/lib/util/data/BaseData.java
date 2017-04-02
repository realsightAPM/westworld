package com.realsight.westworld.tsp.lib.util.data;

import com.realsight.westworld.tsp.lib.series.TimeSeries;

abstract class BaseData {
	public abstract TimeSeries<?> getPropertySeries(String name);
}
