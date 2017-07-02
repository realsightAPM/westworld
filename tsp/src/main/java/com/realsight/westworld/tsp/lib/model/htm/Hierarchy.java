package com.realsight.westworld.tsp.lib.model.htm;

import java.io.Serializable;
import java.util.List;

import com.realsight.westworld.tsp.lib.model.htm.neurongroups.*;

import Jama.Matrix;

public abstract class Hierarchy implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 973860126368457409L;
	
	protected NeuroGroup neuroGroup = null;
	
	public NeuroGroup getNeuroGroup() {
		return neuroGroup;
	}

	public void setNeuroGroup(NeuroGroup neuroGroup) {
		this.neuroGroup = neuroGroup;
	}

	protected Hierarchy() {}
	
	protected abstract List<Integer> value2SensFacts(Matrix value);
	
	public double learn(Matrix value){
		List<Integer> sensFacts = value2SensFacts(value);
		return this.neuroGroup.learnSeries(sensFacts);
	}
	
	public double predict(Matrix value) {
		List<Integer> sensFacts = value2SensFacts(value);
		return this.neuroGroup.predict(sensFacts);
	}
	
	public void sleep() {
		this.neuroGroup.sleep();
	}
}

