package com.realsight.brain.timeseries.lib.model.anomaly;

//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.realsight.brain.timeseries.lib.model.regression.*;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

/* @author sunmuxin
 * 
 */

public class ChangePointAnomaly {
	private static double[][] dp_penalty_value = null;
	// Minimum distance between change-points.
	private static final double eps = 1e-4;
	private static List<Double> e = null;
	private static double[] sse = null;
	private static int min_dist = -1;
	private static int sd_num = 15;
	
	public static void training(DoubleSeries nSeries, int num_changepoint){
		e = new ArrayList<Double>();
		for(int i = 0; i < nSeries.size(); i++){
			e.add(nSeries.get(i).getItem());
		}
//		System.out.println("start traning ...");
		final int n = e.size();
		dp_penalty_value = new double[n+1][];
		sse = new double[n+1];
		sse[0] = 0;
		dp_penalty_value[0] = new double[num_changepoint+1];
		Arrays.fill(dp_penalty_value[0], Double.MAX_VALUE);
		dp_penalty_value[0][0] = 0;
		for(int i = 1; i <= n; i++){
//			if(n % i == 0)
//				System.out.println("complete traning " + (100.0*i/n) + "%");
			sse[i] = sse[i-1] + Math.pow(e.get(i-1), 2);
			dp_penalty_value[i] = new double[num_changepoint+1];
			Arrays.fill(dp_penalty_value[i], Double.MAX_VALUE);
			for(int j = 1; j*min_dist<i && j < num_changepoint+1; j++){
				for(int k = 0; k < i-min_dist; k++){
					//logger.info("dp_penalty_value["+i+","+k+"]="+((sse[i]-sse[k])/(i-k)+eps));
					dp_penalty_value[i][j] = Math.min(dp_penalty_value[i][j], 
							dp_penalty_value[k][j-1] + (i-k)*Math.log((sse[i]-sse[k])/(i-k)+eps));
				}
				//logger.info("dp_penalty_value["+i+","+j+"]="+dp_penalty_value[i][j]);
			}
		}
	}
	
	private static  List<Integer> getChangePoints(int i, int j){
		List<Integer> res = null;
		if(i==0 && j==0)
			return new ArrayList<Integer>();
		else if(i==0){
			throw new RuntimeException("dp_penalty_value is error. " +
					"please check all noise series change-point code. i error");
		}
		else if(j==0){
			throw new RuntimeException("dp_penalty_value is error. " +
					"please check all noise series change-point code. j error");
		}
		for(int k = 0; k < i-min_dist; k++){
			double penalty_value = dp_penalty_value[k][j-1] + (i-k)*Math.log((sse[i]-sse[k])/(i-k)+eps);
			if(Math.abs(penalty_value-dp_penalty_value[i][j]) < eps){
				res = getChangePoints(k, j-1);
				res.add(k);
				return res;
			}
		}
		throw new RuntimeException("@author sunmuxin also don't know what happened.");
	}
	
	public static List<Integer> getChangePoints(int num) throws IndexOutOfBoundsException{
		if(e == null){
			throw new NullPointerException("no exited noise serires.");
		}
		final int n = e.size();
		if(dp_penalty_value[n][num+1]==Double.MAX_VALUE){
			throw new IndexOutOfBoundsException("no exited " + num + " change-points");
		}
		return getChangePoints(n, num+1);
	}
	
