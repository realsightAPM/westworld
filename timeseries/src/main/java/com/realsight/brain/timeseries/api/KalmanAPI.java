package com.realsight.brain.timeseries.api;

import com.realsight.brain.timeseries.lib.model.kalman.action.CointegrationAction;
import com.realsight.brain.timeseries.lib.model.kalman.filter.KalmanFilter;
import com.realsight.brain.timeseries.lib.model.kalman.test.Backtest;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.data.RealSight;
import com.realsight.brain.timeseries.lib.util.plot.Plot;
import com.realsight.brain.timeseries.lib.util.results.Domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.la4j.Matrix;

public class KalmanAPI {
	
	private static final int noise_num = 100;
	private Domain res = null;
	
	private void jsonGenerate(JSONObject jsonObj, Matrix mat, String name) throws JSONException {
		for(int i = 0; i < mat.rows(); i++)
        	for(int j = 0; j < mat.columns(); j++){
        		jsonObj.put(name+"_"+i+"_"+j, mat.get(i, j));
        	}
	}
	
	private Matrix jsonGain(JSONObject jsonObj, String name, int row_num, int col_num) throws JSONException {
		Matrix res = Matrix.zero(row_num, col_num);
		for(int i = 0; i < row_num; i++)
			for(int j = 0; j < col_num; j++){
				res.set(i, j, jsonObj.getDouble(name+"_"+i+"_"+j));
			}
		return res;
	}
	
	public void deleteNoise() throws Exception {
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		deleteNoise(model_dir_path);
	}
	public void deleteNoise(String model_dir_path) throws Exception {
		File noise_file = Paths.get(model_dir_path, "noise.dat").toFile();
        if(noise_file.exists())
        	noise_file.delete();
	}
	public void storageNoise(double noise) throws Exception {
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		storageNoise(noise, model_dir_path);
	}
	public void storageNoise(double noise, String model_dir_path) throws Exception {
		File noise_file = Paths.get(model_dir_path, "noise.dat").toFile();
		List<Double> tmp = new ArrayList<Double>();
        tmp.add(noise);
		if(noise_file.exists()){
			Scanner sin = new Scanner(noise_file);
	        for(int i = 1; i<noise_num && sin.hasNext(); i++){
	        	tmp.add(sin.nextDouble());
	        }
	        sin.close();
		}
        OutputStream os = new FileOutputStream(noise_file.getPath());
        OutputStreamWriter writer = new OutputStreamWriter(os);
        for(int i = 0; i < tmp.size(); i++){
        	writer.write(tmp.get(i) + "\n");
        }
        writer.close();
	}
	public double getNoiseSd() throws Exception {
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		return getNoiseSd(model_dir_path);
	}
	public double getNoiseSd(String model_dir_path) throws Exception {
		File noise_file = Paths.get(model_dir_path, "noise.dat").toFile();
		if(!noise_file.exists())
			return 1e7;
		double sd = 0.0;
		int sz = 1;
        Scanner sin = new Scanner(noise_file);
        for(int i = 1; i<noise_num && sin.hasNext(); i++){
        	double noise = sin.nextDouble();
        	sd += noise*noise;
        	sz += 1;
        }
        sin.close();
        return Math.sqrt(sd/sz);
	}
	public double getNoiseMean() throws Exception {
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		return getNoiseMean(model_dir_path);
	}
	public double getNoiseMean(String model_dir_path) throws Exception {
		File noise_file = Paths.get(model_dir_path, "noise.dat").toFile();
		if(!noise_file.exists())
			return 1e7;
		double mean = 0.0;
		int sz = 1;
        Scanner sin = new Scanner(noise_file);
        for(int i = 1; i<noise_num && sin.hasNext(); i++){
        	double noise = sin.nextDouble();
        	mean += noise;
        	sz += 1;
        }
        sin.close();
        return mean/sz;
	}
	
	public void save(KalmanFilter filter) throws Exception {
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		save(filter, model_dir_path);
	}
	public void save(KalmanFilter filter, String model_dir_path) throws Exception {
		if(model_dir_path == null){
			model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		}
		String model_file_path = Paths.get(model_dir_path, "model.dat").toString();
		File model_dir = new File(model_dir_path);
		if(! model_dir.isDirectory()) {
			model_dir.mkdirs();
		}
		OutputStream os = new FileOutputStream(model_file_path);
        OutputStreamWriter writer = new OutputStreamWriter(os);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("mStateCount", filter.getStateCount());
        jsonObj.put("mSensorCount", filter.getSensorCount());
        jsonGenerate(jsonObj, filter.getState(), "mState");
        jsonGenerate(jsonObj, filter.getStateCovariance(), "mStateCovariance");
        jsonGenerate(jsonObj, filter.getUpdateMatrix(), "mUpdateMatrix");
        jsonGenerate(jsonObj, filter.getUpdateCovariance(), "mUpdateCovariance");
        jsonGenerate(jsonObj, filter.getMeasurementCovariance(), "mMeasurementCovariance");
        writer.write(jsonObj.toString()+"\n");
		writer.close();
//		System.out.println(jsonObj.toString());
	}
	
