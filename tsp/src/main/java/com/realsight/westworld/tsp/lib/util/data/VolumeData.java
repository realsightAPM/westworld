package com.realsight.westworld.tsp.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.realsight.westworld.tsp.lib.csv.CsvReader;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries;

public class VolumeData {
	private char delimiter = ',';
	private Charset charset = null;
	private String csvFilePath = null;
	public VolumeData(char delimiter, Charset charset, String path){
		this.delimiter = delimiter;
		this.charset = charset;
		this.csvFilePath = path;
	}
	
	public VolumeData(String path){
		this(',', Charset.forName("ISO-8859-1"), path);
	}
	
	public DoubleSeries getPropertySeries(String name, SimpleDateFormat sdf){
		DoubleSeries res = new DoubleSeries(name);
		try {
			CsvReader cr = new CsvReader(csvFilePath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("start_time") == -1)
				throw new IOException("File not exists start_time.");
			if(!name.equals("hour") && cr.getIndex(name)==-1)
				throw new IOException("File not exists " + name + ".");
			Long idx = 0L;
			while(cr.readRecord()){
				String time = cr.get("start_time").trim();
				Date date = null;
				try {
					date = sdf.parse(time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Long timestamp = date.getTime();
				Calendar cal =  Calendar.getInstance();
				cal.setTimeInMillis(timestamp);
				double num = Double.parseDouble(cr.get(name));
				res.add(new TimeSeries.Entry<Double>(num, idx));
				idx = idx + 1L;
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
