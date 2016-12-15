package com.realsight.brain.timeseries.lib.model.kalman.test;

import com.realsight.brain.timeseries.lib.model.kalman.context.BacktestContext;
import com.realsight.brain.timeseries.lib.model.kalman.filter.KalmanFilter;
import com.realsight.brain.timeseries.lib.model.kalman.strategy.KalmanStrategy;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

import java.util.Iterator;
import java.util.LinkedList;

import static com.realsight.brain.timeseries.lib.util.Util.check;

public class Backtest {

    MultipleDoubleSeries mSeries;
    double mLeverage = 1;

    KalmanStrategy mStrategy;
    BacktestContext mContext;

    Iterator<Entry<LinkedList<Double>>> mValueIterator;
    
    public Backtest() {
        mSeries = null;
    }
    
    public Backtest(MultipleDoubleSeries series) {
        check(series.isAscending());
        mSeries = series;
    }

    public void run(KalmanStrategy strategy, MultipleDoubleSeries series) {
        initialize(strategy, series);
        while (nextStep()) ;
        return ;
    }

    public void initialize(KalmanStrategy strategy, MultipleDoubleSeries series) {
        mStrategy = strategy;
        mContext = new BacktestContext();
        if(mSeries != null){
	        mContext.mInstruments = mSeries.getNames();
	        mContext.mHistory = new MultipleDoubleSeries(mContext.mInstruments);
	        mValueIterator = mSeries.getData().iterator();
        }
        strategy.onStart(series);
    }

    public boolean nextStep() {
        if (mValueIterator==null || !mValueIterator.hasNext()) {
            finish();
            return false;
        }
        Entry<LinkedList<Double>> entry = mValueIterator.next();
        mContext.mValue = entry.getItem();
        mContext.mInstant = entry.getInstant();
        mStrategy.onTick(mContext);
        mContext.mHistory.add(entry);
        return true;
    }
    
    public double nextStep(Entry<LinkedList<Double>> entry) {
        mContext.mValue = entry.getItem();
        mContext.mInstant = entry.getInstant();
        mStrategy.onTick(mContext);
        mContext.mHistory.add(entry);
        return mStrategy.onForecast(mContext);
    }

    private void finish() {
        mStrategy.onEnd();
    }
    
    public DoubleSeries getErrors(){
    	return mStrategy.getError();
    }
    
    public KalmanStrategy getStrategy(){
    	return mStrategy;
    }
    
    public KalmanFilter getFilter(){
    	return mStrategy.getCoint().getFilter();
    }
}
