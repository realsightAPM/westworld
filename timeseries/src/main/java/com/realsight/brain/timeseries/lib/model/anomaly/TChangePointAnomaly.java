package com.realsight.brain.timeseries.lib.model.anomaly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.realsight.brain.timeseries.lib.model.regression.*;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.data.Anomaly;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/* @author sunmuxin
 * 
 */

public class TChangePointAnomaly {
	private static double[] energy = null;
	// Minimum distance between change-points.
	private static final double eps = 1e-4;
	private static List<Double> e = null;
	private static double[] sse = null;
	private static int sd_num = 15;
	
	public static void training(DoubleSeries nSeries){
		e = new ArrayList<Double>();
		for(int i = 0; i < nSeries.size(); i++){
			e.add(nSeries.get(i).getItem());
		}
//		System.out.println("start traning ...");
		final int n = e.size();
		energy = new double[n];
		sse = new double[n+1];
		sse[0] = 0;
		for(int i = 1; i <= n; i++){
			sse[i] = sse[i-1] + Math.pow(e.get(i-1), 2);
		}
		double max_e = Double.MIN_VALUE;
		for(int i = 1; i < n; i++){
			energy[i-1] = i*Math.log(sse[i]/i+eps)+(n-i)*Math.log((sse[n]-sse[i])/(n-i)+eps);
			max_e = Math.max(max_e, Math.abs(energy[i-1]));
		}
		for(int i = 1; i < n; i++){
			energy[i-1] = Math.exp(energy[i-1]/max_e);
		}
	}
	
	public static List<Double> detectorSeriesAnomaly(MultipleDoubleSeries series, 
			String y, List<String> x, int cp_num, int train_num) throws Exception {
		
		List<Double> res = new ArrayList<Double>();
		LinearRegression mr = new LinearRegression(x, y);
//        lr.training(series.subSeries(0, train_num));
        DoubleSeries nSeries = new DoubleSeries(new ArrayList<Entry<Double>>(), "noise");
    	for(int i = 0; i < series.size(); i += train_num){
    		MultipleDoubleSeries subSeries = series.subSeries(i, Math.min(i+train_num, series.size()));
    		mr.training(subSeries);
            DoubleSeries fSeries = mr.forecasting(subSeries);
            for(int j = 0; j < subSeries.size(); j++){
            	double e = fSeries.get(j).getItem() - subSeries.getColumn(y).get(j).getItem();
            	nSeries.add(new Entry<Double>(e, subSeries.get(j).getInstant()));
            }
//    		lr.training(subSeries);
    	}
//    	Plot.plot(nSeries, series.getColumn(y));
    	
    	training(nSeries);
    	double sum = 0;
    	for(int i = 0; i < energy.length; i++)
    		sum += energy[i];
    	for(int i = 0; i < series.size()+x.size()+1; i++){
    		res.add(0.0);
    	}
    	
		for(int k = 0; k < energy.length; k += 1){
			int cp_id = k + x.size();
			double anormly_score = energy[k] / sum;
			res.set(cp_id, anormly_score);
		}
		System.out.println(sum);
		return res;
	}
	public static List<Double> detectorNABSeriesAnomaly(String dir) throws Exception{
		System.out.println(dir);
		Anomaly.setLocalDir(dir);
		String y = "value-0";
		List<String> x = new ArrayList<String>();
		x.add("value-1");
		x.add("value-2");
		x.add("value-3");
		x.add("value-4");
		x.add("value-5");
		MultipleDoubleSeries series = new MultipleDoubleSeries(Anomaly.getPropertySeries("value", 0), 
				Anomaly.getPropertySeries("value", 1), Anomaly.getPropertySeries("value", 2), 
				Anomaly.getPropertySeries("value", 3), Anomaly.getPropertySeries("value", 4),
				Anomaly.getPropertySeries("value", 5));
//		Plot.plot(series.getColumn(y));
		return detectorSeriesAnomaly(series, y, x, 15, 600);
	}
	
	public static void main(String[] args) throws Exception{
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAWSCloudwatch/null_elb_request_count_8c0756.csv");
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAdExchange/null_exchange-4_cpm_results.csv");
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAdExchange/null_exchange-4_cpc_results.csv");
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/artificialNoAnomaly/null_art_daily_no_noise.csv");
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/artificialWithAnomaly/null_art_daily_flatmiddle.csv");
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/artificialNoAnomaly/null_art_daily_perfect_square_wave.csv");
		File root = new File("D:/workspace/NAB/src/results/null/");
		for(File file : root.listFiles()){
			if(file.isDirectory()){
				for(File dir : file.listFiles()){
					if(dir.isDirectory())
						continue;
					List<String> t = new ArrayList<String>();
					List<Double> s = new ArrayList<Double>();
					List<String> l = new ArrayList<String>();
					List<String> FP = new ArrayList<String>();
					List<String> FN = new ArrayList<String>();
					List<String> S = new ArrayList<String>();
					Scanner sin = new Scanner(dir);
					sin.nextLine();
					while(sin.hasNext()){
						String line = sin.nextLine();
						t.add(line.split(",")[0]);
						s.add(Double.parseDouble(line.split(",")[1]));
						l.add(line.split(",")[3]);
						FP.add(line.split(",")[4]);
						FN.add(line.split(",")[5]);
						S.add(line.split(",")[6]);
					}
					List<Double> a = detectorNABSeriesAnomaly(dir.getPath());
					String resultFileName = dir.getPath().replace("null", "realsight");
					String resultPath = new File(resultFileName).getParent();
					if(!new File(resultPath).exists()){
						new File(resultPath).mkdirs();
					}
					OutputStream os = new FileOutputStream(resultFileName);
			        OutputStreamWriter writer = new OutputStreamWriter(os);
			        writer.write("timestamp,value,anomaly_score,label,S(t)_reward_low_FP_rate,S(t)_reward_low_FN_rate,S(t)_standard\n");
			        for(int i = 0; i < s.size(); i++){
			        	writer.write(t.get(i)+","+s.get(i)+","+a.get(i)+","+l.get(i)+","+FP.get(i)+","+FN.get(i)+","+S.get(i)+"\n");
			        }
			        writer.close();
				}
			}
		}
		
	}
}
