package com.realsight.westworld.tsp.lib.model.hashcode;

import java.io.Serializable;
import java.util.List;

import Jama.Matrix;

public interface ValueHash extends Serializable {
	public List<Integer> value2SensFacts(Matrix value, int radius);
}
