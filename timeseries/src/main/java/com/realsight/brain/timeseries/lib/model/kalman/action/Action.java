package com.realsight.brain.timeseries.lib.model.kalman.action;

import java.util.ArrayList;
import java.util.List;

import org.la4j.Matrix;

import com.realsight.brain.timeseries.lib.model.kalman.filter.KalmanFilter;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;

public class Action {
	String mY;
	List<String> mH;
	MultipleDoubleSeries mSeries;
	List<Matrix> mX;
	KalmanFilter mFilter;
    int mNobs;
    int iter_num = 1;
    double eps = 1e-7;
    int train_num = 200;
    
    public Action(MultipleDoubleSeries series, String y, List<String> h, String type) throws Exception {
    	this(series, y, h, type, 1e-2, 1e-4);
    }
    
    public Action(MultipleDoubleSeries series, String y, List<String> h, String type, double r, double delta) throws Exception {
    	mY = y;
    	mH = h;
    	mSeries = series;
    	mNobs = h.size();
    	Matrix vw = Matrix.identity(mNobs).multiply(delta / (1 - delta));
    	Matrix a = Matrix.identity(mNobs);
    	if (type.equals("poly")) {
        	for(int i = 0; i+1 < mNobs; i++){
        		a.set(i, i+1, 1.0);
        	}
    	} else if (type.equals("identity")){
    		// passed
    	} else if (type.equals("seas")){
        	for(int i = 0; i+1 < mNobs; i++){
        		a.set(i, i+1, 1.0);
        	}
    	}
    	mFilter = new KalmanFilter(mNobs, 1);
        mFilter.setUpdateMatrix(a);
        mFilter.setUpdateCovariance(vw);
        mFilter.setMeasurementCovariance(Matrix.constant(1, 1, r));
        mFilter.setStateCovariance(Matrix.identity(mNobs).multiply(1e-2));
        mFilter.setState(Matrix.zero(mNobs, 1));
    	mFilter.setStateCovariance(Matrix.identity(mNobs).multiply(1e7));
        for(int iter = 0; iter < iter_num; iter++){
        	stepE();
        	stepM();
        }
        mFilter.setState(Matrix.zero(mNobs, 1));
    	mFilter.setStateCovariance(Matrix.identity(mNobs).multiply(1e7));
    }
    
    private void stepE(){
    	mX = new ArrayList<Matrix>();
    	mFilter.setState(Matrix.zero(mNobs, 1));
    	mFilter.setStateCovariance(Matrix.identity(mNobs).multiply(1e7));
    	for(int i = 0; i < mSeries.size(); i++){
    		double y = mSeries.getColumn(mY).get(i).getItem();
    		double[] h = new double[mNobs];
    		for(int j = 0; j < mNobs; j++){
    			h[j] = mSeries.getColumn(mH.get(j)).get(i).getItem();
    		}
    		step(h, y);
    		mX.add(getState());
//    		System.out.println(y + " " + mFilter.getUpdateMatrix().multiply(getState()).get(0, 0));
    	}
    	
    	double r = 0;
    	for(int i = Math.max(0, mX.size()-train_num); i < mSeries.size(); i++){
    		double y = mSeries.getColumn(mY).get(i).getItem();
    		double[] h = new double[mNobs];
    		for(int j = 0; j < mNobs; j++){
    			h[j] = mSeries.getColumn(mH.get(j)).get(i).getItem();
    		}
    		
    		double e = y - Matrix.from1DArray(1, mNobs, h).multiply(mX.get(i)).get(0, 0);
    		r += e*e+eps;
    	}
    	r /= (mSeries.size()-Math.max(0, mX.size()-train_num));
    	mFilter.setMeasurementCovariance(Matrix.constant(1, 1, r));
//    	System.out.println(r);
    }
    
    private void stepM(){

//    	System.out.println(r);
//    	Matrix X = Matrix.zero(mNobs, mX.size()-1);
//    	Matrix Y = Matrix.zero(mNobs, mX.size()-1);
//    	for(int i = Math.max(1, mX.size()-train_num); i < mX.size(); i++){
//    		X.setColumn(i-1, (mX.get(i-1).getColumn(0)));
//    		Y.setColumn(i-1, (mX.get(i).getColumn(0)));
//    	}
//    	Matrix XtX = X.multiply(X.transpose()).add(Matrix.identity(mNobs).multiply(eps));
//    	Matrix invXtX = XtX.withInverter(LinearAlgebra.InverterFactory.SMART).inverse();
//    	Matrix A = invXtX.multiply(X).multiply(Y.transpose()).transpose();
//    	mFilter.setUpdateMatrix(A);
    	
    	Matrix Q = Matrix.zero(mNobs, mNobs);
    	for(int i = Math.max(1, mX.size()-train_num); i < mX.size(); i++){
    		Matrix E = mX.get(i).subtract(mFilter.getUpdateMatrix().multiply(mX.get(i-1)));
    		Q = Q.add(E.multiply(E.transpose()));
    	}
    	Q = Q.divide(mSeries.size()-Math.max(1, mX.size()-train_num));
    	mFilter.setUpdateCovariance(Q);
//    	System.out.println(Q);
    }

    public void step(double[] h, double y) {
        mFilter.setExtractionMatrix(Matrix.from1DArray(1, mNobs, h));
        mFilter.step(Matrix.constant(1, 1, y));
    }

    public Matrix getState() {
        return mFilter.getState();
    }

    public double getVariance() {
        return mFilter.getInnovationCovariance().get(0, 0);
    }

    public double getError() {
        return mFilter.getInnovation().get(0, 0);
    }
    
    public double getPredict(double[] x) {
    	double[] tx = new double[mNobs];
    	for(int i = 0; i < mNobs; i++)
    		tx[i] = x[i-1];
		return mFilter.getNextMeasurement(Matrix.from1DArray(1, mNobs, tx)).get(0, 0);
    }
    
    public KalmanFilter getFilter() {
    	return this.mFilter;
    }
}
