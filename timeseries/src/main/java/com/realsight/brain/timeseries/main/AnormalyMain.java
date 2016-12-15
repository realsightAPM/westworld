package com.realsight.brain.timeseries.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.realsight.brain.timeseries.lib.model.htm.AnormalyHierarchy;
import com.realsight.brain.timeseries.lib.model.segment.AnormalySegment;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries.Entry;
import com.realsight.brain.timeseries.lib.util.data.Anomaly;

public class AnormalyMain {
	private final int scope = 30;
	private double minValue;
	private double maxValue;
	private AnormalyMain(String dir) {
		Anomaly.setLocalDir(dir);
		DoubleSeries nSeries = Anomaly.getPropertySeries("value");
		this.minValue = nSeries.min();
		this.maxValue = nSeries.max();
	}
	
	public DoubleSeries detectorNABSeriesAnomaly(String dir) throws Exception{
		System.out.println(dir);
		Anomaly.setLocalDir(dir);
		DoubleSeries nSeries = Anomaly.getPropertySeries("value");
		DoubleSeries anormlySeries = Anomaly.getPropertySeries("label");
		int n = nSeries.size();
		AnormalyHierarchy anormlyHTM = AnormalyHierarchy.build(null, minValue, maxValue);
		AnormalySegment anormlySegment = AnormalySegment.build(nSeries.subSeries(0, n/10), minValue, maxValue);
		DoubleSeries anormlys = new DoubleSeries("anormlys");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			double value = nSeries.get(i).getItem();
			Long timestamp = nSeries.get(i).getInstant();
			boolean label = anormlySeries.get(i).getItem()>0.5;
//			double anormly = anormlySegment.detectorAnomaly(value, timestamp);
			double anormly = anormlyHTM.detectorAnomaly(value, timestamp);
			anormly += anormlySegment.detectorAnomaly(value, timestamp, label)/2;
			if (anormlys.subSeries(Math.max(0, i-scope), i).max() >= 0.81) 
				anormly = 0.0;
			anormlys.add(new Entry<Double>(anormly, timestamp));
		}
		return anormlys;
	}
	
	public static void run(String nabPath) throws Exception{
		File root = new File(Paths.get(nabPath, "results","null").toString());
		for(File file : root.listFiles()){
			if(file.isDirectory()){
				for(File dir : file.listFiles()){
					if(dir.isDirectory())
						continue;
					List<String> t = new ArrayList<String>();
					List<Double> s = new ArrayList<Double>();
					List<String> l = new ArrayList<String>();
					List<String> FP = new ArrayList<String>();
					List<String> FN = new ArrayList<String>();
					List<String> S = new ArrayList<String>();
					Scanner sin = new Scanner(dir);
					sin.nextLine();
					while(sin.hasNext()){
						String line = sin.nextLine();
						t.add(line.split(",")[0]);
						s.add(Double.parseDouble(line.split(",")[1]));
						l.add(line.split(",")[3]);
						FP.add(line.split(",")[4]);
						FN.add(line.split(",")[5]);
						S.add(line.split(",")[6]);
					}
					sin.close();
					AnormalyMain htm = new AnormalyMain(dir.getPath());
					DoubleSeries a = htm.detectorNABSeriesAnomaly(dir.getPath());
					String resultFileName = dir.getPath().replace("null", "realsight");
					String resultPath = new File(resultFileName).getParent();
					if(!new File(resultPath).exists()){
						new File(resultPath).mkdirs();
					}
					OutputStream os = new FileOutputStream(resultFileName);
			        OutputStreamWriter writer = new OutputStreamWriter(os);
			        writer.write("timestamp,value,anomaly_score,label,S(t)_reward_low_FP_rate,S(t)_reward_low_FN_rate,S(t)_standard\n");
			        for(int i = 0; i < s.size(); i++){
			        	writer.write(t.get(i)+","+s.get(i)+","+a.get(i).getItem()+","+l.get(i)+","+FP.get(i)+","+FN.get(i)+","+S.get(i)+"\n");
			        }
			        writer.close();
				}
			}
		}
	}
}
