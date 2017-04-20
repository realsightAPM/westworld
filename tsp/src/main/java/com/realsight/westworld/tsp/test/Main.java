package com.realsight.westworld.tsp.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.util.*;

import com.realsight.westworld.tsp.api.OnlineAnormalyDetectionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;

public class Main {
	
	public DoubleSeries detectorNABSeriesAnomaly(String dir) throws Exception{
		System.out.println(dir);
		TimeseriesData td = new TimeseriesData(dir);
		DoubleSeries nSeries = td.getPropertyDoubleSeries("value");
//		Plot.plot("sbsb", nSeries);
		int n = nSeries.size();
		this.scope = (int) (Math.min(0.15 * n, 0.15 * 1000));
//		System.out.println(this.scope);
		OnlineAnormalyDetectionAPI oad = new OnlineAnormalyDetectionAPI();
		oad.update(nSeries);
		DoubleSeries realAnormalys = new DoubleSeries("real anormalys");
		DoubleSeries returnAnormalys = new DoubleSeries("return anormalys");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			if (i%500 == 0) System.out.print(i + " -> ");
			double value = nSeries.get(i).getItem();
			Long timestamp = nSeries.get(i).getInstant();
			Entry<Double> ad = oad.detection(value, timestamp);
			double realAnormaly = 0;
			if (ad != null) {
				realAnormaly = oad.detection(value, timestamp).getItem();
			}
			double returnAnormaly = realAnormaly;
			if (realAnormalys.subSeries(Math.max(0, i-scope), i).max() >= 0.65) 
				returnAnormaly = 0.0;
			if (realAnormalys.size() < this.scope)
				returnAnormaly = 0.0;
			realAnormalys.add(new Entry<Double>(realAnormaly, timestamp));
			returnAnormalys.add(new Entry<Double>(returnAnormaly, timestamp));
		}
		System.out.print(nSeries.size() + "\n");
//		Plot.plot("sb", realAnormalys);
		return returnAnormalys;
	}
	
	public static void run(String pathA, String pathB) throws Exception{
		File A = new File(pathA);
		File B = new File(pathB);
		if (!A.exists()) return ;
		
		RandomAccessFile rafA = new RandomAccessFile(A, "rw");
		RandomAccessFile rafB = new RandomAccessFile(B, "rw");
		FileChannel AFileChannel = rafA.getChannel();
		FileChannel BFileChannel = rafB.getChannel();
		BFileChannel.transferFrom(AFileChannel, 0, AFileChannel.size());
	}
	
	public static void main(String[] args) throws Exception {
		String nabPath = System.getProperty("user.dir");
		Main.run(nabPath);
	}
}
