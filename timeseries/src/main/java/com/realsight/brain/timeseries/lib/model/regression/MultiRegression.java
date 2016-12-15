package com.realsight.brain.timeseries.lib.model.regression;

import java.util.LinkedList;
import java.util.List;

import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

public class MultiRegression {
	
	private double[][] inputs = null;
	private double[] outputs = null;
	private DoubleMatrix delta = null;
	
	String mY;
    List<String> mX;
    double mSSE;
    
    public MultiRegression(List<String> x, String y){
    	mX = x;
        mY = y;
    }
	
	private double[][] getTrainingInput(MultipleDoubleSeries series){
		double[][] inputs = new double[series.size()][];
		for(int i = 0; i < series.size(); i++){
			inputs[i] = new double[mX.size()*3+1];
			inputs[i][0] = 1.0;
			for(int j = 0; j < mX.size(); j++){
				int xId = series.indexOf(mX.get(j));
				double x_value = series.get(i).getItem().get(xId);
				inputs[i][1+j] = x_value;
			}
			for(int j = 0; j < mX.size(); j++){
				int xId = series.indexOf(mX.get(j));
				double x_value = series.get(i).getItem().get(xId);
				inputs[i][1+j+mX.size()] = Math.pow(x_value, 2);
			}
			for(int j = 0; j < mX.size(); j++){
				int xId = series.indexOf(mX.get(j));
				double x_value = series.get(i).getItem().get(xId);
				if(x_value > 0){
					inputs[i][1+j+mX.size()+mX.size()] = Math.sqrt(x_value);
				} else {
					inputs[i][1+j+mX.size()+mX.size()] = -Math.sqrt(-x_value);
				}
			}
		}
		return inputs;
	}
	
	private double[] getTrainingOutput(MultipleDoubleSeries series){
		int yId = series.indexOf(mY);
		double[] output = new double[series.size()];
		for(int i = 0; i < series.size(); i++){
			output[i] = series.get(i).getItem().get(yId);
		}
		return output;
	}
	
	public void training(MultipleDoubleSeries series){
		inputs = getTrainingInput(series);
		outputs = getTrainingOutput(series);	
		DoubleMatrix Y = new DoubleMatrix(outputs);
		DoubleMatrix X = new DoubleMatrix(inputs);
		DoubleMatrix Xt = X.transpose();
		DoubleMatrix XtX = Xt.mmul(X);
		DoubleMatrix invX = Solve.pinv(XtX);
		this.delta = invX.mmul(Xt).mmul(Y).transpose();
	}
	
	public DoubleSeries forecasting(MultipleDoubleSeries series){
		mSSE = 0.0;
		DoubleSeries res = new DoubleSeries("result");
		for(Entry<LinkedList<Double>> entry : series.getData()){
			DoubleMatrix x = new DoubleMatrix(1+mX.size()*3);
			x.put(new int[]{0}, 1.0);
			for(int i = 0; i < mX.size(); i++){
				int xId = series.indexOf(mX.get(i));
				double x_value = entry.getItem().get(xId);
				x.put(new int[]{1+i}, x_value);
			}
			for(int i = 0; i < mX.size(); i++){
				int xId = series.indexOf(mX.get(i));
				double x_value = entry.getItem().get(xId);
				x.put(new int[]{1+i+mX.size()}, Math.pow(x_value, 2));
			}
			for(int i = 0; i < mX.size(); i++){
				int xId = series.indexOf(mX.get(i));
				double x_value = entry.getItem().get(xId);
				if(x_value > 0){
					x.put(new int[]{1+i+mX.size()+mX.size()}, Math.sqrt(x_value));
				} else {
					x.put(new int[]{1+i+mX.size()+mX.size()}, -Math.sqrt(-x_value));
				}
			}
			res.add(new Entry<Double>(this.delta.mmul(x).get(0, 0), entry.getInstant()));
			double mE = this.delta.mmul(x).get(0, 0)-entry.getItem().get(series.getNames().indexOf(mY));
			mSSE += mE*mE;
		}
		mSSE /= res.size();
		return res;
	}
	
	public double sse(){
		return mSSE;
	}
}
