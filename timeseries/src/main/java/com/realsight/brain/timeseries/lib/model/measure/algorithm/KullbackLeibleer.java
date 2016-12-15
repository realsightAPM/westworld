package com.realsight.brain.timeseries.lib.model.measure.algorithm;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;

public class KullbackLeibleer {
	public static double distance(DoubleSeries lSeries, DoubleSeries rSeries, int numBit) {
		double res = 0.0, sumP = 0.0, sumQ = 0;
		double[] p = lSeries.toBitArray(numBit);
		double[] q = rSeries.toBitArray(numBit);
		for(int i = 0; i < p.length; i++){
			p[i] += 0.1;
			sumP += p[i];
		}
		for(int i = 0; i < q.length; i++){
			sumQ += q[i];
		}
		for(int i = 0; i < q.length; i++) {
			if(q[i] == 0) continue;
			res += q[i]*Math.log(q[i]/sumQ);
			res -= q[i]*Math.log(p[i]/sumP);
		}
		return res;
	}
}
