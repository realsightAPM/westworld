package com.realsight.westworld.tsp.lib.util;


import Jama.Matrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

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
        for(String name : series.getProperty_list()){
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

    public static Path writeCsv(MultipleDoubleSeries series, Path dir) {
        StringBuffer data = new StringBuffer();
        data.append("timestamp");
        for(String name : series.getProperty_list()){
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
        Path path = Paths.get(dir.toString(), series.getName());
        return writeString(data.toString(), path);
    }

    public static Path writeString(String content, Path path) {
        try {
        	path.getParent().toFile().mkdirs();
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
    
    public static double Sigma(double y){
		return 1.0-2.0/(1.0+Math.exp(y));
	}
    
    public static Matrix toVec(Iterator<Double> iter) {
    	List<Double> list = new ArrayList<Double>();
    	while(iter.hasNext()) {
    		list.add(iter.next());
    	}
    	double[] array = new double[list.size()];
    	for(int i = 0; i < list.size(); i++){
    		array[i] = list.get(i);
    	}
    	return new Matrix(array, array.length);
    }
    
    public static Matrix toVec(Double x) {
    	return new Matrix(1, 1, x);
    }
}

