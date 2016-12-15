package com.realsight.brain.timeseries.lib.model.kalman.context;

import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BacktestContext implements KalmanContext {
	public Long mInstant;
    public List<Double> mValue;
    public List<String> mInstruments;
    public MultipleDoubleSeries mHistory;

    @Override public Long getTime() {
        return mInstant;
    }

    @Override public double getLastValue(String instrument) {
        return mValue.get(mInstruments.indexOf(instrument));
    }

    @Override public List<TimeSeries.Entry<Double>> getHistory(String instrument) {
        int index = mInstruments.indexOf(instrument);
        List<TimeSeries.Entry<Double>> res = new ArrayList<TimeSeries.Entry<Double>>();
        for( Entry<LinkedList<Double>> t : mHistory.reverse()){
        	res.add(new TimeSeries.Entry<>(t.getItem().get(index), t.getInstant()));
        }
        return res;
    }


    @Override public List<String> getInstruments() {
        return mInstruments;
    }

}

