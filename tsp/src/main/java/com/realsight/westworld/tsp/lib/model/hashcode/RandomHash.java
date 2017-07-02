package com.realsight.westworld.tsp.lib.model.hashcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.util.Entry;

import Jama.Matrix;

public class RandomHash implements ValueHash{
	/**
	 * @author √»
	 */
	
	private static final long serialVersionUID = -8674841172102011039L;
	
	private List<Matrix> sensoryNeurons = new ArrayList<Matrix>();
	private Random rng = new Random();
	
	public RandomHash(List<Matrix> data, int k) throws Exception {
		while(sensoryNeurons.size() < k) {
			int r = rng.nextInt(data.size());
			sensoryNeurons.add(data.get(r).copy());
		}
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
