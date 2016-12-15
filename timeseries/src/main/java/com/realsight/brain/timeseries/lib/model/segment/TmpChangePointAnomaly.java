package com.realsight.brain.timeseries.lib.model.segment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.realsight.brain.timeseries.lib.model.regression.*;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.IntegerSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.data.Anomaly;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/* @author sunmuxin
 * 
 */

public class TmpChangePointAnomaly {
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
	
	private static double D(IntegerSeries Q, IntegerSeries P, int bit_size){
		double res = 0.0;
		double[] q = new double[bit_size];
		double[] p = new double[bit_size];
		
		for(int i = 0; i < Q.size(); i++){
			q[Q.get(i).getItem()] += 1.0;
		}
		for(int i = 0; i < P.size(); i++){
			p[P.get(i).getItem()] += 1.0;
		}
		for(int i = 0; i < bit_size; i++) {
			q[i] += 0.01; p[i] += 0.01;
			res += q[i]*Math.log(q[i]/(Q.size()+bit_size*0.01));
			res -= q[i]*Math.log(p[i]/(P.size()+bit_size*0.01));
		}
		return res;
	}
	
	// A match B
	private static DoubleSeries match(DoubleSeries A, DoubleSeries B){
		DoubleSeries fSeries = new DoubleSeries(new ArrayList<Entry<Double>>(), "fSeries");
		int[] match = new int[A.size()];
		for(int i = 0; i < A.size(); i++)
			match[i] = i;
		Random rng = new Random(2234);
		for(int iter = 0; iter < 1000; iter++){
			int i = rng.nextInt(A.size());
			int j = rng.nextInt(A.size());
			double a1 = A.get(i).getItem();
			double a2 = A.get(j).getItem();
			double b1 = B.get(match[i]).getItem();
			double b2 = B.get(match[j]).getItem();
			double w1 = (a1-b1)*(a1-b1) + (a2-b2)*(a2-b2);
			double w2 = (a1-b2)*(a1-b2) + (a2-b1)*(a2-b1);
			if(w1 > w2) {
				int t = match[i];
				match[i] = match[j];
				match[j] = t;
			}
		}
		for(int i = 0; i < A.size(); i++){
			double a = A.get(i).getItem();
			double f = B.get(match[i]).getItem();
//			System.out.println(a + "," + f);
			fSeries.add(new Entry<Double>(f, A.get(i).getInstant()));
		}
		return fSeries;
	}
	
