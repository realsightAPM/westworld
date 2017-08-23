package com.realsight.westworld.bnanalysis.algorithm;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.realsight.westworld.bnanalysis.basic.Pair;

public class SortPairList {

	public SortPairList() {}
	
	public void sort(List<Pair<String, Double>> pairList) {
		Collections.sort(pairList, new Comparator<Pair<String, Double>>() {
			public int compare(Pair<String, Double> x, Pair<String, Double> y) {
				return y.second - x.second > 0 ? 1 : -1;
			}
		});
	}
}
