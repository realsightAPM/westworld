package com.realsight.brain.timeseries.lib.model.htm;

import java.util.ArrayList;
import java.util.List;

import com.realsight.brain.timeseries.lib.model.htm.neurongroups.*;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

public class AnormalyHierarchy {
	
	private double minValue;
	private double maxValue;
	private static final int maxLeftSemiContextsLenght = 7;
	private static final int maxActiveNeuronsNum = 15;
	private static final int numBit = 3;
//	private static final int numFact = 5;
	private double fullValueRange;
	private double minValueStep;
	private NeuroGroup neuroGroup = null;
	
	private AnormalyHierarchy(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.fullValueRange = this.maxValue - this.minValue;
		int numNormValue = (1<<numBit) - 1;
        if ( this.fullValueRange == 0.0 ) {
        	this.fullValueRange = numNormValue;
        }
		this.minValueStep = this.fullValueRange / numNormValue;
		this.neuroGroup = new NeuroGroup(maxActiveNeuronsNum, maxLeftSemiContextsLenght);
	}
	
	public int getBit(double value) {
		int bit = (int) ((value-this.minValue)/this.minValueStep);
		return bit;
	}
	
	private double learn(List<Integer> currSensFacts){
		return this.neuroGroup.learn(currSensFacts);
	}
	
	private double predict(List<Integer> currSensFacts){
		return this.neuroGroup.predict(currSensFacts);
	}
	
	public Double detectorAnomaly(Double value, Long timestamp, boolean anormly) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		int bit = getBit(value);
		for(int j = 0; j < numBit; j++){
			if ( (bit&(1<<j)) > 0 ) {
				currSensFacts.add(2*j+1);
			} else {
				currSensFacts.add(2*j);
			}
		}
		if (anormly) return predict(currSensFacts);
		return learn(currSensFacts);
	}
	
	public Double detectorAnomaly(Double value, Long timestamp) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		int bit = getBit(value);
		for(int j = 0; j < numBit; j++){
			if ( (bit&(1<<j)) > 0 ) {
				currSensFacts.add(2*j+1);
			} else {
				currSensFacts.add(2*j);
			}
		}
		return learn(currSensFacts);
	}
	
	public DoubleSeries detectorSeriesAnomaly(DoubleSeries nSeries) {
		List<Entry<Double>> newEntries = new ArrayList<>();
		for(int i = 0; i < nSeries.size(); i += 1){
			List<Integer> currSensFacts = new ArrayList<Integer>();
			int bit = getBit(nSeries.get(i).getItem());
			for(int j = 0; j < numBit; j++){
				if ( (bit&(1<<j)) > 0 ) {
					currSensFacts.add(2*j+1);
				} else {
					currSensFacts.add(2*j);
				}
			}
			newEntries.add(new Entry<Double>(learn(currSensFacts), nSeries.get(i).getInstant()));
		}
		return new DoubleSeries(newEntries, "anormly");
	}
	
	public static AnormalyHierarchy build(DoubleSeries nSeries, double minValue, double maxValue){
		AnormalyHierarchy res = new AnormalyHierarchy(minValue, maxValue);
		if (nSeries != null) {
			for(int i = 0; i< nSeries.size(); i += 1){
				List<Integer> currSensFacts = new ArrayList<Integer>();
				int bit = res.getBit(nSeries.get(i).getItem());
				for(int j = 0; j < numBit; j++){
					if ( (bit&(1<<j)) > 0 ) {
						currSensFacts.add(2*j+1);
					} else {
						currSensFacts.add(2*j);
					}
				}
				res.learn(currSensFacts);
			}
		}
		return res;
	}
}

