package com.realsight.brain.timeseries.main;

import java.io.File;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.lib.model.htm.SoundHierarchy;
import com.realsight.brain.timeseries.lib.series.DoubleSeries;
import com.realsight.brain.timeseries.lib.util.data.Sound;

public class HTMMain {
	public static void main(String[] args) throws Exception {
		File rootTrain = new File(Paths.get(System.getProperty("user.dir"), "target", "data", "Train").toString());
		File rootTest = new File(Paths.get(System.getProperty("user.dir"), "target", "data", "Test").toString());
		int acc_num = 0, total_num = 0;
		for(File file : rootTest.listFiles()){
			if(file.isDirectory()){
				if(!file.getName().equals("Sad")) continue;
				for(File dir : file.listFiles()){
					if(dir.isDirectory()) continue;
					DoubleSeries trainSeries = new Sound(dir.toString()).getPropertySeries();
					SoundHierarchy sh = SoundHierarchy.build(trainSeries);
					sh.train(trainSeries);
					double max_active = 0.0;
					String className = "", fileNmae = "";
					for(File tFile : rootTrain.listFiles()){
						if(tFile.isDirectory()){
							for(File tDir : tFile.listFiles()){
								if(tDir.isDirectory()) continue;
								DoubleSeries testSeries = new Sound(tDir.toString()).getPropertySeries();
								double tmp = sh.test(testSeries);
								if(tmp > max_active){
									max_active = tmp;
									className = tFile.getName();
									fileNmae = tDir.getName();
								}
//								System.out.println(tDir.getName() + ", " + tFile.getName() + ", " + tmp);
							}
						}
					}
					
					if(className.equals("")) continue;
					if(className.equals(file.getName()))
						acc_num += 1;
					total_num += 1;
					System.out.print("train -> " + dir.getName() + ", " + file.getName() + ", ");
					System.out.println(acc_num + ", " + total_num + ", " + (100.0*acc_num/total_num) + "%");
					System.out.println(className + ", " + fileNmae + "," + max_active);
				}
			}
		}
		
	}
}
