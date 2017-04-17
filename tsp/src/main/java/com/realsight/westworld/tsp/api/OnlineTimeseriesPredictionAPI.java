package com.realsight.westworld.tsp.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.model.TimeseriesPrediction;
import com.realsight.westworld.tsp.lib.model.htm.PredictHierarchy;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;
import com.realsight.westworld.tsp.lib.util.Util;
import com.rits.cloning.Cloner;

import Jama.Matrix;

public class OnlineTimeseriesPredictionAPI extends TimeseriesPrediction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5226623086002164960L;
	
	private DoubleSeries[] mSeries = null;
	private static final int HTM_len = 1000;
	private Double[][] up2down = new Double[2][];
	private PredictHierarchy t_HTM = null;
	private Cloner cloner = new Cloner();
	private Matrix pre_value = null;
	private Random rng = new Random();
	
	public void update(MultipleDoubleSeries mSeries) {
		HTM = PredictHierarchy.build(mSeries, true);
	}
	
	public Entry<Double> todayValue(Matrix value, Long timestamp) {
		t_HTM = null;
		if(up2down[0] == null){
			up2down[0] = new Double[value.getRowDimension()];
			Arrays.fill(up2down[0], Double.MIN_VALUE);
		}
		if(up2down[1] == null){
			up2down[1] = new Double[value.getRowDimension()];
			Arrays.fill(up2down[1], Double.MAX_VALUE);
		}
		for (int i = 0; i < value.getRowDimension(); i++) {
			up2down[0][i] = Math.max(up2down[0][i], value.get(i, 0));
			up2down[1][i] = Math.min(up2down[1][i], value.get(i, 0));
		}
		if (mSeries == null) {
			mSeries = new DoubleSeries[value.getRowDimension()];
			for (int i = 0; i < value.getRowDimension(); i++) {
				mSeries[i] = new DoubleSeries(String.valueOf(i));
			}
		}
		if (mSeries.length != value.getRowDimension()) {
			return null;
		}
		if ((HTM == null) && (mSeries[0].size() < HTM_len)) {
			for (int i = 0; i < value.getRowDimension(); i++) {
				mSeries[i].add(new Entry<Double>(value.get(i, 0), timestamp));
			}
			return null;
		} else if (HTM == null) {
			MultipleDoubleSeries nSeries = new MultipleDoubleSeries("prediction", mSeries);
			HTM = PredictHierarchy.build(nSeries, true);
			return null;
		}
		pre_value = value;
		Double score = HTM.learn(value);
		return new Entry<Double>(score, timestamp);
	}
	
	public Matrix prediction() {
		Matrix auxiliary = new Matrix(up2down[0].length, 1);
		for ( int i = up2down[0].length-1; i >= 0; i--) {
			auxiliary.set(i, 0, Double.NaN);
		}
		return prediction(auxiliary);
	}
	
	public Matrix prediction(Matrix auxiliary) {
		if (t_HTM == null) {
			t_HTM = cloner.deepClone(HTM);
		}
		if (t_HTM == null) return null;
		if (pre_value == null)	return null;
		double max_score = Double.MIN_VALUE; //t_HTM.predict(pre_value);
		Matrix res = pre_value;
		for ( int i = up2down[0].length-1; i >= 0; i--) {
			if(!Double.isNaN(auxiliary.get(i, 0))) {
				res.set(i, 0, auxiliary.get(i, 0));
				continue;
			}
			double value_p = pre_value.get(i, 0);
			double up = up2down[0][i];
			double down = up2down[1][i];
//			System.out.println(down + " <-> " + up);
			double scope = (up - down)/100.0;
			double up_p = value_p, down_p = value_p;
			double pre_score = -1.0;
			for (double value_i = down; value_i <= up; value_i += scope) {
				Matrix value = res.copy();
				value.set(i, 0, value_i);
				double score = t_HTM.predict(value);
				
				if (max_score < score){
//					System.out.println(score + " " + value_i);
					max_score = score;
					up_p = value_i;
					down_p = value_i;
				} else if (max_score==score && pre_score==score) {
					up_p = value_i;
				}
				pre_score = score;
			}
//			System.out.println(down_p + " <-> " + up_p);
//			res.set(i, 0, (up_p-down_p)*0.5+down_p);
			res.set(i, 0, (up_p-down_p)*rng.nextDouble()+down_p);
//			res.set(i, 0, down_p);
		}
		pre_value = res;
		t_HTM.predict_learn(res);
		return res;
	}
	
	public double solve(MultipleDoubleSeries mSeries){
		int len = mSeries.size();
		double res = 10000;
		double ms = 0;
		
		for (int s = 0; s < 200; s++) {
			double sum = 0;
			int sz = 0;
//			double tmp = mSeries.get(len/2).getItem().get(0);
			for (int i = len/2; i < len; i++){
				Matrix real = Util.toVec(mSeries.get(i).getItem().iterator());
				for (int r = 0; r < real.getRowDimension(); r++) {
					sum += 1.0*Math.abs(real.get(r, 0)-s)/real.get(r, 0);
					sz += 1;
				}
			}
			if (res > sum/sz){
				ms = s;
				res = sum/sz;
			}
		}
		double sum = 0;
		for (int i = 0; i < len/2; i++) {
			sum += mSeries.get(i).getItem().get(0);
		}
//		System.out.println(sum + ", " + ms);
		return res;
	}
	
	public double AMPE(MultipleDoubleSeries mSeries) {
		if (this.HTM != null) {
			this.HTM.sleep();
		}
//		if (mSeries.size() > 0)
//			return solve(mSeries);
		int len = mSeries.size();
		double sum = 0, sz = 0;
		for (int i = 0; i < len; i++) {
			Matrix real = Util.toVec(mSeries.get(i).getItem().iterator());
			Long timestamp = mSeries.get(i).getInstant();
			if(i > len/2) {
				Matrix pm = prediction();
				if (pm != null){
					for (int r = 0; r < pm.getRowDimension(); r++) {
						sum += 1.0*Math.abs(pm.get(r, 0)-real.get(r, 0))/(real.get(r, 0)+100);
						sz += 1;
					}
				}
			}
			todayValue(real, timestamp);
		}
		if (sz == 0)
			return Double.NaN;
		return sum / sz;
	}
	
	public List<Integer> submmit(MultipleDoubleSeries mSeries) {
		if (this.HTM != null) {
			this.HTM.sleep();
		}
		int len = mSeries.size();
		for (int i = 0; i < len; i++) {
			Matrix real = Util.toVec(mSeries.get(i).getItem().iterator());
			Long timestamp = mSeries.get(i).getInstant();
			todayValue(real, timestamp);
		}
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < len; i++) {
			Matrix pm = prediction();
			if (pm == null){
				res.add(0);
				continue;
			}
 			for (int r = 0; r < pm.getRowDimension(); r++) {
				res.add( (int) (pm.get(r, 0)));
			}
		}
		return res;
	}
	
	public void train(MultipleDoubleSeries mSeries) {
		if (this.HTM != null) {
			this.HTM.sleep();
		}
		for (int i = 0; i < mSeries.size(); i++) {
			Matrix vec = Util.toVec(mSeries.get(i).getItem().iterator());
			Long timestamp = mSeries.get(i).getInstant();
			todayValue(vec, timestamp);
		}
		double ampe = AMPE(mSeries);
		if (! Double.isNaN(ampe) )
			System.err.println("Train AMPE is " + ampe);
	}
	
	public void train(List<MultipleDoubleSeries> lmSeries) {
		for (MultipleDoubleSeries mSeries : lmSeries) {
			train(mSeries);
		}
	}
	
	public double test(List<MultipleDoubleSeries> lmSeries) {
		int schedule = 0;
		double sum = 0;
		for (MultipleDoubleSeries mSeries : lmSeries) {
			double ampe = AMPE(mSeries);
			if(Double.isNaN(ampe)) continue;
			sum += ampe;
			schedule += 1;
		}
		if (! Double.isNaN(sum))
			System.err.println("Test AMPE is " + (sum/schedule));
		return sum/schedule;
	}
}
