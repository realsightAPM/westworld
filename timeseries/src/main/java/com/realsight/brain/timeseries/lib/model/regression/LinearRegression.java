package com.realsight.brain.timeseries.lib.model.regression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jblas.DoubleMatrix;
import org.jblas.Solve;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.data.Anomaly;

public class LinearRegression {
	
	private double[][] inputs = null;
	private double[] outputs = null;
	private DoubleMatrix delta = null;
	
	String mY;
    List<String> mX;
    double mSSE;
    
    public LinearRegression(List<String> x, String y){
    	mX = x;
        mY = y;
    }
	
	private double[][] getTrainingInput(MultipleDoubleSeries series){
		double[][] inputs = new double[series.size()][];
		for(int i = 0; i < series.size(); i++){
			inputs[i] = new double[mX.size()+1];
			inputs[i][0] = 1.0;
			for(int j = 0; j < mX.size(); j++){
				int xId = series.indexOf(mX.get(j));
				inputs[i][1+j] = series.get(i).getItem().get(xId);
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
			DoubleMatrix x = new DoubleMatrix(1+mX.size());
			x.put(new int[]{0}, 1.0);
			for(int i = 0; i < mX.size(); i++){
				int xId = series.indexOf(mX.get(i));
				x.put(new int[]{1+i}, entry.getItem().get(xId));
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
	
	public static void main(String[] args) throws IOException{
		List<String> x = new ArrayList<String>();
        String y = "value-0";
        x.add("value-"+1);
        x.add("value-"+23);
        x.add("value-"+24);
        x.add("value-"+25);
        
        String dir = "D:/workspace/NAB/src/results/null/realAdExchange/null_exchange-4_cpc_results.csv";
        // download historical prices
        Anomaly.setLocalDir(dir);
        MultipleDoubleSeries series = new MultipleDoubleSeries(Anomaly.getPropertySeries("value"), 
        		Anomaly.getPropertySeries("value", 1), Anomaly.getPropertySeries("value", 25), 
        		Anomaly.getPropertySeries("value", 23), Anomaly.getPropertySeries("value", 24));
        System.out.println(Util.sd(series, x, y) + " " + (series.getColumn(y)));
	}
}