	public static double getPenaltyValue(int num) throws IndexOutOfBoundsException{
		if(e == null){
			throw new NullPointerException("no exited noise serires.");
		}
		final int n = e.size();
		return dp_penalty_value[n][num+1];
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
    	double total_sd = Math.sqrt(nSeries.sqrtSum())+eps;
    	
    	min_dist = nSeries.size()/40;
    	sd_num = Math.min(min_dist/2, 40);
    	training(nSeries, cp_num+1);
    	List<Integer> cp = getChangePoints(cp_num);
    	for(int i = 0; i < series.size()+x.size()+291; i++){
    		res.add(0.0);
    	}
    	
		for(int k = 1; k < cp.size(); k += 1){
			MultipleDoubleSeries front_series = series.subSeries(Math.max(0, cp.get(k)-sd_num), cp.get(k));
			mr.forecasting(front_series);
//			System.out.println(mr.sse());
			@SuppressWarnings("unused")
			double front_sd = Math.sqrt(mr.sse());
			MultipleDoubleSeries back_series = series.subSeries(cp.get(k), Math.max(cp.get(k)+sd_num, series.size()));
			mr.forecasting(back_series);
//			Plot.plot(nSeries.subSeries(cp.get(k)-sd_num+1, cp.get(k)+sd_num+1));
			double back_sd = Math.sqrt(mr.sse());
			double anormly_score = Math.abs(total_sd-back_sd)/Math.min(total_sd, back_sd);
//			System.out.println("sd = " + front_sd + "," + back_sd + "," + total_sd);
			int cp_id = cp.get(k)+x.size()+291;
			System.out.println(anormly_score+ ", "+cp_id);
			res.set(cp_id, anormly_score);
		}
		return res;
	}
//	public static List<Double> detectorNABSeriesAnomaly(String dir) throws Exception{
//		System.out.println(dir);
//		TimeseriesData.setLocalDir(dir);
//		String y = "value-0";
//		List<String> x = new ArrayList<String>();
//		x.add("value-1");
//		x.add("value-2");
//		x.add("value-3");
//		x.add("value-4");
//		x.add("value-5");
//		MultipleDoubleSeries series = new MultipleDoubleSeries(TimeseriesData.getPropertySeries("value", 0), 
//				TimeseriesData.getPropertySeries("value", 1), TimeseriesData.getPropertySeries("value", 2), 
//				TimeseriesData.getPropertySeries("value", 3), TimeseriesData.getPropertySeries("value", 4),
//				TimeseriesData.getPropertySeries("value", 5));
////		Plot.plot(series.getColumn(y));
//		return detectorSeriesAnomaly(series, y, x, 8, 60000);
//	}
//	
//	public static void main(String[] args) throws Exception{
////		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAWSCloudwatch/null_elb_request_count_8c0756.csv");
////		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAdExchange/null_exchange-4_cpm_results.csv");
////		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAdExchange/null_exchange-4_cpc_results.csv");
////		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/artificialNoAnomaly/null_art_daily_no_noise.csv");
////		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/artificialWithAnomaly/null_art_daily_flatmiddle.csv");
////		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/artificialNoAnomaly/null_art_daily_perfect_square_wave.csv");
//		File root = new File("D:/workspace/NAB/src/results/null/");
//		for(File file : root.listFiles()){
//			if(file.isDirectory()){
//				for(File dir : file.listFiles()){
//					if(dir.isDirectory())
//						continue;
//					List<String> t = new ArrayList<String>();
//					List<Double> s = new ArrayList<Double>();
//					List<String> l = new ArrayList<String>();
//					List<String> FP = new ArrayList<String>();
//					List<String> FN = new ArrayList<String>();
//					List<String> S = new ArrayList<String>();
//					Scanner sin = new Scanner(dir);
//					sin.nextLine();
//					while(sin.hasNext()){
//						String line = sin.nextLine();
//						t.add(line.split(",")[0]);
//						s.add(Double.parseDouble(line.split(",")[1]));
//						l.add(line.split(",")[3]);
//						FP.add(line.split(",")[4]);
//						FN.add(line.split(",")[5]);
//						S.add(line.split(",")[6]);
//					}
//					List<Double> a = detectorNABSeriesAnomaly(dir.getPath());
//					String resultFileName = dir.getPath().replace("null", "realsight");
//					String resultPath = new File(resultFileName).getParent();
//					if(!new File(resultPath).exists()){
//						new File(resultPath).mkdirs();
//					}
//					OutputStream os = new FileOutputStream(resultFileName);
//			        OutputStreamWriter writer = new OutputStreamWriter(os);
//			        writer.write("timestamp,value,anomaly_score,label,S(t)_reward_low_FP_rate,S(t)_reward_low_FN_rate,S(t)_standard\n");
//			        for(int i = 0; i < s.size(); i++){
//			        	writer.write(t.get(i)+","+s.get(i)+","+a.get(i)+","+l.get(i)+","+FP.get(i)+","+FN.get(i)+","+S.get(i)+"\n");
//			        }
//			        writer.close();
//				}
//			}
//		}
//		
//	}
}
