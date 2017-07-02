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
import com.realsight.westworld.tsp.lib.series.TimeSeries;
import com.realsight.westworld.tsp.lib.solr.SolrReader;
import com.realsight.westworld.tsp.lib.solr.SolrWriter;
import com.realsight.westworld.tsp.lib.util.Entry;
import com.realsight.westworld.tsp.lib.util.TimeUtil;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;
import com.realsight.westworld.tsp.sever.ServerAnomaly;
import com.realsight.westworld.tsp.sever.ServerLogger;

@SuppressWarnings("unused")
public class ToSolrExample {
	public static void run_detection(String ... series_names) throws Exception{
		for(String series_name : series_names){
			SolrReader sr = new SolrReader("http://localhost:8080/solr/apm/", "series_name_s:"+series_name);
			SolrWriter sw = new SolrWriter("http://localhost:8080/solr/apm/");
			sr.setSort("timestamp_l", true);
			Thread run = new Thread(new ServerAnomaly(sr, sw));
			run.start();
		}
	}
	
	public static void run_logger_analysis() throws Exception{
		SolrReader sr = new SolrReader("http://localhost:8080/solr/apm/", "series_name_s:LOGGER");
		SolrWriter sw = new SolrWriter("http://localhost:8080/solr/apm/");
		sr.setSort("timestamp_l", true);
		Thread run = new Thread(new ServerLogger(sr, sw));
		run.start();
	}
	
	
	public static void main(String[] args) throws Exception {
		
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("AD", "AnomalyDetection", false, "anomaly detection");
		
		options.addOption("LA", "LoggerAnalysis", false, "logger analysis");
		
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
		
		if (commandLine.hasOption("AD")) {
			ToSolrExample.run_detection("CPU_LOAD_SYSTEM");
		}
		
		if (commandLine.hasOption("LA")) {
			ToSolrExample.run_logger_analysis();
		}
	}
}
