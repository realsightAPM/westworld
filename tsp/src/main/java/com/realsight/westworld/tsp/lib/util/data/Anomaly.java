package com.realsight.westworld.tsp.lib.util.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import com.realsight.westworld.tsp.lib.csv.CsvReader;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries;

public class Anomaly {
	private static char delimiter = ',';
	private static Charset charset = Charset.forName("ISO-8859-1");
	private static String localDir = null;
	
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
				String time = cr.get("timestamp");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = sdf.parse(time);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Long timestamp = date.getTime();
				double num = Double.parseDouble(que.poll());
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
