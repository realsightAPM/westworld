package com.realsight.brain.timeseries.lib.util;


import com.realsight.brain.timeseries.lib.model.regression.LinearRegression;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Util {
	
	public static Path writeCsv(DoubleSeries series) {
        StringBuffer data = new StringBuffer();
        data.append("timestamp");
        data.append(","+series.getName());
        data.append("\n");
        for(Entry<Double> e : series.getData()){
        	data.append(e.getInstant());
        	data.append(","+e.getItem());
        	data.append("\n");
        }
        return writeStringToTempFile(data.toString());
    }
	
    public static Path writeCsv(MultipleDoubleSeries series) {
        StringBuffer data = new StringBuffer();
        data.append("timestamp");
        for(String name : series.getNames()){
        	data.append(","+name);
        }
        data.append("\n");
        for(Entry<LinkedList<Double>> e : series.getData()){
        	data.append(e.getInstant());
        	for(Double item : e.getItem()){
        		data.append(","+item);
        	}
        	data.append("\n");
        }
        return writeStringToTempFile(data.toString());
    }

    public static Path writeStringToTempFile(String content) {
        try {
            return writeString(content, Paths.get(File.createTempFile("out-", ".csv").getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Path writeString(String content, Path path) {
        try {
            Files.write(path, content.getBytes());
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void check(boolean condition) {
        if (!condition) {
            throw new RuntimeException();
        }
    }

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
        }
    }
    
    public static double mean(DoubleSeries series){
    	double sum = 0;
    	for(int i = 0; i < series.size(); i++){
    		sum += series.get(i).getItem();
    	}
    	return sum/series.size();
    }
    
    public static double sd(MultipleDoubleSeries series, List<String> x, String y) {
		LinearRegression lr = new LinearRegression(x,y);
        lr.training(series);
        DoubleSeries fSeries = lr.forecasting(series);
        DoubleSeries Y = series.getColumn(y);
        
        List<Double> noise = new ArrayList<Double>();
        for(int i = 0; i < Y.size(); i++){
        	double t = Y.get(i).getItem() - fSeries.get(i).getItem();
        	noise.add(t*t);
        }
        Collections.sort(noise);
        int n = noise.size();
        int l = (int) (n*0.0000001), r = (int) (n*0.99999);
        double sse = 0;
        for(int i = l; i <= r; i++){
        	sse += noise.get(i);
        }
        return Math.sqrt(sse/(r-l+1));
	}
    
    public static double Sigma(double y){
		return 1.0-2.0/(1.0+Math.exp(y));
	}
}

