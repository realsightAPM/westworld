package com.realsight.westworld.tsp.lib.model.hashcode;

import java.util.ArrayList;
import java.util.List;

public class MazeHash implements EnvHash{
	/**
	 * @author √»
	 */
	
	private static final long serialVersionUID = -8674841172102011039L;

	@Override
	public List<Integer> env2SensFacts(List<Character> env, int radius) {
		// TODO Auto-generated method stub
		List<Integer> sensFacts = new ArrayList<Integer>();
		int t = 0;
		for (int i = 0; i < env.size(); i++) {
			if (!env.get(i).equals('#')) {
				t += (1<<i);
			}
		}
		sensFacts.add(t);
		return sensFacts;
	}
}
