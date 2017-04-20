package com.realsight.westworld.tsp.lib.model.htm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.model.htm.neurongroups.NeuroGroup;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

import Jama.Matrix;

public class AnormalyHierarchy extends Hierarchy implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 973860126368457409L;

	private int maxLeftSemiContextsLenght = 27;
	private int maxActiveNeuronsNum = 15;
	private int maxRemeberNeuronsNum = 1155;
	private double min_value;
	private double max_value;
	private double scope;
	private int radius = 3;
	private int numBit = 1<<radius;
	private Random rng = new Random(542);

	public int getNumBit() {
		return numBit;
	}

	public int getRadius() {
		return radius;
	}

	public Random getRng() {
		return rng;
	}

	public void setNumBit(int numBit) {
		this.numBit = numBit;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setRng(Random rng) {
		this.rng = rng;
	}

	private AnormalyHierarchy(DoubleSeries series) {
		this.neuroGroup = new NeuroGroup(maxActiveNeuronsNum, maxLeftSemiContextsLenght, maxRemeberNeuronsNum);
		this.min_value = series.min();
		this.max_value = series.max();
		this.scope = (max_value - min_value)/(this.numBit-1);
		if (this.scope < 1e-5) this.scope = 1.0;
	}
	
	protected List<Integer> value2SensFacts(Matrix value) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		int x = (int) ((value.get(0, 0)-this.min_value)/this.scope);
		for (int i = 0; i<this.radius || x>0; i++) {
			if ((x&1) > 0) currSensFacts.add(i*2+1);
			else currSensFacts.add(i*2);
			x >>= 1;
		}
		return currSensFacts;
	}
	
	public static AnormalyHierarchy build(DoubleSeries nSeries){
		return build(nSeries, false);
	}
	
	public static AnormalyHierarchy build(DoubleSeries nSeries, boolean flag){
		AnormalyHierarchy res = new AnormalyHierarchy(nSeries);
		if (flag){
			for(int i = 0; i < nSeries.size(); i++) {
				Entry<Double> entry = nSeries.get(i);
				res.learn(new Matrix(1, 1, entry.getItem()));
			}
		}
		return res;
	}
}

