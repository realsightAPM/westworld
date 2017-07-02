package com.realsight.westworld.tsp.lib.model.hashcode;

import java.io.Serializable;
import java.util.List;

public interface EnvHash extends Serializable {
	public List<Integer> env2SensFacts(List<Character> env, int radius);
}
