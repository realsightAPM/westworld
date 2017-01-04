package com.realsight.brain.timeseries.lib.model.segment;

import java.util.HashMap;
import java.util.Map;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.Util;

public class AnormalySegment {
	private double maxScore = 0.0;
	private DoubleSeries nSeries = new DoubleSeries("series");
	private DoubleSeries anormalySeriesSegment = new DoubleSeries("anormaly Segment");
	private int step = 1;
	private final int finishStep = 100;
	private final int windowsLen = 50;
	private final double threshold = 8.0;
	private double minValue;
	private double maxValue;
	private final int numBit = 3;
	private double fullValueRange;
	private double minValueStep;
	private Map<Integer, Double> pro = null;
	
	private void addPro(Map<Integer, Double> map, int bit){
		double key = this.getPro(map, bit);
		map.put(bit, key+1);
	}
	
	private double getPro(Map<Integer, Double> map, int bit) {
		if(map.containsKey(bit))
			return map.get(bit);
		return 0.1;
	}
	
	private AnormalySegment(DoubleSeries nSeries, double minValue, double maxValue) {
		this.nSeries = new DoubleSeries("series");
		this.anormalySeriesSegment = new DoubleSeries("anormaly Segment");
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.fullValueRange = this.maxValue - this.minValue;
		int numNormValue = (1<<numBit) - 1;
        if ( this.fullValueRange == 0.0 ) {
        	this.fullValueRange = numNormValue;
        }
		this.minValueStep = this.fullValueRange / numNormValue;
		this.pro = new HashMap<Integer, Double>();
		if (nSeries == null) return ;
		for(int i = 0; i < nSeries.size(); i++){
			double value = nSeries.get(i).getItem();
			int bit = getBit(value);
			this.addPro(this.pro, bit);
			this.nSeries.add(nSeries.get(i));
		}
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
		Map<Integer, Double> q = new HashMap<Integer, Double>();
		for(Map.Entry<Integer, Double> entry : this.pro.entrySet()){
			sumP += entry.getValue();
		}
		for(int i = 0; i < Q.size(); i++){
			int bit = getBit(Q.get(i).getItem());
			this.addPro(q, bit);
			sumQ += 1.0;
		}
		for(Map.Entry<Integer, Double> entry : q.entrySet()){
			int bit = entry.getKey();
			double value = entry.getValue();
			res += value*Math.log(value/sumQ);
			res -= value*Math.log(this.getPro(this.pro, bit)/sumP);
		}
		return res;
	}
	
	public double detectorAnomaly(Double value, Long timestamp) {
		int bit = getBit(value);
		this.addPro(this.pro, bit);
		nSeries.add(new Entry<Double>(value, timestamp));
		if (nSeries.size() > windowsLen){
			int n = nSeries.size();
			nSeries = nSeries.subSeries(n-windowsLen, n);
		}
		double score = mutual(nSeries);
		if(step<finishStep || score<maxScore*threshold){
			step += 1;
			maxScore = Math.max(score, maxScore);
			anormalySeriesSegment.add(new Entry<Double>(score, timestamp));
		}
		double res = score/Math.sqrt(anormalySeriesSegment.variance()+1e-4)/10;
		return Util.Sigma(res);
	}
	
	public double detectorAnomaly(Double value, Long timestamp, boolean anormaly) {
		if(anormaly == false) {
			int bit = getBit(value);
			this.addPro(this.pro, bit);
		}
		nSeries.add(new Entry<Double>(value, timestamp));
		if (nSeries.size() > windowsLen){
			int n = nSeries.size();
			nSeries = nSeries.subSeries(n-windowsLen, n);
		}
		double score = mutual(nSeries);
		if(anormaly == false){
			step += 1;
			maxScore = Math.max(score, maxScore);
			anormalySeriesSegment.add(new Entry<Double>(score, timestamp));
		}
		double res = score/Math.sqrt(anormalySeriesSegment.variance()+1e-4)/10;
		return Util.Sigma(res);
	}
	
	public DoubleSeries detectorSeriesAnomaly(DoubleSeries nSeries) {
		DoubleSeries res = new DoubleSeries("anormaly value");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double anormaly = detectorAnomaly(nSeries.get(i).getItem(), nSeries.get(i).getInstant());
			res.add(new Entry<Double>(anormaly, nSeries.get(i).getInstant()));
		}
		return res;
	}
	
	public static AnormalySegment build(DoubleSeries nSeries, double minValue, double maxValue){
		return new AnormalySegment(nSeries, minValue, maxValue);
	}
}

