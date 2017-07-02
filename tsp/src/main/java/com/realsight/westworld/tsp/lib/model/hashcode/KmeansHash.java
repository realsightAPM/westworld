package com.realsight.westworld.tsp.lib.model.hashcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.util.Entry;

import Jama.Matrix;

public class KmeansHash implements ValueHash{
	/**
	 * @author √»
	 */
	
	private static final long serialVersionUID = -8674841172102011039L;
	
	private List<Matrix> sensoryNeurons = new ArrayList<Matrix>();
	private Random rng = new Random();
	private static final int iter_num = 30;
	
	public KmeansHash(List<Matrix> data, int k) throws Exception {
		System.out.println("kmeans start...");
		for (int iter = 0; iter < iter_num; iter += 1) {
			while(sensoryNeurons.size() < k) {
				int r = rng.nextInt(data.size());
				sensoryNeurons.add(data.get(r).copy());
			}
			int[] opt_match = new int[data.size()];
			double error = 0.0;
			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < sensoryNeurons.size(); j++) {
					if (data.get(i).minus(sensoryNeurons.get(j)).norm2() <
							data.get(i).minus(sensoryNeurons.get(opt_match[i])).norm2()) {
						opt_match[i] = j;
					}
				}
				error += data.get(i).minus(sensoryNeurons.get(opt_match[i])).norm2();
			}
			List<Matrix> tmp_sensoryNeurons = new ArrayList<Matrix>();
			for (int j = 0; j < sensoryNeurons.size(); j++){
				Matrix tmp = new Matrix(sensoryNeurons.get(j).getRowDimension(), 
						sensoryNeurons.get(j).getColumnDimension());
				int sz = 0;
				for (int i = 0; i < data.size(); i++) {
					if (opt_match[i] != j) continue;
					tmp.plusEquals(data.get(i));
					sz += 1;
				}
				if (sz == 0) continue;
				tmp_sensoryNeurons.add(tmp.times(1.0/sz));
			}
			sensoryNeurons = tmp_sensoryNeurons;
			System.out.println("kmeans iter number is " + iter + ", sensory number is " + sensoryNeurons.size() + 
					", error is " + error);
		}
		System.out.println("kmeans end.");
	}

	public List<Integer> value2SensFacts(Matrix value, int radius) {
		// TODO Auto-generated method stub
		List<Entry<Double, Integer>> tmp = new ArrayList<Entry<Double, Integer>>();
		for (int i = 0; i < this.sensoryNeurons.size(); i++) {
			tmp.add(new Entry<Double, Integer>(this.sensoryNeurons.get(i).minus(value).norm2(), i));
		}
		Collections.sort(tmp);
		List<Integer> currSensFacts = new ArrayList<Integer>();
		for (int i = 0; i < Math.min(tmp.size(), radius); i++) {
			currSensFacts.add(tmp.get(i).getSecond());
		}
//		System.out.println(currSensFacts);
		return currSensFacts;
	}
	
}
