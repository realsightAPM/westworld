package com.realsight.brain.timeseries.lib.model.segment;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/* @author Sun Muxin
 * 
 */

public class ChangePoint {
	private double[][] dp_penalty_value = null;
	// Minimum distance between change-points.
	private static final double eps = 1e-4;
	private List<Double> e = null;
	private double[] sse = null;
	private static int min_dist = 19;
	
	public double getChangePointAnormlyScore(DoubleSeries nSeries) {
//		System.out.println(nSeries);
		training(nSeries, 1);
		return getPenaltyValue(0)-getPenaltyValue(1);
	}
	
	public void training(DoubleSeries nSeries, int num_changepoint){
		num_changepoint += 1;
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
			sse[i] = sse[i-1] + Math.pow(e.get(i-1), 2);
			dp_penalty_value[i] = new double[num_changepoint+1];
			Arrays.fill(dp_penalty_value[i], Double.MAX_VALUE);
			for(int j = 1; j*min_dist<i && j < num_changepoint+1; j++){
				for(int k = 0; k < i-min_dist; k++){
//					System.out.println("dp_penalty_value["+i+","+k+"]="+((sse[i]-sse[k])/(i-k)+eps));
					dp_penalty_value[i][j] = Math.min(dp_penalty_value[i][j], 
							dp_penalty_value[k][j-1] + (i-k)*Math.log((sse[i]-sse[k])/(i-k)+eps));
					if(i == k) System.out.println(Math.log((sse[i]-sse[k])/(i-k)+eps));
				}
//				System.out.println("dp_penalty_value["+i+","+j+"]="+dp_penalty_value[i][j]);
			}
		}
	}
	
	private List<Integer> getChangePoints(int i, int j){
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
	
	public List<Integer> getChangePoints(int num) throws IndexOutOfBoundsException{
		if(e == null){
			throw new NullPointerException("no exited noise serires.");
		}
		final int n = e.size();
		if(dp_penalty_value[n][num+1]==Double.MAX_VALUE){
			throw new IndexOutOfBoundsException("no exited " + num + " change-points");
		}
		return getChangePoints(n, num+1);
	}
	
	public double getPenaltyValue(int num) throws IndexOutOfBoundsException{
		if(e == null){
			throw new NullPointerException("no exited noise serires.");
		}
		final int n = e.size();
		return dp_penalty_value[n][num+1];
	}
	
	public static void main(String[] args) throws Exception{
		char delimiter = ',';
		Charset charset = Charset.forName("ISO-8859-1");
		String localDir = Paths.get(System.getProperty("user.dir"), "target", "data", "health.csv").toString();
		DoubleSeries nSeries = new DoubleSeries("series name");
		try {
			CsvReader cr = new CsvReader(localDir, delimiter, charset);
			cr.readHeaders();
			long timestamp = 0;
			while(cr.readRecord()){
				Double value = Double.valueOf(cr.get(1));
				timestamp += 1;
				nSeries.add(new TimeSeries.Entry<Double>(value, timestamp));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int num_changepoint = 2;
		ChangePoint CP = new ChangePoint();
		CP.training(nSeries.plus(-1.0*nSeries.mean()), num_changepoint);
		List<Integer> res = CP.getChangePoints(num_changepoint);
		Plot.plot(nSeries);
		for (int i = 0; i < res.size(); i++) {
			System.out.printf(res.get(i) + " ");
		}
		System.out.println("");
	}
}
