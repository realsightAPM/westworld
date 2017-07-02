package com.realsight.westworld.tsp.lib.model.htm;

import java.io.Serializable;
import java.util.List;

import com.realsight.westworld.tsp.lib.model.hashcode.ScalarHash;
import com.realsight.westworld.tsp.lib.model.htm.neurongroups.NeuroGroup;

import Jama.Matrix;

public class StrategyHierarchy extends Hierarchy implements Serializable{

	/**
	 * @author √»
	 */
	
	private static final long serialVersionUID = -7202226048247013910L;
	
	private int maxLeftSemiContextsLenght = 17;
	private int maxActiveNeuronsNum = 25;
	private int maxRemeberNeuronsNum = 10000;
	private int maxValidActiveNeural = 30;
	private int radius = 5;
	private int numBit = 32;
	private ScalarHash hash = null;
	
	public StrategyHierarchy(Double minValue, Double maxValue, List<String> actions) throws Exception {
		super.neuroGroup = new NeuroGroup(maxActiveNeuronsNum, 
				maxLeftSemiContextsLenght, 
				maxRemeberNeuronsNum, 
				maxValidActiveNeural, 
				true, 
				actions);
		this.hash = new ScalarHash(minValue, maxValue, numBit);
	}
	
	@Override
	protected List<Integer> value2SensFacts(Matrix value) {
		// TODO Auto-generated method stub
		return hash.value2SensFacts(value, radius);
	}
	
	public String action() {
		return super.neuroGroup.action();
	}
	
	public void learnAction(String currAction, Double reward) {
		super.neuroGroup.learnActions(currAction, reward);
	}
}

