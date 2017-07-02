package com.realsight.westworld.tsp.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.JSONObject;

import com.realsight.westworld.tsp.api.OnlineAnomalyDetectionAPI;
import com.realsight.westworld.tsp.lib.model.LoggerAnalysis;
import com.realsight.westworld.tsp.lib.model.analysis.context.LoggerContext;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleStringSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

@SuppressWarnings("unused")
public class AnomalyDetectionExample {
	private static String splitedchar = "  ";
	public MultipleDoubleSeries detectorNABSeriesAnomaly(File file) throws Exception{
		TimeseriesData td = new TimeseriesData(file.getAbsolutePath());
		DoubleSeries nSeries = td.getPropertyDoubleSeries("maxYVal");
		int n = nSeries.size();
		int scope = (int) (Math.min(0.15 * n, 0.15 * 3000));
		OnlineAnomalyDetectionAPI oad = new OnlineAnomalyDetectionAPI();
		oad.update(nSeries);
		DoubleSeries realAnomalys = new DoubleSeries("real Anomalys");
		DoubleSeries returnAnomalys = new DoubleSeries("return Anomalys");
		for ( int i = 0; i < nSeries.size(); i++ ) {
			if (i%500 == 0) System.out.print(i + " -> ");
			double value = nSeries.get(i).getItem();
			Long timestamp = nSeries.get(i).getInstant();
			Entry<Double> ad = oad.detection(value, timestamp);
			double realAnomaly = 0;
			if (ad != null) {
				realAnomaly = oad.detection(value, timestamp).getItem();
			}
			double returnAnomaly = realAnomaly;
			if (realAnomalys.size() < scope)
				returnAnomaly = 0.0;
			realAnomalys.add(new Entry<Double>(realAnomaly, timestamp));
			returnAnomalys.add(new Entry<Double>(returnAnomaly, timestamp));
		}
		System.out.print(nSeries.size() + "\n");
		return new MultipleDoubleSeries(file.getName(), returnAnomalys, nSeries);
	}
	
	public static void run_detection(Path metricPath, Path resultPath) throws Exception{
		for(File file : metricPath.toFile().listFiles()){
			if(file.isDirectory()){
				continue;
			}
			AnomalyDetectionExample htm = new AnomalyDetectionExample();
			MultipleDoubleSeries mSeries = htm.detectorNABSeriesAnomaly(file);
			resultPath.toFile().mkdirs();
			System.out.println(resultPath.toString()+File.separator+file.getName());
			OutputStream os = new FileOutputStream(resultPath.toString()+File.separator+file.getName());
	        OutputStreamWriter writer = new OutputStreamWriter(os);
	        writer.write("timestamp,anomaly_score,value,file\n");
	        for(int i = 0; i < mSeries.size(); i++){
	        	writer.write(mSeries.get(i).getInstant()+","+mSeries.get(i).getItem().get(0)+
	        			","+mSeries.get(i).getItem().get(1)+","+file.getName()+"\n");
	        }
	        writer.close();
		}
	}
	
