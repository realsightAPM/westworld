package com.realsight.westworld.tsp.lib.model.htm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.realsight.westworld.tsp.lib.model.hashcode.KmeansHash;
import com.realsight.westworld.tsp.lib.model.hashcode.LSHash;
import com.realsight.westworld.tsp.lib.model.hashcode.RandomHash;
import com.realsight.westworld.tsp.lib.model.htm.neurongroups.NeuroGroup;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.util.Util;

import Jama.Matrix;

@SuppressWarnings("unused")
public class VideoHierarchy extends Hierarchy implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 973860126368457409L;

	private int maxLeftSemiContextsLenght = 17;
	private int maxActiveNeuronsNum = 25;
	private int maxRemeberNeuronsNum = 1155;
	private int maxValidActiveNeural = 30;
	private int numBit = 16;
	private int radius = 3;
	private LSHash hash = null;
	private int[] rows = {30};

	public int getRadius() {
		return radius;
	}

	public VideoHierarchy(MultipleDoubleSeries mSeries) throws Exception {
		this.neuroGroup = new NeuroGroup(
				maxActiveNeuronsNum, maxLeftSemiContextsLenght, maxRemeberNeuronsNum, maxValidActiveNeural);
		List<Matrix> data = new ArrayList<Matrix>();
		for (int i = 0; i < mSeries.size(); i++) {
			Matrix m = Util.toVec(mSeries.get(i).getItem().iterator());
			data.add(m);
		}
		this.hash = new LSHash(data.get(0).getColumnDimension(), numBit);
	}
	
	protected List<Integer> value2SensFacts(Matrix value) {
		return hash.value2SensFacts(value, radius);
	}
	
	public double learn(Matrix value){
		List<Integer> currSensFacts = this.hash.value2SensFacts(value, radius);
		return this.neuroGroup.learnSeries(currSensFacts);
	}
	
	public void sleep() {
		this.neuroGroup.sleep();
	}
}

