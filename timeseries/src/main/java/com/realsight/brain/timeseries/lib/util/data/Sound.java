package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class Sound {
	private static final int len = 100;
	private char delimiter = ',';
	private Charset charset = Charset.forName("ISO-8859-1");
	private String localDir = Paths.get(System.getProperty("user.dir"), "target", "data", 
			"Caped Crusader.mp3.csv").toString();
	
	public Sound(String dir){
		localDir = dir;
	}
	public Sound(){
	}
	public DoubleSeries getPropertySeries() throws Exception{
		DoubleSeries res = new DoubleSeries(localDir);
		try {
			CsvReader cr = new CsvReader(localDir, delimiter, charset);
			cr.readHeaders();
			long timestamp = 0;
			int cnt = 1;
			double sumSq = 0.0;
			while(cr.readRecord()){
				Double value = Double.valueOf(cr.get(0));
				sumSq += value*value;
				if(cnt == len){
					timestamp += 1;
					res.add(new TimeSeries.Entry<Double>(Math.sqrt(sumSq/len), timestamp));
					cnt = 0;
					sumSq = 0.0;
				}
				cnt += 1;
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = res.size();
		res = res.subSeries(len/10, len*9/10);
		System.out.println(res.size());
		return res;
	}
	
	public static void main(String[] args) throws Exception{
		DoubleSeries res = new Sound().getPropertySeries();
		System.out.println(res.max() + " " + res.min());
	}
}