	public static void print_anomaly(Path resultPath, double th) {
		List<DoubleSeries> nSeries = new ArrayList<DoubleSeries>();
		for(File file : resultPath.toFile().listFiles()){
			if(file.isDirectory()){
				continue;
			}
			TimeseriesData td = new TimeseriesData(file.getAbsolutePath());
			DoubleSeries series = td.getPropertyDoubleSeries("anomaly_score");
			nSeries.add(series);
		}
		MultipleDoubleSeries mSeries = new MultipleDoubleSeries("anomalys", nSeries);
		System.out.println("timestamp, date, metric, score");
		for (int i = 0; i < mSeries.size(); i++) {
			Long timestamp = mSeries.get(i).getInstant();
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			cal.setTimeInMillis(timestamp);
			String time = String.format("%d/%02d/%02d %02d:%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			
			for (int j = 0; j < mSeries.getColumnNum(); j++) {
				Double as = mSeries.get(i).getItem().get(j).doubleValue();
				if (as > th) {
					System.out.println(timestamp + ", " + time + ", " + mSeries.getProperty_list().get(j) + ", " + as);
				}
			}
		}
	}
	
	public static void print_logger(Path logFilePath) {
		TimeseriesData td = new TimeseriesData(logFilePath.toString());
		MultipleStringSeries lSeries = td.getPropertyStringSeries();
		lSeries.sort();
		int index = lSeries.indexOf("application");
		int ex_index = lSeries.indexOf("exception");
		int el_index = lSeries.indexOf("elapsed");
		int app_index = lSeries.indexOf("application");
		System.out.println("url, elapsed, timestamp, time");
		for (int i = 0; i < lSeries.size(); i++) {
			String url = lSeries.get(i).getItem().get(index);
			Long timestamp = lSeries.get(i).getInstant();
			String elapsed = lSeries.get(i).getItem().get(el_index);
			String exception = lSeries.get(i).getItem().get(ex_index);
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			cal.setTimeInMillis(timestamp);
			String time = String.format("%d/%02d/%02d %02d:%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			if (exception.equals("1")) {
				System.out.println(url + ", " + elapsed + ", " + timestamp + ", " + time);
			}
			
		}
	}
	
	public static void run_analysis(Path resultPath, Path logFilePath, Long start_timestamp, Long time_windows, double th) throws Exception{
		List<DoubleSeries> nSeries = new ArrayList<DoubleSeries>();
		for(File file : resultPath.toFile().listFiles()){
			if(file.isDirectory()){
				continue;
			}
			TimeseriesData td = new TimeseriesData(file.getAbsolutePath());
			DoubleSeries series = td.getPropertyDoubleSeries("anomaly_score");
			nSeries.add(series);
		}
		MultipleDoubleSeries mSeries = new MultipleDoubleSeries("anomalys", nSeries);
//		Plot.plot(mSeries);
		TimeseriesData td = new TimeseriesData(logFilePath.toString());
		MultipleStringSeries lSeries = td.getPropertyStringSeries();
		lSeries.sort();
		
		int m = 0, l = 0;
		while(m < mSeries.size()) {
			if (mSeries.get(m).getInstant() < start_timestamp) m++;
			else break;
		}
		while(l < lSeries.size()) {
			if (lSeries.get(l).getInstant() < start_timestamp) l++;
			else break;
		}
		start_timestamp += time_windows;
		while((m<mSeries.size()) || (l<lSeries.size())) {
			
			System.out.print("Time               "+splitedchar);
			for (int i = 0; i < mSeries.getColumnNum(); i++) {
				System.out.print(mSeries.getColumn(i).getName()+splitedchar);
			}
			System.out.print("Total"+splitedchar);
			System.out.print("Log_er" + splitedchar);
			System.out.println("Slow");
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			cal.setTimeInMillis(start_timestamp);
			String time = String.format("%d/%02d/%02d %02d:%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			System.out.print(time+splitedchar);
			
			int[] err_num = new int[mSeries.getColumnNum()];
			while(m < mSeries.size()) {
				if (mSeries.get(m).getInstant() < start_timestamp) {
					for (int i = 0; i < mSeries.getColumnNum(); i++){
						double as = mSeries.get(m).getItem().get(i);
						if (as > th) {
							err_num[i] += 1;
						}
					}
					m++;
				}
				else break;
			}
			int err_total = 0;
			for (int i = 0; i < err_num.length; i++) {
				err_total += err_num[i];
				System.out.print(err_num[i]);
				for (int j = 1; j < mSeries.getProperty_list().get(i).length(); j++) {
					System.out.print(" ");
				}
				System.out.print(splitedchar);
			}
			System.out.print(err_total+"    "+splitedchar);
			int ex_num = 0;
			int sl_num = 0;
			while(l < lSeries.size()) {
				if (lSeries.get(l).getInstant() < start_timestamp) {
					int ex_index = lSeries.indexOf("exception");
					int el_index = lSeries.indexOf("elapsed");
					String exception = lSeries.get(l).getItem().get(ex_index);
					String elapsed = lSeries.get(l).getItem().get(el_index);
					if (exception.equals("1")) ex_num += 1;
					else if (Integer.parseInt(elapsed) > 3000) sl_num += 1;
					l++;
				}
				else break;
			}
			System.out.print(ex_num+"      "+splitedchar);
			System.out.println(sl_num);
			System.out.println(start_timestamp);
			System.out.println("");
			
			start_timestamp += time_windows;
		}
	}
	
	public static void run_logger_analysis(Path logFilePath, Long logger_analysis_timestamp, Long time_windows, int min_num) throws Exception{
		LoggerAnalysis la = new LoggerAnalysis(time_windows);
		TimeseriesData td = new TimeseriesData(logFilePath.toString());
		MultipleStringSeries lSeries = td.getPropertyStringSeries();
		lSeries.sort();
		int index = lSeries.indexOf("application");
		int ex_index = lSeries.indexOf("exception");
		int el_index = lSeries.indexOf("elapsed");
		int app_index = lSeries.indexOf("application");
//		for (int i = 0; i < lSeries.size(); i++) {
//			String url = lSeries.get(i).getItem().get(index);
//			Long timestamp = lSeries.get(i).getInstant();
//			String elapsed = lSeries.get(i).getItem().get(el_index);
//			if (timestamp > logger_analysis_timestamp+time_windows) break;
//			JSONObject jo = new JSONObject();
//			jo.put("elapsed", elapsed);
//			la.insert(url, "/", jo.toString(), timestamp);
//		}
//		
//		System.out.println("RCA rank : ");
//		List<LoggerContext> lcs = la.getRank(min_num, 1);
//		for (LoggerContext lc : lcs) {
//			System.out.print(lc.getScore() + ",");
//			System.out.print(lc.getUrl() + ",");
//			System.out.print(lc.getAverage_fre() + ",");
//			System.out.print(lc.getRecent_fre() + ",");
//			System.out.print(lc.getAverage_elapsed() + ",");
//			System.out.print(lc.getRecent_elapsed());
//			System.out.println("");
//		}
		
		System.out.println("");
		System.out.println("Exception and Slow Logger : ");
		
		for (int i = 0; i < lSeries.size(); i++) {
			Long timestamp = lSeries.get(i).getInstant();
			if (timestamp < logger_analysis_timestamp) continue;
			if (timestamp > logger_analysis_timestamp + time_windows) continue;
			String application = lSeries.get(i).getItem().get(app_index);
			String exception = lSeries.get(i).getItem().get(ex_index);
			String elapsed = lSeries.get(i).getItem().get(el_index);
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			cal.setTimeInMillis(timestamp);
			String time = String.format("%d/%02d/%02d %02d:%02d:%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			if (exception.equals("1")) {
				System.out.print("exception" + ",");
				System.out.print(application + ",");
				System.out.print(timestamp + ",");
				System.out.print(time);
				System.out.println("");
			}
			else if (Integer.parseInt(elapsed) > 3000) {
				System.out.print("slow" + ",");
				System.out.print(application + ",");
				System.out.print(timestamp + ",");
				System.out.print(time + ",");
				System.out.print(elapsed);
				System.out.println("");
			}
		}
	}
	
	public static void run_analysis_url(Path logFilePath, Long logger_analysis_timestamp, Long time_windows, String url) throws Exception{
		LoggerAnalysis la = new LoggerAnalysis(time_windows);
		TimeseriesData td = new TimeseriesData(logFilePath.toString());
		MultipleStringSeries lSeries = td.getPropertyStringSeries();
		lSeries.sort();
		int index = lSeries.indexOf("application");
		int ex_index = lSeries.indexOf("exception");
		int el_index = lSeries.indexOf("elapsed");
		int app_index = lSeries.indexOf("application");
		
		List<Double> x = new ArrayList<Double>();
		List<Double> y1 = new ArrayList<Double>();
		List<Double> y2 = new ArrayList<Double>();
		double mx = 0.0;
		
		for (int i = 0; i < lSeries.size(); i++) {
			Long timestamp = lSeries.get(i).getInstant();
			String u = lSeries.get(i).getItem().get(index);
			String application = lSeries.get(i).getItem().get(app_index);
			String exception = lSeries.get(i).getItem().get(ex_index);
			String elapsed = lSeries.get(i).getItem().get(el_index);
			if (! u.contains(url)) continue;
			x.add(y2.size()+0.0);
			mx = Math.max(mx, Double.parseDouble(elapsed));
			y1.add(Double.parseDouble(elapsed));
			if (timestamp < logger_analysis_timestamp) {
				y2.add(0.0);
			} else {
				y2.add(1.0);
			}
		}
		for (int i = 0; i < y2.size(); i++) {
			y2.set(i, y2.get(i)>0?mx:0);
		}
		Plot.plot(url, x, y1, y2);
	}
	
	public static void main(String[] args) throws Exception {
		
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("AD", "AnomalyDetection", false, "anomaly detection");
		options.addOption("MP", "MetricPath", true, "metric path default is data/metric");
		options.addOption("ARP", "AnomalyResultPath", true, "anomaly result path default is data/metric_result");
		options.addOption("LFP", "LoggerFilePath", true, "logger file path default is data/log");
		options.addOption("ST", "StartTimestamp", true, "start timestamp");
		options.addOption("TW", "TimeWindows", true, "time windows");
		options.addOption("ATV", "AnomalyThresholdValue", true, "anomaly threshold value");
		options.addOption("ADU", "AnomalyDetectionUpdate", false, "anomaly detection update");
		
		options.addOption("LA", "LoggerAnalysis", false, "logger analysis");
		options.addOption("LAT", "LoggerAnalysisTimestamp", true, "logger analysis timestamp");
		options.addOption("MN", "MinNum", true, "min");
		options.addOption("AURL", "AnalysisURL", true, "analysis, URL");
		
		options.addOption("PA", "PrintAnomaly", false, "print anomaly");
		options.addOption("PL", "PrintLogger", false, "print logger");
		
		options.addOption("H", false, "help");
		
		CommandLine commandLine = parser.parse(options, args);
		
		if (commandLine.hasOption("H")) {
			System.out.println("Anomaly Detection Example arguments:\n");
			for (Option option : options.getOptions()) {
				System.out.println("\t-" + option.getOpt());
				System.out.print("\t\t");
				System.out.println(option.getDescription());
			}
			return ;
		}
		
		Path metricPath = Paths.get(System.getProperty("user.dir"), "data", "metric");
		Path resultPath = Paths.get(System.getProperty("user.dir"), "data", "metric_result");
		Path logFilePath = Paths.get(System.getProperty("user.dir"), "data", "log", "log.csv");
		Long start_timestamp = 1492608464065L;
		Long time_windows = 1000L * 60 * 300;
		Double th = 0.7;
		Long logger_analysis_timestamp = 1492608464065L;
		int mn = 30;
		
		if (commandLine.hasOption("AD")) {
			if (commandLine.hasOption("MP")){
				metricPath = Paths.get(commandLine.getOptionValue("MP"));
			}
			if (commandLine.hasOption("ARP")) {
				resultPath = Paths.get(commandLine.getOptionValue("ARP"));
			}
			if (commandLine.hasOption("LFP")) {
				logFilePath = Paths.get(commandLine.getOptionValue("LFP"));
			}
			if (commandLine.hasOption("ST")) {
				start_timestamp = Long.parseLong(commandLine.getOptionValue("ST"));
			}
			if (commandLine.hasOption("TW")) {
				time_windows = 1000L * 60 * Long.parseLong(commandLine.getOptionValue("TW"));
			}
			if (commandLine.hasOption("ATV")) {
				th = Double.parseDouble(commandLine.getOptionValue("ATV"));
			}
			if (commandLine.hasOption("ADU")) {
				AnomalyDetectionExample.run_detection(metricPath, resultPath);
			}
			AnomalyDetectionExample.run_analysis(resultPath, logFilePath, start_timestamp, time_windows, th);
		}
		if (commandLine.hasOption("LA")) {
			if (commandLine.hasOption("LFP")) {
				logFilePath = Paths.get(commandLine.getOptionValue("LFP"));
			}
			if (commandLine.hasOption("TW")) {
				time_windows = 1000L * 60 * Long.parseLong(commandLine.getOptionValue("TW"));
			}
			if (commandLine.hasOption("LAT")) {
				logger_analysis_timestamp = Long.parseLong(commandLine.getOptionValue("LAT"));
			}
			if (commandLine.hasOption("MN")) {
				mn = Integer.parseInt(commandLine.getOptionValue("MN"));
			}
			run_logger_analysis(logFilePath, logger_analysis_timestamp, time_windows, mn);
		}
		if (commandLine.hasOption("AURL")) {
			String url = commandLine.getOptionValue("AURL");
			if (commandLine.hasOption("LFP")) {
				logFilePath = Paths.get(commandLine.getOptionValue("LFP"));
			}
			if (commandLine.hasOption("TW")) {
				time_windows = 1000L * 60 * Long.parseLong(commandLine.getOptionValue("TW"));
			}
			if (commandLine.hasOption("LAT")) {
				logger_analysis_timestamp = Long.parseLong(commandLine.getOptionValue("LAT"));
			}
			run_analysis_url(logFilePath, logger_analysis_timestamp, time_windows, url);
		}
		
		if (commandLine.hasOption("PA")) {
			if (commandLine.hasOption("ARP")) {
				resultPath = Paths.get(commandLine.getOptionValue("ARP"));
			}
			if (commandLine.hasOption("ATV")) {
				th = Double.parseDouble(commandLine.getOptionValue("ATV"));
			}
			print_anomaly(resultPath, th);
		}
		
		if (commandLine.hasOption("PL")) {
			if (commandLine.hasOption("LFP")) {
				logFilePath = Paths.get(commandLine.getOptionValue("LFP"));
			}
			print_logger(logFilePath);
		}
	}
}