	public KalmanFilter load() throws Exception{
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		return load(model_dir_path);
	}
	public KalmanFilter load(String model_dir_path) throws Exception{
		if(model_dir_path == null){
			model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		}
		String model_file_path = Paths.get(model_dir_path, "model.dat").toString();
		File model_file = new File(model_file_path);
		if(! model_file.exists()) {
			return null;
		}
		Scanner sin = new Scanner(new File(model_file_path));
		String str = sin.nextLine();
    	JSONObject jsonObj = new JSONObject(str);
    	int mStateCount = jsonObj.getInt("mStateCount");
    	int mSensorCount = jsonObj.getInt("mSensorCount");
    	Matrix mState = jsonGain(jsonObj, "mState", mStateCount, 1);
    	Matrix mStateCovariance = jsonGain(jsonObj, "mStateCovariance", mStateCount, mStateCount);
    	Matrix mUpdateMatrix = jsonGain(jsonObj, "mUpdateMatrix", mStateCount, mStateCount);
    	Matrix mUpdateCovariance = jsonGain(jsonObj, "mUpdateCovariance", mStateCount, mStateCount);
    	Matrix mMeasurementCovariance = jsonGain(jsonObj, "mMeasurementCovariance", mSensorCount, 1);
    	sin.close();
    	KalmanFilter res = new KalmanFilter(mStateCount, mSensorCount);
    	res.setState(mState);
    	res.setStateCovariance(mStateCovariance);
    	res.setUpdateMatrix(mUpdateMatrix);
    	res.setUpdateCovariance(mUpdateCovariance);
    	res.setMeasurementCovariance(mMeasurementCovariance);
    	return res;
	}
	
	public void train(String file_path) throws Exception{
		RealSight.setLocalDir(file_path);
		List<String> x = new ArrayList<String>();
        String y = "value-0";
        x.add("ones-0");
        x.add("ones-0");
        x.add("ones-0");
        x.add("ones-0");
        CointegrationAction strategy = new CointegrationAction(x, y);
        // download historical data
        MultipleDoubleSeries series = new MultipleDoubleSeries(RealSight.getPropertySeries("value"),
        		RealSight.getPropertySeries("ones", 0), RealSight.getPropertySeries("zeros", 0));
        Backtest backtest = new Backtest(series);
        backtest.run(strategy, series);
        save(backtest.getFilter());
        deleteNoise();
	}
	public void train(DoubleSeries ySeries) throws Exception {
		List<String> x = new ArrayList<String>();
        String y = "value-0";
        x.add("ones-0");
        x.add("ones-0");
        x.add("ones-0");
        x.add("ones-0");
        
        CointegrationAction strategy = new CointegrationAction(x, y);
        // download historical data
        ySeries.setName(y);
        DoubleSeries xSeries = ySeries.mul(0.0).plus(1.0);
        xSeries.setName("ones-0");
        MultipleDoubleSeries series = new MultipleDoubleSeries(ySeries, xSeries);
        Backtest backtest = new Backtest(series);
        backtest.run(strategy, series);
        save(backtest.getFilter());
        deleteNoise();
	}
	public void train(List<Double> yList) throws Exception {
		DoubleSeries ySeries = new DoubleSeries("value-0");
		for(int i = 0; i < yList.size(); i++){
			Long timestamp = i+0L;
			ySeries.add(new Entry<Double>(yList.get(i), timestamp));
		}
		train(ySeries);
	}
	
	public void forecasting(Entry<Double> y) throws Exception {
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		forecasting(y, 0.9, model_dir_path);
	}
	public void forecasting(Entry<Double> y, double alpha) throws Exception {
		String model_dir_path = Paths.get(System.getProperty("user.dir"), "target", "model", "kalman").toString();
		forecasting(y, alpha, model_dir_path);
	}
	public void forecasting(Entry<Double> y, double alpha, String model_dir_path) throws Exception {
		double[] h = new double[]{1.0, 1.0, 1.0, 1.0};
		KalmanFilter filter = load(model_dir_path);
		filter.setExtractionMatrix(Matrix.from1DArray(1, filter.getStateCount(), h));
		filter.step(Matrix.constant(1, 1, y.getItem()));
		res = new Domain(y.getInstant(), y.getItem(), y.getItem()-filter.getInnovation().get(0, 0), getNoiseSd(), alpha);
		save(filter);
		storageNoise(filter.getInnovation().get(0, 0));
	}
	
	public Long getTimeStamp() {
		return res.getTimestamp();
	}
	public double getValue() {
		return res.getValue();
	}
	public double getFValue() {
		return res.getFValue();
	}
	public double getsd() {
		return res.getSd();
	}
	public double getUpper_bound() {
		return res.getUpper_bound();
	}
	public double getLower_bound() {
		return res.getLower_bound();
	}
	public double getUpper_bound(double fValue, double sd, double alpha) {
		Domain res = new Domain(-1L, fValue, fValue, sd, alpha);
		return res.getUpper_bound();
	}
	public double getLower_bound(double fValue, double sd, double alpha) {
		Domain res = new Domain(-1L, fValue, fValue, sd, alpha);
		return res.getLower_bound();
	}
	
    private void main() throws Exception {
    	String localDir = Paths.get(System.getProperty("user.dir"), "target", "data", 
    			"282.1c701d3e20125b8909c8bc40aa4cc1e0.ActiveThreadsNum").toString();
    	train(localDir);
    	DoubleSeries ySeries = RealSight.getPropertySeries("value");
    	Plot.plot("kalman", ySeries);
    	for(int i = 0; i < ySeries.size(); i++){
    		forecasting(ySeries.get(i));
    		System.out.println(getTimeStamp() + " " + getValue() + " " + getFValue() + " " + getsd() + 
    				" " + getUpper_bound() + " " + getLower_bound());
    	}
    }
    
    public static void main(String[] args) throws Exception {
    	new KalmanAPI().main();
    }
}

