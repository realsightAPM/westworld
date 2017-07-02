package com.realsight.westworld.tsp.api;

import com.realsight.westworld.tsp.lib.model.VideoCut;
import com.realsight.westworld.tsp.lib.model.htm.VideoHierarchy;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

import Jama.Matrix;

public class VideoCutAPI extends VideoCut{

	/**
	 * @author √»
	 */
	private static final long serialVersionUID = -7135430791491079092L;

	public VideoCutAPI(MultipleDoubleSeries mSeries) {
		super();
		try {
			super.HTM = new VideoHierarchy(mSeries);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Entry<Double> detection(Matrix value, Long timestamp) {
		// TODO Auto-generated method stub
		if (super.HTM != null) {
			double score = super.HTM.learn(value);
			return new Entry<Double>(score, timestamp);
		}
		return null;
	}
	
	@Override
	public void sleep() {
		// TODO Auto-generated method stub
		super.HTM.sleep();
	}
}
