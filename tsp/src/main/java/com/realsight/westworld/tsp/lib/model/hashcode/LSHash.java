package com.realsight.westworld.tsp.lib.model.hashcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Jama.Matrix;

public class LSHash implements ValueHash{
	/**
	 * @author √»
	 */
	private static final long serialVersionUID = -8077654873607093205L;
	private Matrix proMatrix = null;
	
	public LSHash(int originalD, int distinationD) {
		Random rng = new Random();
		double[][] tProMatrix = new double[originalD][distinationD];
		for (int i = 0; i < originalD; i++) {
			tProMatrix[i] = new double[distinationD];
			for (int j = 0; j < distinationD; j++) {
				tProMatrix[i][j] = rng.nextGaussian();
			}
		}
		this.proMatrix = new Matrix(tProMatrix);
	}

	@Override
	public List<Integer> value2SensFacts(Matrix value, int radius) {
		// TODO Auto-generated method stub
		System.out.println(value.getColumnDimension() + " " + value.getRowDimension());
		System.out.println(proMatrix.getColumnDimension() + " " + proMatrix.getRowDimension());
		Matrix tmp = proMatrix.transpose().times(value);
		List<Integer> currFacts = new ArrayList<Integer>();
		for (int i = 0; i < tmp.getColumnDimension(); i++) {
			if (tmp.get(i, 0) > 0) currFacts.add(i);
		}
		return currFacts;
	}
	
}
