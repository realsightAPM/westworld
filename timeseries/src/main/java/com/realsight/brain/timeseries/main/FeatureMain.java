package com.realsight.brain.timeseries.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.lib.model.bow.audio.DictionaryExtraction;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.util.data.Sound;

public class FeatureMain {
	public static void main(String[] args) throws Exception {
		File rootTrain = new File(Paths.get(System.getProperty("user.dir"), "target", "data", "Train").toString());
		File rootTest = new File(Paths.get(System.getProperty("user.dir"), "target", "data", "Test").toString());
		DictionaryExtraction FE = new DictionaryExtraction(0.0, 1.1);
		for(File tFile : rootTrain.listFiles()){
			if(tFile.isDirectory()){
				for(File tDir : tFile.listFiles()){
					if(tDir.isDirectory()) continue;
					DoubleSeries testSeries = new Sound(tDir.toString()).getPropertySeries();
					FE.addSeries(testSeries);
				}
			}
		}
		FE.run();
		OutputStreamWriter train = new OutputStreamWriter( 
				new FileOutputStream(Paths.get(System.getProperty("user.dir"), "target", "sunmuxin5.csv").toString()));
		int cla = 0;
		for(File file : rootTrain.listFiles()){
			cla += 1;
			if(file.isDirectory()){
				System.out.println(file.getPath());
				for(File dir : file.listFiles()){
					if(dir.isDirectory()) continue;
					DoubleSeries trainSeries = new Sound(dir.toString()).getPropertySeries();
					double[] fe = FE.getFeature(trainSeries);
					for(int i = 0; i < fe.length; i++) {
//						if(i != 0) train.write(",");
						train.write(fe[i]+",");
					}
					train.write(cla + "");
					train.write("\n");
				}
			}
		}
		
//		OutputStreamWriter test = new OutputStreamWriter( 
//				new FileOutputStream(Paths.get(System.getProperty("user.dir"), "target", "test.txt").toString()));
		int clb = 0;
		for(File file : rootTest.listFiles()){
			clb += 1;
			if(file.isDirectory()){
				System.out.println(file.getPath());
				for(File dir : file.listFiles()){
					if(dir.isDirectory()) continue;
					DoubleSeries trainSeries = new Sound(dir.toString()).getPropertySeries();
					double[] fe = FE.getFeature(trainSeries);
					
					for(int i = 0; i < fe.length; i++) {
//						if(i != 0) train.write(" ");
						train.write(fe[i]+",");
					}
					train.write(clb + "");
					train.write("\n");
				}
			}
		}
		train.close();
//		test.close();
//		for(File file : rootTest.listFiles()){
//			if(file.isDirectory()){
//				System.out.println(file.getPath());
////				if(!file.getName().equals("Sad")) continue;
//				for(File dir : file.listFiles()){
//					if(dir.isDirectory()) continue;
//					DoubleSeries trainSeries = new Sound(dir.toString()).getPropertySeries();
//					double[] fe = FE.getFeature(trainSeries);
//					File featureFile = new File(Paths.get(file.getPath(), "feature").toString());
//					if(! featureFile.exists()){
//						featureFile.mkdirs();
//					}
//					OutputStreamWriter osw = new OutputStreamWriter( 
//							new FileOutputStream(Paths.get(featureFile.getPath(), dir.getName()).toString()));
//					for(int i = 0; i < fe.length; i++) {
//						if(i != 0) osw.write(", ");
//						osw.write(fe[i]+"");
//					}
//					osw.write("\n");
//					osw.close();
//				}
//			}
//		}
	}
}
