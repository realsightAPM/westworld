package com.realsight.westworld.tsp.lib.model.hashcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.util.Entry;
import com.realsight.westworld.tsp.lib.util.Util;

import Jama.Matrix;

public class SOMHash implements ValueHash{
	/**
	 * @author win7
	 */
	private static final long serialVersionUID = 7766603155832335122L;
	
	private List<Matrix> sensoryNeurons = new ArrayList<Matrix>();
	private List<Double> alphas = new ArrayList<Double>();
	private int[] rows;
	private Random rng = new Random();
	private double std = 1.0;
	
	public SOMHash(SpaceBiont biont, int dimension, int ...rows) throws Exception {
		switch(biont) {
			case CRAB:
				if (rows.length != 1) {
					throw new Exception("Expected row size is 1!");
				}
				initial(dimension, rows);
				break;
			default:
				throw new NoSuchMethodException(String.valueOf(biont));
		}
	}
	
	private void initial(int dimension, int ...rows) {
		this.rows = rows;
		int count = 1;
		for (int i = 0; i < rows.length; i++){
			count = count*rows[i];
		}
		for (int i = 0; i < count; i++) {
			double[][] A = new double[dimension][];
			for (int j = 0; j < A.length; j++) {
				A[j] = new double[1];
				A[j][0] = (this.rng.nextDouble())*8000;
			}
			this.sensoryNeurons.add(new Matrix(A));
			this.alphas.add(1.0);
		}
	}
	
	private int[] getAxes(int value) {
		int[] axes = new int[this.rows.length];
		for (int i = 0; i < rows.length; i++) {
			axes[i] = value%rows[i];
			value /= rows[i];
		}
		try{
			Util.check(value == 0);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return axes;
	}
	
	private double G(double x) {
		double g =  Math.exp(-1.0*x*x/(2.0*std*std));
		return g;
	}
	
	public void train(Matrix value, int id) {
		int[] axes = getAxes(id);
		Iterator<Entry<Integer, Integer>> neighbor = getNeighborIterator(axes);
		double alpha = this.alphas.get(id);
		this.alphas.set(id, alpha+1.0);
		while(neighbor.hasNext()) {
			Entry<Integer, Integer> entry = neighbor.next();
			int dist = entry.getFirst();
			int t_id = entry.getSecond();
			double w = G(dist)/alpha;
			Matrix sm = this.sensoryNeurons.get(t_id);
			sm = value.minus(sm).times(w).plus(sm);
			this.sensoryNeurons.set(t_id, sm);
		}
	}
	
	public double matchingOptimalDistence(Matrix value, boolean flag) {
		int optimal_id = matchingOptimalId(value, false);
		double dist = value.minus(this.sensoryNeurons.get(optimal_id)).norm2();
		if (flag) {
			train(value, optimal_id);
		}
		return dist;
	}
	
	public int matchingOptimalId(Matrix value, boolean flag) {
		int optimal_id = 0;
		double optimal_dist = Double.POSITIVE_INFINITY;
		for (int id = 0; id < this.sensoryNeurons.size(); id++) {
			double dist = value.minus(this.sensoryNeurons.get(id)).norm2();
			if (optimal_dist > dist) {
				optimal_id = id;
				optimal_dist = dist;
			}
		}
//		System.out.println(optimal_dist);
		if (flag) {
			train(value, optimal_id);
		}
		return optimal_id;
	}
	
	public List<Integer> value2SensFacts(Matrix value, int radius) {
		List<Integer> currSensFacts = new ArrayList<Integer>();
		int optimal_id = matchingOptimalId(value, true);
		int[] axes = getAxes(optimal_id);
		Iterator<Entry<Integer, Integer>> neighbor = getNeighborIterator(axes);
		while(neighbor.hasNext()) {
			Entry<Integer, Integer> entry = neighbor.next();
			int dist = entry.getFirst();
			int t_id = entry.getSecond();
			if (dist > radius) continue;
			currSensFacts.add(t_id);
		}
		Collections.sort(currSensFacts);
		return currSensFacts;
	}
	
	public Iterator<Entry<Integer, Integer>> getNeighborIterator(int ...axes) {
		int count = 1;
		for (int i = 0; i < rows.length; i++){
			count = count*rows[i];
		}
		List<Entry<Integer, Integer>> array = new ArrayList<Entry<Integer, Integer>>();
		for (int num = 0; num < count; num++) {
			int key = 0;
			int value = num;
			for (int i = 0; i < rows.length; i++) {
				key += Math.abs(value%rows[i] - axes[i]);
				value /= rows[i];
			}
			array.add(new Entry<Integer, Integer>(key, num));
		}
		return array.iterator();
	}
	
}
