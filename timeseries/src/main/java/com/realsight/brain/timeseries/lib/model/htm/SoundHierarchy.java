package com.realsight.brain.timeseries.lib.model.htm;

import java.util.ArrayList;
import java.util.List;

import com.realsight.brain.timeseries.lib.model.htm.neurongroups.*;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;

public class SoundHierarchy {
	
	private double minValue;
	private double maxValue;
	private static final double threshold = 0.15;
	private static final int maxLeftSemiContextsLenght = 11;
	private static final int maxActiveNeuronsNum = 5;
	private static final int numBit = 8;
	private static final int numFact = 5;
	private double fullValueRange;
	private double minValueStep;
	private NeuroGroup neuroGroup = null;
	
	private SoundHierarchy(double minValue, double maxValue) {
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
	private double learn(List<Integer> currSensFacts){
		return this.neuroGroup.learn(currSensFacts);
	}
	private double predict(List<Integer> currSensFacts) {
		double p = this.neuroGroup.predict(currSensFacts);
		if ( threshold < p ) return numFact;
		return 0.0;
	}
	public void train(DoubleSeries sound) {
		for(int i = 0; i < sound.size(); i += numFact){
			List<Integer> currSensFacts = new ArrayList<Integer>();
			for(int j = 0; j<numFact && j+i<sound.size(); j++){
				int bit = (int) ((sound.get(i+j).getItem()-this.minValue)/minValueStep);
				currSensFacts.add(bit);
			}
			learn(currSensFacts);
		}
	}
	
	public double test(DoubleSeries sound) {
		this.neuroGroup.sleep();
		double res = 0.0;
		for(int i = 0; i < sound.size(); i += numFact){
			List<Integer> currSensFacts = new ArrayList<Integer>();
			for(int j = 0; j<numFact && j+i<sound.size(); j++){
				int bit = (int) ((sound.get(i+j).getItem()-this.minValue)/minValueStep);
				currSensFacts.add(bit);
//				System.out.print(bit+",");
			}
//			System.out.print("\n");
			res += predict(currSensFacts);
		}
		
		return res/sound.size();
	}
	
	public static SoundHierarchy build(DoubleSeries sound){
		if ( sound==null || sound.size()==0 )
			return null;
		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;
		for(int i = 0; i < sound.size(); i++){
			minValue = Math.min(minValue, sound.get(i).getItem());
			maxValue = Math.max(maxValue, sound.get(i).getItem());
		}
		return new SoundHierarchy(minValue, maxValue);
	}
	
	public static SoundHierarchy build(double minValue, double maxValue){
		SoundHierarchy h = new SoundHierarchy(minValue, maxValue);
		return h;
	}
}

