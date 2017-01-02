package com.realsight.brain.timeseries.example;


import java.io.File;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.model.measure.DistanceMeasure;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;
import com.realsight.brain.timeseries.lib.util.plot.Plot;

/**
 * @author Sun Muxin
 * 
 */ 
public class MeasureExample {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String root = new File(new File(System.getProperty("user.dir")).getParent()).getParent();
		String nabPath = Paths.get(root, "NAB").toString();
//		AnormalyMain.run(nabPath);
		String localDir = Paths.get(nabPath, "results", "null", "realTweets", "null_Twitter_volume_CRM.csv").toString();
		char delimiter = ',';
		Charset charset = Charset.forName("ISO-8859-1");
		DoubleSeries nSeries = new DoubleSeries("series name");
		try {
			CsvReader cr = new CsvReader(localDir, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("timestamp") == -1)
				throw new IOException("File not exists timestamp.");
			while(cr.readRecord()){
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
				double value = Double.parseDouble(cr.get("value"));
				nSeries.add(new TimeSeries.Entry<Double>(value, timestamp));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		DoubleSeries a = nSeries.subSeries(0, 100);
		DoubleSeries b = nSeries.subSeries(1000, 1100);
		Plot.plot(a, b);
		DistanceMeasure dm = DistanceMeasure.build();
		System.out.println(dm.distance(a, b));
	}
}
