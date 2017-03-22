package com.realsight.brain.timeseries.lib.model.anomaly;

import java.util.ArrayList;
import java.util.List;

import com.realsight.brain.timeseries.lib.model.htm.AnormalyHierarchy;
import com.realsight.brain.timeseries.lib.model.segment.AnormalySegment;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.Pair;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

public abstract class AnormalyDetection {
	protected int windowsLen = 50;
	protected AnormalyHierarchy anormalyHTM = null;
	protected AnormalySegment anormalySegment = null;
	protected double minValue = 0.0;
	protected double maxValue = 0.0;
	DoubleSeries anormalys = new DoubleSeries("anormalys");
	
	public AnormalyDetection(DoubleSeries nSeries, double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		anormalyHTM = AnormalyHierarchy.build(nSeries, minValue, maxValue);
		anormalySegment = AnormalySegment.build(nSeries, minValue, maxValue);
	}
	
	public AnormalyDetection(DoubleSeries nSeries) {
		anormalyHTM = AnormalyHierarchy.build(nSeries);
		anormalySegment = AnormalySegment.build(nSeries, nSeries.min(), nSeries.max());
	}
	
	@SuppressWarnings("unchecked")
	public MultipleDoubleSeries pridict(DoubleSeries nSeries, double baseThreshold) {
		
		double minValue = nSeries.min();
		double maxValue = nSeries.max();
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		
		DoubleSeries anormaly = new DoubleSeries("anormaly");
		DoubleSeries predict = new DoubleSeries("predict");
		
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double value = nSeries.get(i).getItem();
			Long timestamp = nSeries.get(i).getInstant();
			
			double p = 0, V = 0;
			for (double v = minValue; v < maxValue; v += (maxValue-minValue)/100) {
				double tmp_p = anormalyHTM.predict(-1L, v);
				if (tmp_p > p){
					p = tmp_p;
					V = v;
					
				}
			}
			Entry<Double> score = detection(value, timestamp);
			if ( score.getItem()>baseThreshold && i>200 ) {
				anormaly.add(new Entry<Double>(score.getItem(), score.getInstant()));
			} else {
				anormaly.add(new Entry<Double>(0.0, score.getInstant()));
			}
			predict.add(new Entry<Double>(V, score.getInstant()));
			
		}
		
		for (double value = minValue; value < maxValue; value += (maxValue-minValue)/1000) {
			x.add(value);
			y.add(anormalyHTM.predict(-1L, value));
		}
		Plot.plot("predict", x, y);
		return new MultipleDoubleSeries(anormaly, predict);
	}
	
	public Entry<Double> detection(double value, Long timestamp) {
		double score = anormalyHTM.detectorAnomaly(value, timestamp);
//		score += anormalySegment.detectorAnomaly(value, timestamp)/2;
//		System.out.println(timestamp + ", " + value + ", " + score);
		return (new Entry<Double>(score, timestamp));
	}
	
	public DoubleSeries detectorSeries(DoubleSeries nSeries) {
		DoubleSeries scores = new DoubleSeries("anormalys");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double value = nSeries.get(i).getItem();
			Long timestamp = nSeries.get(i).getInstant();
			scores.add(detection(value, timestamp));
		}
		return scores;
	}
	
	public DoubleSeries detectorSeries(DoubleSeries nSeries, double baseThreshold) {
		DoubleSeries scores = new DoubleSeries("anormalys");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double value = nSeries.get(i).getItem();
			Long timestamp = nSeries.get(i).getInstant();
			Entry<Double> score = detection(value, timestamp);
			if ( score.getItem()>baseThreshold && i>200 ) {
				scores.add(new Entry<Double>(score.getItem(), score.getInstant()));
			} else {
				scores.add(new Entry<Double>(0.0, score.getInstant()));
			}
		}
		return scores;
	}
	
	public List<Pair<Long, Long>> detectorSeriesAnomalySegment(DoubleSeries nSeries) {
		return detectorSeriesAnomalySegment(nSeries, 0.81);
	}
	
	public List<Pair<Long, Long>> detectorSeriesAnomalySegment(DoubleSeries nSeries, double baseThreshold) {
		if ( baseThreshold <= 0.3 )
			baseThreshold = 0.3;
		List<Pair<Long, Long>> res = new ArrayList<Pair<Long, Long>>();
		DoubleSeries seriesAnomalys = detectorSeries(nSeries);
		Long segmentLeftTimestamp = -1L;
		Long segmentRightTimestamp = -1L;
		for(int i = 0; i < seriesAnomalys.size(); i++) {
			double anormaly = seriesAnomalys.get(i).getItem();
			Long timestamp = seriesAnomalys.get(i).getInstant();
			if ( anormaly >= baseThreshold ) {
				if (segmentRightTimestamp == -1){
					segmentLeftTimestamp = timestamp;
					segmentRightTimestamp = timestamp;
				}
				else if ( seriesAnomalys.subSeries(i-windowsLen, i).max() >= baseThreshold) {
					segmentRightTimestamp = timestamp;
				}
				else {
					res.add(new Pair<Long, Long>(segmentLeftTimestamp, segmentRightTimestamp));
					segmentLeftTimestamp = timestamp;
					segmentRightTimestamp = timestamp;
				}
			}
		}
		if (segmentRightTimestamp != -1) {
			res.add(new Pair<Long, Long>(segmentLeftTimestamp, segmentRightTimestamp));
		}
		return res;
	}
}
