package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class TestData extends BaseData{
	private Character delimiter = null;
	private Charset charset = null;
	private String coalFilePath = null;
	
	public TestData(char delimiter, Charset charset, String coalFilePath){
		this.delimiter = delimiter;
		this.charset = charset;
		this.coalFilePath = coalFilePath;
	}
	
	public TestData(String coalFilePath){
		this(',', Charset.forName("ISO-8859-1"), coalFilePath);
	}

	public DoubleSeries getPropertySeries(String name){
		DoubleSeries res = new DoubleSeries(name);
		try {
			CsvReader cr = new CsvReader(coalFilePath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("times") == -1)
				throw new IOException("File not exists timestamp.");
			Long pre_timestamp = -1L;
			while(cr.readRecord()){
				String time = cr.get("times");
				System.out.println(time);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = sdf.parse(time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Long timestamp = date.getTime();
				if (pre_timestamp.equals(timestamp)) continue;
				pre_timestamp = timestamp;
				Double value = new Double(cr.get(name));
				res.add(new TimeSeries.Entry<Double>(value, timestamp));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
