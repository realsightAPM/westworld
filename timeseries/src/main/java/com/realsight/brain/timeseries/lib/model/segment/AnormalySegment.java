package com.realsight.brain.timeseries.lib.model.segment;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.Util;

public class AnormalySegment {
	private double maxScore = 0.0;
	private DoubleSeries nSeries = new DoubleSeries("series");
	private DoubleSeries anormlySeriesSegment = new DoubleSeries("anormly Segment");
	private int step = 1;
	private final int finishStep = 2000;
	private final int windowsLen = 50;
	private final double threshold = 8.0;
	private double minValue;
	private double maxValue;
	private final int numBit = 3;
	private final int numNormValue = (1<<numBit);
	private double fullValueRange;
	private double minValueStep;
	private double[] pro = null;
	
	private AnormalySegment(DoubleSeries nSeries, double minValue, double maxValue) {
		this.nSeries = new DoubleSeries("series");
		this.anormlySeriesSegment = new DoubleSeries("anormly Segment");
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.fullValueRange = this.maxValue - this.minValue;
		int numNormValue = (1<<numBit) - 1;
        if ( this.fullValueRange == 0.0 ) {
        	this.fullValueRange = numNormValue;
        }
		this.minValueStep = this.fullValueRange / numNormValue;
		this.pro = new double[this.numNormValue];
		for ( int i = 0; i < this.numNormValue; i++ ) {
			this.pro[i] += 0.1;
		}
		for(int i = 0; i < nSeries.size(); i++){
			double value = nSeries.get(i).getItem();
			int bit = getBit(value);
			this.pro[bit] += 1.0;
		}
		this.nSeries = nSeries;
	}
	
	private AnormalySegment(DoubleSeries nSeries) {
		this(nSeries, nSeries.min(), nSeries.max());
	}

	private int getBit(double value) {
		int bit = (int) ((value-this.minValue)/this.minValueStep);
		return bit;
	}
	
	private double mutual(DoubleSeries Q){
		double res = 0.0, sumP = 0.0, sumQ = 0;
		double[] q = new double[numNormValue];
		for(int i = 0; i < numNormValue; i++){
			sumP += this.pro[i];
		}
		for(int i = 0; i < Q.size(); i++){
			int bit = getBit(Q.get(i).getItem());
			q[bit] += 1.0;
			sumQ += 1.0;
		}
		for(int i = 0; i < numNormValue; i++) {
			if(q[i] == 0) continue;
			res += q[i]*Math.log(q[i]/sumQ);
			res -= q[i]*Math.log(this.pro[i]/sumP);
		}
		return res;
	}
	
	public double detectorAnomaly(Double value, Long timestamp) {
		int bit = getBit(value);
		this.pro[bit] += 1.0;
		nSeries.add(new Entry<Double>(value, timestamp));
		if (nSeries.size() > windowsLen){
			int n = nSeries.size();
			nSeries = nSeries.subSeries(n-windowsLen, n);
		}
		double score = mutual(nSeries);
		if(step<finishStep || score<maxScore*threshold){
			step += 1;
			maxScore = Math.max(score, maxScore);
			anormlySeriesSegment.add(new Entry<Double>(score, timestamp));
		}
		double res = score/Math.sqrt(anormlySeriesSegment.variance()+1e-4)/10;
		return Util.Sigma(res);
	}
	
	public double detectorAnomaly(Double value, Long timestamp, boolean anormly) {
		if(anormly == false) {
			int bit = getBit(value);
			this.pro[bit] += 1.0;
		}
		nSeries.add(new Entry<Double>(value, timestamp));
		if (nSeries.size() > windowsLen){
			int n = nSeries.size();
			nSeries = nSeries.subSeries(n-windowsLen, n);
		}
		double score = mutual(nSeries);
		if(anormly == false){
			step += 1;
			maxScore = Math.max(score, maxScore);
			anormlySeriesSegment.add(new Entry<Double>(score, timestamp));
		}
		double res = score/Math.sqrt(anormlySeriesSegment.variance()+1e-4)/10;
		return Util.Sigma(res);
	}
	
	public DoubleSeries detectorSeriesAnomaly(DoubleSeries nSeries) {
		DoubleSeries res = new DoubleSeries("anormly value");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double anormly = detectorAnomaly(nSeries.get(i).getItem(), nSeries.get(i).getInstant());
			res.add(new Entry<Double>(anormly, nSeries.get(i).getInstant()));
		}
		return res;
	}
	
	public static AnormalySegment build(DoubleSeries nSeries, double minValue, double maxValue){
		return new AnormalySegment(nSeries, minValue, maxValue);
	}
}