	public static List<Double> detectorSeriesAnomaly(DoubleSeries series, 
			int cp_num, int window_size, int train_num, int bit_size) throws Exception {
		
		double max_value = series.max();
		double min_value = series.min();
		List<Double> borders = new ArrayList<Double>();
		borders.add(min_value);
		for(int i = 1; i < bit_size; i++){
			double k_value = (max_value-min_value)*i/bit_size+min_value;
			borders.add(k_value);
//			System.out.println(k_value);
		}
		borders.add(max_value);
		IntegerSeries bitSeries = new IntegerSeries(new ArrayList<Entry<Integer>>(), "bit");
		for(int i = 0; i < series.size(); i++){
			int bit = bit_size-1;
			for(int b = 1; b < bit_size; b++){
				if(borders.get(b) >= series.get(i).getItem()){
					bit = b-1;
					break;
				}
			}
//			System.out.println(bit);
			bitSeries.add(new Entry<Integer>(bit, series.get(i).getInstant()));
		}
		
		List<Double> res = new ArrayList<Double>();
        DoubleSeries nSeries = new DoubleSeries(new ArrayList<Entry<Double>>(), "noise");
    	for(int i = 0; i*window_size < series.size(); i += 1){
    		IntegerSeries bitWindow = bitSeries.subSeries(i*window_size, 
    				Math.min((i+1)*window_size,series.size()));
    		DoubleSeries window = series.subSeries(i*window_size, 
    				Math.min((i+1)*window_size,series.size()));
    		int c_window = 0;
    		for(int j = 1; j < i; j++){
//    			System.out.println(D(bitSeries.subSeries(j*window_size, (j+1)*window_size), bitWindow, bit_size));
    			if(D(bitSeries.subSeries(j*window_size, (j+1)*window_size), bitWindow, bit_size) < 
    				D(bitSeries.subSeries(c_window*window_size, (c_window+1)*window_size), bitWindow, bit_size)){
    				c_window = j;
    			}
    		}
    		DoubleSeries fSeries = match(window, series.subSeries(c_window*window_size, (c_window+1)*window_size));
    		
            for(int j = 0; j < window.size(); j++){
            	double e = fSeries.get(j).getItem() - window.get(j).getItem();
            	if(i*window_size+j < train_num)
            		e = 0.0;
            	nSeries.add(new Entry<Double>(e, window.get(j).getInstant()));
            }
    	}
//    	Plot.plot(series);
//    	Plot.plot(nSeries);
//    	double total_sd = Math.sqrt(nSeries.sqrtSum())+eps;
    	
    	min_dist = nSeries.size()/80;
    	training(nSeries.subSeries(train_num, nSeries.size()), cp_num+1);
    	List<Integer> cp = getChangePoints(cp_num);
    	for(int i = 0; i < series.size(); i++){
    		res.add(0.0);
    	}
		for(int k = 1; k < cp.size(); k += 1){
			double anormly_score = 1.0;
//			System.out.println("sd = " + front_sd + "," + back_sd + "," + total_sd);
			int cp_id = cp.get(k)+train_num;
			System.out.println(anormly_score+ ", "+cp_id);
			res.set(cp_id, anormly_score);
		}
		return res;
	}
	public static List<Double> detectorNABSeriesAnomaly(String dir) throws Exception{
		System.out.println(dir);
		Anomaly.setLocalDir(dir);
		
		int cp_num = 5;
		if (dir.lastIndexOf("artificialNoAnomaly") != -1)
			cp_num = 0;
		else if (dir.lastIndexOf("artificialWithAnomaly") != -1)
			cp_num = 3;
		else if (dir.lastIndexOf("realAdExchange") != -1)
			cp_num = 11;
		else if (dir.lastIndexOf("realAWSCloudwatch") != -1)
			cp_num = 6;
		else if (dir.lastIndexOf("realKnownCause") != -1)
			cp_num = 15;
		else if (dir.lastIndexOf("realTraffic") != -1)
			cp_num = 7;
		else if (dir.lastIndexOf("realTweets") != -1)
			cp_num = 12;
		return detectorSeriesAnomaly(Anomaly.getPropertySeries("value"), cp_num, 150, 600, 11);
	}
	
	public static void main(String[] args) throws Exception{
//		detectorNABSeriesAnomaly("D:/MyEclipse/results/null/realAWSCloudwatch/null_elb_request_count_8c0756.csv");
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAdExchange/null_exchange-4_cpm_results.csv");
//		detectorNABSeriesAnomaly("D:/workspace/NAB/src/results/null/realAdExchange/null_exchange-4_cpc_results.csv");
//		detectorNABSeriesAnomaly("D:/MyEclipse/results/null/artificialNoAnomaly/null_art_daily_no_noise.csv");
//		detectorNABSeriesAnomaly("D:/MyEclipse/results/null/artificialWithAnomaly/null_art_increase_spike_density.csv");
//		detectorNABSeriesAnomaly("D:/MyEclipse/results/null/artificialNoAnomaly/null_art_daily_perfect_square_wave.csv");
		File root = new File("D:/MyEclipse/results/null/");
		for(File file : root.listFiles()){
			if(file.isDirectory()){
				for(File dir : file.listFiles()){
					if(dir.isDirectory())
						continue;
//					if (dir.getPath().lastIndexOf("rogue") == -1)
//						continue;
					if (dir.getPath().lastIndexOf("realTweets") == -1)
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
					sin.close();
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
