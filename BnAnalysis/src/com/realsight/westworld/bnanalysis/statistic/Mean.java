package com.realsight.westworld.bnanalysis.statistic;

import java.util.List;

import com.realsight.westworld.bnanalysis.Dao.Statistic;

public class Mean implements Statistic{

	public Mean() {}

	@Override
	public double run(List<Double> array) {
		if (array != null && array.size() > 0) {
			int len = array.size();
			double x = 0;
			for (Double it : array) {
				x += it;
			}
			return x/len;
		} else {
			return 0;
		}
	}
}
