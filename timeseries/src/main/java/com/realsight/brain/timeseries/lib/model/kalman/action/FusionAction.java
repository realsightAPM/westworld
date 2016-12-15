package com.realsight.brain.timeseries.lib.model.kalman.action;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

import com.realsight.brain.timeseries.lib.model.kalman.context.KalmanContext;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.evaluation.NMSE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FusionAction implements com.realsight.brain.timeseries.lib.model.kalman.strategy.KalmanStrategy {
	
    private static Logger log = LoggerFactory.getLogger(FusionAction.class);

    boolean mReinvest = false;

    String mY;
    List<String> mX;
    List<String> types;
    KalmanContext mContext;
    List<Action> mCoints;
    
    DoubleSeries mYs;
    DoubleSeries mError;
    DoubleSeries mVariance;
    DoubleSeries mModel;
    
    public FusionAction(List<String> x, String y) {
        mX = x;
        mY = y;
        types = new ArrayList<String>();
//        types.add("poly");
        types.add("identity");
//        types.add("seas");
    }

    @Override
    public void onStart(MultipleDoubleSeries series) {
        try {
        	mCoints = new ArrayList<Action>();
        	for(String type : types){
        		mCoints.add(new Action(series, mY, mX, type));
        	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        mYs = new DoubleSeries("y");
        mError = new DoubleSeries("error");
        mVariance = new DoubleSeries("variance");
        mModel = new DoubleSeries("model");
    }

    @Override
    public void onTick(KalmanContext context) {
    	this.mContext = context;
        double[] x = new double[mX.size()+1];
        x[0] = 1.0;
        for(int i = 0; i < mX.size(); i++){
//        	System.out.println(mX.get(i));
        	x[i+1] = mContext.getLastValue(mX.get(i));
        }
        double y = mContext.getLastValue(mY);
        
        mYs.add(y, mContext.getTime());
        double variance = 1e7;
        double model = 0.0;
        
        for(Action mCoint : mCoints){
			mCoint.step(x, y);
        	double K = variance/(variance+mCoint.getVariance());
        	model = model+K*((y-mCoint.getError())-model);
        	variance = variance*(1.0-K);
        }
        
        
        mError.add(y-model, mContext.getTime());
        mVariance.add(Math.sqrt(variance), mContext.getTime());
        mModel.add(model, mContext.getTime());
//        System.out.println((beta * x + alpha) + " " + y + " " + x);
    }

    @Override
    public void onEnd() {
    	Path dir = Util.writeCsv(new MultipleDoubleSeries(mYs, mError, mVariance, mModel));
    	try {
			NMSE.nmse(dir.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        log.debug("Kalman filter statistics: " + dir);
    }

    @Override public String toString() {
        return "Fusionaction{" +
            "mY='" + mY + '\'' +
            ", mX='" + mX + '\'' +
            '}';
    }

    public DoubleSeries getYs() {
        return mYs;
    }

    public DoubleSeries getError() {
        return mError;
    }

    public DoubleSeries getVariance() {
        return mVariance;
    }

    public DoubleSeries getModel() {
        return mModel;
    }

	@Override
	public double onForecast(KalmanContext context) {
		// TODO Auto-generated method stub
		double[] x = new double[mX.size()];
        for(int i = 0; i < mX.size(); i++){
        	x[i] = context.getLastValue(mX.get(i));
        }
        
        double variance = 1e7;
        double model = 0.0;
        for(Action mCoint : mCoints){
			double t = mCoint.getPredict(x);
        	double K = variance/(variance+mCoint.getVariance());
        	model = model+K*(t-model);
        	variance = variance*(1.0-K);
        }
		return model;
	}

	@Override
	public Action getCoint() {
		// TODO Auto-generated method stub
		return null;
	}
}
