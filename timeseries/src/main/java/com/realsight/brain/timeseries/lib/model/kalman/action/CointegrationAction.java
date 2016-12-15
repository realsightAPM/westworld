package com.realsight.brain.timeseries.lib.model.kalman.action;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import com.realsight.brain.timeseries.lib.model.kalman.context.KalmanContext;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.evaluation.NMSE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CointegrationAction implements com.realsight.brain.timeseries.lib.model.kalman.strategy.KalmanStrategy {
	
    private static Logger log = LogManager.getLogger(CointegrationAction.class);

    boolean mReinvest = false;

    String mY;
    List<String> mX;
    KalmanContext mContext;
    Action mCoint;

    DoubleSeries mYs;
    DoubleSeries mError;
    DoubleSeries mVariance;
    DoubleSeries mModel;

    public CointegrationAction(List<String> x, String y) {
        mX = x;
        mY = y;
    }

    @Override
    public void onStart(MultipleDoubleSeries series) {
        try {
			mCoint = new Action(series, mY, mX, "identity");
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
        double[] x = new double[mX.size()];
        for(int i = 0; i < mX.size(); i++){
//        	System.out.println(mX.get(i));
        	x[i] = mContext.getLastValue(mX.get(i));
        }
        double y = mContext.getLastValue(mY);
        
        mCoint.step(x, y);
        mYs.add(y, mContext.getTime());
        mError.add(mCoint.getError(), mContext.getTime());
        mVariance.add(Math.sqrt(mCoint.getVariance()), mContext.getTime());
        mModel.add(y-mCoint.getError(), mContext.getTime());
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
        return "CointegrationStrategy{" +
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
		return mCoint.getPredict(x);
	}

	@Override
	public Action getCoint() {
		// TODO Auto-generated method stub
		return this.mCoint;
	}
}
