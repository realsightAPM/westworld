package com.realsight.westworld.tsp.lib.model.htm;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.realsight.westworld.tsp.lib.model.hashcode.MazeHash;
import com.realsight.westworld.tsp.lib.model.htm.neurongroups.NeuroGroup;

import Jama.Matrix;

public class DogHierarchy extends Hierarchy implements Serializable{

	/**
	 * @author √»
	 */
	
	private static final long serialVersionUID = -7202226048247013910L;
	
	private int maxLeftSemiContextsLenght = 17;
	private int maxActiveNeuronsNum = 25;
	private int maxRemeberNeuronsNum = 10000;
	private int maxValidActiveNeural = 30;
	private MazeHash hash = null;
	
	public DogHierarchy(List<String> actions) throws Exception {
		super.neuroGroup = new NeuroGroup(maxActiveNeuronsNum, 
				maxLeftSemiContextsLenght, 
				maxRemeberNeuronsNum, 
				maxValidActiveNeural, 
				true, 
				actions);
		this.hash = new MazeHash();
	}
	
	public double learn(List<Character> env){
		List<Integer> sensFacts = this.hash.env2SensFacts(env, -1);
		return this.neuroGroup.learnSeries(sensFacts);
	}
	
	public String action() {
		return super.neuroGroup.action();
	}
	
	public void learnAction(String currAction, Double reward) {
		super.neuroGroup.learnActions(currAction, reward);
	}

	@Override
	protected List<Integer> value2SensFacts(Matrix value) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}
}