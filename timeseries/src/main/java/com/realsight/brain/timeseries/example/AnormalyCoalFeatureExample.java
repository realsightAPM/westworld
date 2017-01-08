package com.realsight.brain.timeseries.example;


import java.io.File;
//import java.nio.file.Paths;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Timestamp;

import com.realsight.brain.timeseries.api.OnlineAnormalyDetectionAPI;
import com.realsight.brain.timeseries.lib.csv.CsvReader;
import com.realsight.brain.timeseries.lib.csv.CsvWriter;
import com.realsight.brain.timeseries.lib.model.anomaly.AnormalyDetection;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.series.MultipleDoubleSeries;
import com.realsight.brain.timeseries.lib.series.TimeSeries;
import com.realsight.brain.timeseries.lib.util.Util;
import com.realsight.brain.timeseries.lib.util.data.CoalData;

/**
 * @author Sun Muxin
 * 
 */ 
public class AnormalyCoalFeatureExample {
	/**
	 * @param args
	 * @throws Exception 
	 */
	
	private void detect() {		
		String coalFilesDir = Paths.get(System.getProperty("user.dir"), "target", "data", 
    			"coal").toString();
		File coalFiles = new File(coalFilesDir);
		int coalFileId = 0;
		OnlineAnormalyDetectionAPI detection = null;
//		System.out.println("CoalFileId,CoalFileName");
		for (String coalFileName : coalFiles.list()){
			coalFileId += 1;
			System.out.println(coalFileId + "," + coalFileName);
			String coalFilePath = Paths.get(coalFilesDir, coalFileName).toString();
			CoalData coal = new CoalData(coalFilePath);
			DoubleSeries dSeries = coal.getPropertySeries("diweijiage");
			dSeries.sort();
			dSeries.normly();
			if (detection == (null))
				detection = new OnlineAnormalyDetectionAPI(dSeries, 0.0, 1.0);
			else {
				DoubleSeries anormalys = detection.detectorSeries(dSeries);
				String resultDir = Util.writeCsv(new MultipleDoubleSeries(dSeries, anormalys)).toString();
				System.out.println("anormaly result dir is : " + resultDir);
			}
		}
	}
	public static void main(String[] args) throws Exception {
		new AnormalyCoalFeatureExample().detect();
	}
}
