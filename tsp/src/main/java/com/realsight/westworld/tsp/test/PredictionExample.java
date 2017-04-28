package com.realsight.westworld.tsp.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.realsight.westworld.tsp.api.OnlineTimeseriesPredictionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries;
import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.data.VolumeData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

import Jama.Matrix;

/**
 * @author Sun Muxin
 * 
 */ 
public class PredictionExample {
	
	public void main1() throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		Path trainPath = Paths.get(root, "data", "volume");
		Path testPath = Paths.get(root, "data", "test");
		String[] regexs = new String[]{"1_0_am", "1_0_pm", "1_1_am", "1_1_pm", 
				"2_0_am", "2_0_pm", "3_0_am", "3_0_pm", "3_1_am", "3_1_pm"};
		double sum = 0;
		for (String regex : regexs) {
//			System.err.print(regex + " ");
			OnlineTimeseriesPredictionAPI p = new OnlineTimeseriesPredictionAPI();
			List<MultipleDoubleSeries> train = new ArrayList<MultipleDoubleSeries>();
			List<DoubleSeries> plot = new ArrayList<DoubleSeries>();
			for (File file : trainPath.toFile().listFiles()) {
				if (!file.getName().contains(regex)) continue;
				VolumeData vd = new VolumeData(file.getAbsolutePath());
				DoubleSeries series = vd.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
				MultipleDoubleSeries mSeries = new MultipleDoubleSeries(file.getName(), series);
				if (series.size() != 12)
					continue;
				train.add(mSeries);
				plot.add(series);	
			}
//			Plot.plot(regex, plot);
			int len = train.size();
			int train_len = (int) (len*0.8);
			List<MultipleDoubleSeries> train_data = new ArrayList<MultipleDoubleSeries>();
			List<MultipleDoubleSeries> test_data = new ArrayList<MultipleDoubleSeries>();
			for (int i = 0; i < train_len; i++){
				train_data.add(train.get(i));
			}
			for (int i = train_len; i < len; i++) {
				test_data.add(train.get(i));
			}
			for (int iter = 0; iter < 30; iter ++) {
				p.train(train);
//				p.test(test_data);
			}
//			sum += p.test(test_data);
			for (File file : testPath.toFile().listFiles()) {
				if (!file.getName().contains(regex)) continue;
				VolumeData vd = new VolumeData(file.getAbsolutePath());
				DoubleSeries series = vd.getPropertySeries("volume", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS"));
				MultipleDoubleSeries mSeries = new MultipleDoubleSeries(file.getName(), series);
				List<Integer> res = p.submmit(mSeries);
				for (Integer i : res) {
					System.out.println(file.getName() + "," + i);
				}
			}
		}
		System.err.println(sum / regexs.length);
	}
	
	public void main2() throws Exception {
		String root = new File(System.getProperty("user.dir")).getPath();
		String dataPath = Paths.get(root, "data").toString();
		TimeseriesData in = new TimeseriesData(dataPath+File.separator+"test.csv");
		MultipleDoubleSeries nSeries = in.getPropertyDoubleSeries().subSeries(300, 350);
//		MultipleDoubleSeries nSeries = new MultipleDoubleSeries("213", in.getPropertyDoubleSeries("cpu").subSeries(300, 350));//.subSeries(300, 350)
		Plot.plot(nSeries);
		OnlineTimeseriesPredictionAPI p = new OnlineTimeseriesPredictionAPI();
		DoubleSeries real = new DoubleSeries("real");
		DoubleSeries pre = new DoubleSeries("pre");
		for (int iter = 0; iter < 20; iter ++) {
			System.err.println("epoch is " + iter);
			p.train(nSeries);
		}
		for ( int i = 0; i < nSeries.size(); i++ ) {
			Matrix value = Util.toVec(nSeries.get(i).getItem().iterator());
			long timestamp = nSeries.get(i).getInstant();
			if (i > nSeries.size()/2){
				Matrix t_value = p.prediction();
//				System.out.println(t_value);
				if (t_value != null) {
					System.out.println("");
					System.out.print("{ " + t_value.get(0, 0) + " & " + value.get(0, 0) + "}");
					real.add(new TimeSeries.Entry<Double>(value.get(4, 0), timestamp));
					pre.add(new TimeSeries.Entry<Double>(t_value.get(4, 0), timestamp));
					continue;
				}
			}
			p.todayValue(value, timestamp);
		}
		Plot.plot("222", real, pre);
	}
	
	public static void main(String[] args) throws Exception {
		new PredictionExample().main2();
	}
}
