package com.realsight.brain.timeseries.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;

public class Station {
	private static char delimiter = ',';
	private static Charset charset = Charset.forName("ISO-8859-1");
	private static String localDir = Paths.get(System.getProperty("user.dir"), "target", "data", 
			"Government Center.csv").toString();
	
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
				Double value = 0.0;
				Long timestamp = new Long(cr.get("timestamp"));
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
		Collections.sort(res.getData());
		List<TimeSeries.Entry<Double>> add_ele = new ArrayList<TimeSeries.Entry<Double>>();
		Long pre_timestamp = res.get(0).getInstant();
		Long time_interval = 3600000L;
		for(TimeSeries.Entry<Double> entry : res.getData()){
			Long now_timestamp = entry.getInstant();
			while(now_timestamp-pre_timestamp > time_interval){
				Double value = 0.0;
				if(name.equals("ones")){
					value = 1.0;
				} else if (name.equals("zeros")) {
					value = 0.0;
				}
				add_ele.add(new TimeSeries.Entry<Double>(value, pre_timestamp+time_interval));
				pre_timestamp+=time_interval;
			}
			pre_timestamp = now_timestamp;
		}
		for(TimeSeries.Entry<Double> entry : add_ele){
			res.add(entry);
		}
		Collections.sort(res.getData());
		return res;
	}
}
