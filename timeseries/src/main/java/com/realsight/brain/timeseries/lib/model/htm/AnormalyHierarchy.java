package com.realsight.brain.timeseries.lib.model.htm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.realsight.brain.timeseries.lib.model.htm.neurongroups.*;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.Pair;

public class AnormalyHierarchy {

	private final int maxLeftSemiContextsLenght = 27;
	private final int maxActiveNeuronsNum = 25;
	private final int numBit = 32;
	private final double radius = 0.150; // (0,0.5)
	private List<Pair<Double, Double>> sensing = new ArrayList<Pair<Double, Double>>();
	private NeuroGroup neuroGroup = null;
	private final double eps = 1e-5;
	
	private AnormalyHierarchy(double minValue, double maxValue) {
		double length = maxValue - minValue;
		if ( length < eps ) length = 1.0;
		double stepLength = length / this.numBit;
		for ( double x = minValue; x < length; x += stepLength ) {
			Double L = x - length*radius;
			Double R = x + length*radius;
			this.sensing.add(new Pair<Double, Double>(L, R));
		}
		this.neuroGroup = new NeuroGroup(maxActiveNeuronsNum, maxLeftSemiContextsLenght);
	}
	
	private AnormalyHierarchy(DoubleSeries series) {
		Random rng = new Random(2234);
		List<Double> data = series.getTData();
		Collections.sort(data);
		int n = data.size() - 1;
		while(this.sensing.size() < this.numBit) {
			int rn = rng.nextInt(data.size());
			Double R = data.get(n);
			Double L = data.get(0);
			if (rn + n*radius <= n) R = data.get((int) (rn + n*radius));
			if (rn - n*radius >= 0) L = data.get((int) (rn - n*radius));
			this.sensing.add(new Pair<Double, Double>(L, R));
		}
		
		this.neuroGroup = new NeuroGroup(maxActiveNeuronsNum, maxLeftSemiContextsLenght);
	}
	
	private List<Integer> value2SensFacts(double value) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		for ( int i = 0; i < this.sensing.size(); i++) {
			Pair<Double, Double> p = this.sensing.get(i);
			if (value>p.getA() && value<p.getB()) 
				currSensFacts.add(i);
		}
//		System.out.println(currSensFacts.size());
		return currSensFacts;
	}
	
	private double learn(List<Integer> currSensFacts, Long timestamp){
		return this.neuroGroup.learn(currSensFacts, timestamp);
	}
	
	public double predict(Long timestamp, double value) {
		List<Integer> sensFacts = value2SensFacts(value);
		return this.neuroGroup.predict(sensFacts, timestamp);
	}
	
	public Double detectorAnomaly(Double value, Long timestamp, boolean anormly) {
		List<Integer> currSensFacts = value2SensFacts(value);
		if (anormly) return learn(currSensFacts, timestamp);
		return learn(currSensFacts, timestamp);
	}
	
	public Double detectorAnomaly(Double value, Long timestamp) {
		List<Integer> currSensFacts = value2SensFacts(value);
		return learn(currSensFacts, timestamp);
	}
		
	public DoubleSeries detectorSeriesAnomaly(DoubleSeries nSeries, Long timestamp) {
		List<Entry<Double>> newEntries = new ArrayList<>();
		for(int i = 0; i < nSeries.size(); i += 1){
			List<Integer> currSensFacts = value2SensFacts(nSeries.get(i).getItem());
			newEntries.add(new Entry<Double>(learn(currSensFacts, timestamp), nSeries.get(i).getInstant()));
		}
		return new DoubleSeries(newEntries, "anormly");
	}
	
	public static AnormalyHierarchy build(DoubleSeries nSeries, double minValue, double maxValue){
		AnormalyHierarchy res = new AnormalyHierarchy(minValue, maxValue);
		if (nSeries != null) {
			for(int i = 0; i< nSeries.size(); i += 1){
				List<Integer> currSensFacts = res.value2SensFacts(nSeries.get(i).getItem());
				res.learn(currSensFacts, nSeries.get(i).getInstant());
			}
		}
		return res;
	}
	
	public static AnormalyHierarchy build(DoubleSeries nSeries){
		AnormalyHierarchy res = new AnormalyHierarchy(nSeries);
		return res;
	}
}

