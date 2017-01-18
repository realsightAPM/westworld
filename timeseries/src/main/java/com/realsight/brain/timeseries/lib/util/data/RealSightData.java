package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class RealSightData extends BaseData{
	private Character delimiter = null;
	private Charset charset = null;
	private String artificialFilePath = null;
	
	public RealSightData(char delimiter, Charset charset, String artificialFilePath){
		this.delimiter = delimiter;
		this.charset = charset;
		this.artificialFilePath = artificialFilePath;
	}
	
	public RealSightData(String artificialFilePath){
		this(',', Charset.forName("ISO-8859-1"), artificialFilePath);
	}

	public DoubleSeries getPropertySeries(String name){
		DoubleSeries res = new DoubleSeries(name);
		try {
			CsvReader cr = new CsvReader(artificialFilePath, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("timestamp") == -1)
				throw new IOException("File not exists timestamp.");
			Long pre_timestamp = -1L;
			while(cr.readRecord()){
				Long timestamp = new Long(cr.get("timestamp"));
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
