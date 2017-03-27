package com.realsight.brain.timeseries.lib.util.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleStringSeries;
import com.realsight.brain.timeseries.lib.series.StringSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class TimeseriesData {
	private char delimiter = ',';
	private Charset charset = null;
	private String csvFilePath = null;
	public TimeseriesData(char delimiter, Charset charset, String coalFilePath){
		this.delimiter = delimiter;
		this.charset = charset;
		this.csvFilePath = coalFilePath;
	}
	
	public TimeseriesData(String coalFilePath){
		this(',', Charset.forName("ISO-8859-1"), coalFilePath);
	}
	
	public DoubleSeries getPropertyDoubleSeries(String name, SimpleDateFormat sdf){
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
	
	public DoubleSeries getPropertyDoubleSeries(String name){
		DoubleSeries res = new DoubleSeries(name);
		long id = 0L;
		try {
			CsvReader cr = new CsvReader(csvFilePath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("timestamp") == -1)
				throw new IOException("File not exists timestamp.");
			if(cr.getIndex(name) == -1)
				throw new IOException("File not exists " + name + ".");
			while(cr.readRecord()){
				String time = cr.get("timestamp");
				Long timestamp = id;
				double num = Double.parseDouble(cr.get(name));
				res.add(new TimeSeries.Entry<Double>(num, timestamp));
				id = id + 1;
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public StringSeries getPropertyStringSeries(String name){
		StringSeries res = new StringSeries(name);
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
				res.add(new TimeSeries.Entry<String>(cr.get(name), timestamp));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public MultipleStringSeries getPropertyStringSeries(){
		Collection<String> names = new ArrayList<String>();
		MultipleStringSeries res = null;
		try {
			CsvReader cr = new CsvReader(csvFilePath, delimiter, charset);
			cr.readHeaders();
			for ( int i = 0; i < cr.getHeaderCount(); i++ ) {
				if (cr.getHeader(i).equals("timestamp")) continue;
				names.add(cr.getHeader(i));
			}
			List<StringSeries> datas = new ArrayList<StringSeries>();
			for ( int i = 0; i < cr.getHeaderCount(); i++ ) {
				if (cr.getHeader(i).equals("timestamp")) continue;
				datas.add(new StringSeries(cr.getHeader(i)));
			}
			while(cr.readRecord()){
				for ( int i = 0, j = 0; i < cr.getHeaderCount(); i++ ) {
					if (cr.getHeader(i).equals("timestamp")) continue;
					Long timestamp = Long.parseLong(cr.get(cr.getIndex("timestamp")).trim());
					String value = cr.get(cr.getHeader(i));
					datas.get(j).add(
						new TimeSeries.Entry<String> (value, timestamp));
					j++;
				}
			}
			System.out.println((new File(csvFilePath)).getName().split(".").length);
			res = new MultipleStringSeries((new File(csvFilePath)).getName(),datas);
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public MultipleDoubleSeries getPropertyDoubleSeries(){
		Collection<String> names = new ArrayList<String>();
		MultipleDoubleSeries res = null;
		try {
			CsvReader cr = new CsvReader(csvFilePath, delimiter, charset);
			cr.readHeaders();
			for ( int i = 0; i < cr.getHeaderCount(); i++ ) {
				if (cr.getHeader(i).equals("timestamp")) continue;
				names.add(cr.getHeader(i));
			}
			List<DoubleSeries> datas = new ArrayList<DoubleSeries>();
			for ( int i = 0; i < cr.getHeaderCount(); i++ ) {
				if (cr.getHeader(i).equals("timestamp")) continue;
				datas.add(new DoubleSeries(cr.getHeader(i)));
			}
			while(cr.readRecord()){
				for ( int i = 0, j = 0; i < cr.getHeaderCount(); i++ ) {
					if (cr.getHeader(i).equals("timestamp")) continue;
					Long timestamp = Long.parseLong(cr.get(cr.getIndex("timestamp")).trim());
					Double value = Double.parseDouble(cr.get(cr.getHeader(i)));
					datas.get(j).add(
						new TimeSeries.Entry<Double> (value, timestamp));
					j++;
				}
			}
//			System.out.println((new File(csvFilePath)).getName().split(".").length);
			res = new MultipleDoubleSeries((new File(csvFilePath)).getName(),datas);
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
