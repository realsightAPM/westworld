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
	private static final int numBit = 4;
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
	
	private List<Integer> bit2SensFacts(int bit) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		for(int j = 0; bit>0 || j<numBit; bit>>=1, j++){
			if ( (bit&1) > 0 ) {
				currSensFacts.add(2*j+1);
			} else {
				currSensFacts.add(2*j);
			}
		}
		return currSensFacts;
	}
	
	public int getBit(double value) {
		int bit = (int) ((value-this.minValue)/this.minValueStep);
		return bit;
	}
	
	private double learn(List<Integer> currSensFacts, Long timestamp){
		return this.neuroGroup.learn(currSensFacts, timestamp);
	}
	
	private double predict(List<Integer> currSensFacts, Long timestamp){
		return this.neuroGroup.predict(currSensFacts, timestamp);
	}
	
	public Double detectorAnomaly(Double value, Long timestamp, boolean anormly) {
		int bit = getBit(value);
		List<Integer> currSensFacts = bit2SensFacts(bit);
		if (anormly) return predict(currSensFacts, timestamp);
		return learn(currSensFacts, timestamp);
	}
	
	public Double detectorAnomaly(Double value, Long timestamp) {
		int bit = getBit(value);
		List<Integer> currSensFacts = bit2SensFacts(bit);
		return learn(currSensFacts, timestamp);
	}
	
	public DoubleSeries detectorSeriesAnomaly(DoubleSeries nSeries, Long timestamp) {
		List<Entry<Double>> newEntries = new ArrayList<>();
		for(int i = 0; i < nSeries.size(); i += 1){
			int bit = getBit(nSeries.get(i).getItem());
			List<Integer> currSensFacts = bit2SensFacts(bit);
			newEntries.add(new Entry<Double>(learn(currSensFacts, timestamp), nSeries.get(i).getInstant()));
		}
		return new DoubleSeries(newEntries, "anormly");
	}
	
	public static AnormalyHierarchy build(DoubleSeries nSeries, double minValue, double maxValue){
		AnormalyHierarchy res = new AnormalyHierarchy(minValue, maxValue);
		if (nSeries != null) {
			for(int i = 0; i< nSeries.size(); i += 1){
				int bit = res.getBit(nSeries.get(i).getItem());
				List<Integer> currSensFacts = res.bit2SensFacts(bit);
				res.learn(currSensFacts, nSeries.get(i).getInstant());
			}
		}
		return res;
	}
}

