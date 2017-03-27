package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class VolumeData {
	private char delimiter = ',';
	private Charset charset = null;
	private String csvFilePath = null;
	public VolumeData(char delimiter, Charset charset, String coalFilePath){
		this.delimiter = delimiter;
		this.charset = charset;
		this.csvFilePath = coalFilePath;
	}
	
	public VolumeData(String coalFilePath){
		this(',', Charset.forName("ISO-8859-1"), coalFilePath);
	}
	
	public DoubleSeries getPropertySeries(String name, SimpleDateFormat sdf){
		DoubleSeries res = new DoubleSeries(name);
		try {
			CsvReader cr = new CsvReader(csvFilePath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("time_window") == -1)
				throw new IOException("File not exists timestamp.");
			if(cr.getIndex(name) == -1)
				throw new IOException("File not exists " + name + ".");
			while(cr.readRecord()){
				String time = cr.get("time_window").split(",")[0].substring(1);
				Date date = null;
				try {
					date = sdf.parse(time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Long timestamp = date.getTime();
				double num = Double.parseDouble(cr.get(name));
				res.add(new TimeSeries.Entry<Double>(num, timestamp));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public DoubleSeries getPropertySeries(String name){
		DoubleSeries res = new DoubleSeries(name);
		try {
			CsvReader cr = new CsvReader(csvFilePath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("timestamp") == -1)
				throw new IOException("File not exists timestamp.");
			if(cr.getIndex(name) == -1)
				throw new IOException("File not exists " + name + ".");
			while(cr.readRecord()){
				String time = cr.get("timestamp");
				Long timestamp = Long.valueOf(time);
				double num = Double.parseDouble(cr.get(name));
				res.add(new TimeSeries.Entry<Double>(num, timestamp));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
