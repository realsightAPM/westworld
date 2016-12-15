package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class Sunspot {
	private static char delimiter = ';';
	private static Charset charset = Charset.forName("ISO-8859-1");
	private static String localDir = Paths.get(System.getProperty("user.dir"), "target", "data", 
			"SN_d_hem_V2.0.csv").toString();
	
	public static void setLocalDir(String dir){
		localDir = dir;
	}
	public static DoubleSeries getPropertySeries(String name){
		return getPropertySeries(name, 0);
	}
	public static DoubleSeries getPropertySeries(String name, int delay){
		DoubleSeries res = new DoubleSeries(name+"-"+delay);
		try {
			CsvReader cr = new CsvReader(localDir, delimiter, charset);
			cr.readHeaders();
			Queue<String> que = new LinkedList<String>();
			for(int i = 0; i < delay; i++){
				cr.readRecord();
				que.add(cr.get(name));
			}
			if(cr.getIndex("timestamp") == -1)
				throw new IOException("File not exists timestamp.");
			while(cr.readRecord()){
				que.add(cr.get(name));
				Double time = Double.valueOf(cr.get("timestamp"));
				Long timestamp = (long) (time*100000);
				Double value = 0.0;
				if(name.equals("ones")){
					value = 1.0;
				} else if (name.equals("zeros")) {
					value = 0.0;
				}else {
					value = new Double(que.poll());
				}
				res.add(new TimeSeries.Entry<Double>(value, timestamp));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static void main(String[] args){
		DoubleSeries res = getPropertySeries("value", 0);
		System.out.println(res);
	}
}
