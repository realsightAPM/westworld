package com.realsight.westworld.tsp.lib.model.hashcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.realsight.westworld.tsp.lib.util.Entry;

import Jama.Matrix;

public class ScalarHash implements ValueHash{
	/**
	 * @author √»
	 */
	
	private static final long serialVersionUID = -8674841172102011039L;
	
	private List<Double> sensoryNeurons = new ArrayList<Double>();
	
	public ScalarHash(Double minValue, Double maxValue, int nBit) throws Exception {
		Double r = (maxValue - minValue) / nBit;
		for (int i = 0; i < nBit; i++) {
			this.sensoryNeurons.add(minValue + r*(0.5+i));
		}
	}
	
	
	public List<Integer> value2SensFacts(Double value, int radius) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		List<Entry<Double, Integer>> s = new ArrayList<Entry<Double, Integer>>();
		for (int i = 0; i < this.sensoryNeurons.size(); i++) {
			s.add(new Entry<Double, Integer>(Math.abs(this.sensoryNeurons.get(i)-value), i));
		}
		Collections.sort(s);
		for (int i = 0; i < radius && i < s.size(); i++) {
			currSensFacts.add(s.get(i).getSecond());
		}
		return currSensFacts;
	}


	public List<Integer> value2SensFacts(Matrix value, int radius) {
		// TODO Auto-generated method stub
		return value2SensFacts(value.get(0, 0), radius);
	}
	
}
