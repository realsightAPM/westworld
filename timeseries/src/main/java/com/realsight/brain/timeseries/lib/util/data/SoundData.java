package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class SoundData{
	private Character delimiter = null;
	private Charset charset = null;
	private String soundFilePath = null;
	
	public SoundData(char delimiter, Charset charset, String soundFilePath){
		this.delimiter = delimiter;
		this.charset = charset;
		this.soundFilePath = soundFilePath;
	}
	
	public SoundData(String soundFilePath){
		this(',', Charset.forName("ISO-8859-1"), soundFilePath);
	}
	public DoubleSeries getPropertySeries() {
		DoubleSeries res = new DoubleSeries(soundFilePath);
		try {
			CsvReader cr = new CsvReader(soundFilePath, delimiter, charset);
			cr.readHeaders();
			long timestamp = 0;
			while(cr.readRecord()){
				Double value = Double.valueOf(cr.get(0));
				res.add(new TimeSeries.Entry<Double>(value, timestamp));
				timestamp += 1;
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
