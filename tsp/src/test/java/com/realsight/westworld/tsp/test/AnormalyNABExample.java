package com.realsight.westworld.tsp.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.realsight.westworld.tsp.api.OnlineAnomalyDetectionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;

public class AnormalyNABExample {
	private int scope = 30;
	private AnormalyNABExample(String dir) {
	}
	
	public DoubleSeries detectorNABSeriesAnomaly(String dir) throws Exception{
		System.out.println(dir);
		TimeseriesData td = new TimeseriesData(dir);
		DoubleSeries nSeries = td.getPropertyDoubleSeries("value");
//		Plot.plot("sbsb", nSeries);
		int n = nSeries.size();
		this.scope = (int) (Math.min(0.15 * n, 0.15 * 1000));
//		System.out.println(this.scope);
		OnlineAnomalyDetectionAPI oad = new OnlineAnomalyDetectionAPI();
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
	
	public static void run(String nabPath) throws Exception{
		File root = new File(Paths.get(nabPath, "null").toString());
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
					AnormalyNABExample htm = new AnormalyNABExample(dir.getPath());
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
	
	public static void main(String[] args) throws Exception {
		String nabPath = System.getProperty("user.dir");
		AnormalyNABExample.run(nabPath);
	}
}
