package com.realsight.brain.timeseries.example;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.realsight.brain.timeseries.lib.csv.CsvWriter;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.util.data.CoalData;

public class CoalFeatureExtendExample {
	private static final Character delimiter = ',';
	private static final Charset charset = Charset.forName("ISO-8859-1");
	private static final Long day = 1000L * 60L * 60L * 24L;
	private String[] getContents(int coalFileId, DoubleSeries cSeries){
		cSeries.normly();
		String[] contents = new String[cSeries.size()+1];
		contents[0] = String.valueOf(coalFileId);
		for ( int i = 0; i < cSeries.size(); i ++ ){
			contents[i + 1] = String.valueOf(cSeries.get(i).getItem());
		}
		return contents;
	}
	
	private void getFeature(int len) {
		final String fileName = "coal_" + len + ".csv";
		final String fileDir = Paths.get(System.getProperty("user.dir"), 
				"target", "data").toString();
		final String filePath = Paths.get(fileDir, fileName).toString();
		CsvWriter csvWriter = new CsvWriter(filePath, delimiter, charset);
		
		String coalFilesDir = Paths.get(System.getProperty("user.dir"), "target", "data", 
    			"coal").toString();
		File coalFiles = new File(coalFilesDir);
		int coalFileId = 0;
//		System.out.println("CoalFileId,CoalFileName");
		for (String coalFileName : coalFiles.list()){
			coalFileId += 1;
			System.out.println(coalFileId + "," + coalFileName);
			String coalFilePath = Paths.get(coalFilesDir, coalFileName).toString();
			CoalData coal = new CoalData(coalFilePath);
			DoubleSeries dSeries = coal.getPropertySeries("diweijiage");
			dSeries.sort();
			for ( int i = 1, startId = 0; i < dSeries.size(); i++) {
				Long now_timestamp = dSeries.get(i).getInstant();
				Long pre_timestamp = dSeries.get(i-1).getInstant();
				if (now_timestamp > 14 * day + pre_timestamp) {
					startId = i;
				}
				if (now_timestamp-pre_timestamp == 0) {
					System.out.println("sb le.");
					System.out.println(now_timestamp);
					System.out.println(pre_timestamp);
					startId = i;
				}
				Timestamp ts = new Timestamp(dSeries.get(startId).getInstant());
//				if (coalFileId == 57){
//					System.out.println((14 * day)+ " " + (now_timestamp - pre_timestamp) );
//					System.out.println(ts.toString()+ " " + startId + " " + startId);
//				}
				if (i-startId == len) {
					startId += 1;
					if (ts.getMonth() != 11) continue;
					String[] contents = this.getContents(coalFileId, dSeries.subSeries(startId, i));
					try {
						csvWriter.writeRecord(contents);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		csvWriter.close();
	}
	
	public static void main(String[] args) {
		new CoalFeatureExtendExample().getFeature(12);
	}
}
