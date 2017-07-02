package com.realsight.westworld.bnanalysis.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.realsight.westworld.tsp.api.OnlineTimeseriesPredictionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries;
import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

import Jama.Matrix;
//import com.realsight.westworld.bnanalysis.basic;

public class TSPService {

	public TSPService() {
		
	}
	
	public Map<String, Double> getTSPService(String csv_file, int current) throws Exception {
		
		Map<String, Double> resMap = new HashMap<String, Double>();
		
		TimeseriesData in = new TimeseriesData(csv_file);
		MultipleDoubleSeries nSeries = in.getPropertyDoubleSeries().subSeries(0, current);
		OnlineTimeseriesPredictionAPI p = new OnlineTimeseriesPredictionAPI();
		
		System.out.println("时间序列分析的规模：" + nSeries.size());
		
		for (int i = 0; i < current; i++) {
			Matrix value = Util.toVec(nSeries.get(i).getItem().iterator());
			long timestamp = nSeries.get(i).getInstant();
			p.todayValue(value, timestamp);
		}
		
		for (int i = 0; i < 10; i++) {
			System.out.println(i+": ");
			p.train(nSeries);
		}
			
		Matrix t_value = p.prediction();
		if (t_value != null) {
			for (int i = 0; i < nSeries.getProperty_list().size(); i++) {
				resMap.put(nSeries.getProperty_list().get(i), t_value.get(i, 0));
			}
			return resMap;
		}
		return null;
//		Plot.plot("对比", real, pre);
	}
}