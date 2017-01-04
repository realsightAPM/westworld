package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class CoalData {
	private Character delimiter = null;
	private Charset charset = null;
	private String coalFilePath = null;
	
	public CoalData(char delimiter, Charset charset, String coalFilePath){
		this.delimiter = delimiter;
		this.charset = charset;
		this.coalFilePath = coalFilePath;
	}
	
	public CoalData(String coalFilePath){
		this(',', Charset.forName("ISO-8859-1"), coalFilePath);
	}

	public DoubleSeries getPropertySeries(String name){
		DoubleSeries res = new DoubleSeries(name);
		try {
			CsvReader cr = new CsvReader(coalFilePath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("time") == -1)
				throw new IOException("File not exists timestamp.");
			Long pre_timestamp = -1L;
			while(cr.readRecord()){
				Long timestamp = new Long(cr.get("time"));
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
