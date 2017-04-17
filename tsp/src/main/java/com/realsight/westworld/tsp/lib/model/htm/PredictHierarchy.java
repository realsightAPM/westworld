package com.realsight.westworld.tsp.lib.model.htm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.model.htm.neurongroups.*;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.util.Pair;
import com.realsight.westworld.tsp.lib.util.Util;

import Jama.Matrix;

public class PredictHierarchy implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 973860126368457409L;
	
	private int maxLeftSemiContextsLenght = 17;
	private int maxActiveNeuronsNum = 35;
	private int maxRemeberNeuronsNum = 1125;
	private int numBit = 50;
	private int radius = 2; // (0,0.5)
	private List<Matrix> sensing = new ArrayList<Matrix>();
	private NeuroGroup neuroGroup = null;
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

	public List<Matrix> getSensing() {
		return sensing;
	}

	public void setSensing(List<Matrix> sensing) {
		this.sensing = sensing;
	}

	public NeuroGroup getNeuroGroup() {
		return neuroGroup;
	}

	public void setNeuroGroup(NeuroGroup neuroGroup) {
		this.neuroGroup = neuroGroup;
	}

	private PredictHierarchy(MultipleDoubleSeries series) {
		this.sensing = unique(series.getTData(), numBit);
		this.neuroGroup = new NeuroGroup(maxActiveNeuronsNum, maxLeftSemiContextsLenght, maxRemeberNeuronsNum);
	}
	
	private List<Matrix> unique(List<Matrix> data, int num) {
		List<Matrix> res = new ArrayList<Matrix>();
		if (data.size() < 1)
			return res;
		int row = data.get(0).getRowDimension();
		int col = data.get(0).getColumnDimension();
		
		for (int it = 0; it < 100; it++) {
			while(res.size() < num) {
				int r = rng.nextInt(data.size());
				Matrix mr = data.get(r);
				res.add(mr);
			}
			int[] sz = new int[num];
			Matrix[] center = new Matrix[num];
			for (int i = 0; i < num; i++) {
				center[i] = new Matrix(row, col);
			}
			for (int i = 0; i < data.size(); i++){
				int id = -1;
				double min_dist = Double.MAX_VALUE;
				for (int j = 0; j < res.size(); j++) {
					double t_dist = res.get(j).minus(data.get(i)).norm2();
					if (min_dist > t_dist) {
						min_dist = t_dist;
						id = j;
					}
				}
				sz[id] += 1;
				center[id].plusEquals(data.get(i));
			}
			res.clear();
			for (int i = 0; i < num; i++) {
				if (sz[i] == 0) continue;
				center[i].timesEquals(1.0/sz[i]);
				res.add(center[i]);
			}
		}
		return res;
	}
	
	private List<Integer> value2SensFacts(Matrix value) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		List<Pair<Double, Integer>> ss = new ArrayList<Pair<Double, Integer>>();
		for ( int i = 0; i < this.sensing.size(); i++) {
			Matrix x = this.sensing.get(i).minus(value);
//			this.sensing.get(i).print(2, 3);
//			System.out.println(x.norm2());
			Pair<Double, Integer> p = new Pair<Double, Integer>(x.norm2(), i);
			ss.add(p);
		}
		Collections.sort(ss);
		
		for(int i = 0; i < Math.min(radius, ss.size()); i++) {
			currSensFacts.add(ss.get(i).getB());
		}
		return currSensFacts;
	}
	
	public double learn(Matrix value){
		List<Integer> currSensFacts = value2SensFacts(value);
		return this.neuroGroup.learn(currSensFacts);
	}
	
	public double predict(Matrix value) {
		List<Integer> sensFacts = value2SensFacts(value);
		return this.neuroGroup.predict(sensFacts);
	}
	
	public double predict_learn(Matrix value){
		List<Integer> currSensFacts = value2SensFacts(value);
		return this.neuroGroup.predict_learn(currSensFacts);
	}
	
	public void sleep() {
		this.neuroGroup.sleep();
	}
	
	public static PredictHierarchy build(MultipleDoubleSeries nSeries){
		return build(nSeries, false);
	}
	
	public static PredictHierarchy build(MultipleDoubleSeries nSeries, boolean flag){
		PredictHierarchy res = new PredictHierarchy(nSeries);
		if (flag) {
			for (int i = 0; i < nSeries.size(); i++) {
				Matrix value = Util.toVec(nSeries.get(i).getItem().iterator());
				res.learn(value);
			}
		}
		return res;
	}
}

