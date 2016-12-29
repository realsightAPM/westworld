package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class Coal {
	private Character delimiter = null;
	private Charset charset = null;
	private String coalFilePath = null;
	
	public Coal(char delimiter, Charset charset, String coalFilePath){
		this.delimiter = delimiter;
		this.charset = charset;
		this.coalFilePath = coalFilePath;
	}
	
	public Coal(String coalFilePath){
		this(',', Charset.forName("ISO-8859-1"), coalFilePath);
	}

	public DoubleSeries getPropertySeries(String name){
		return getPropertySeries(name, 0);
	}
	public DoubleSeries getPropertySeries(String name, int delay){
		DoubleSeries res = new DoubleSeries(name+"-"+delay);
		try {
			CsvReader cr = new CsvReader(coalFilePath, delimiter, charset);
			cr.readHeaders();
			Queue<String> que = new LinkedList<String>();
			for(int i = 0; i < delay; i++){
				cr.readRecord();
				que.add(cr.get(name));
			}
			if(cr.getIndex("time") == -1)
				throw new IOException("File not exists timestamp.");
			Long pre_timestamp = -1L;
			while(cr.readRecord()){
				que.add(cr.get(name));
				Long timestamp = new Long(cr.get("time"));
				if (pre_timestamp.equals(timestamp)) continue;
				pre_timestamp = timestamp;
				Double value = new Double(que.poll());
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
