package com.realsight.westworld.tsp.lib.util.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
/*
 * @author sunmuxin
 */
public class NAB {
	
	private static final double tpWeight = 1.0;
	@SuppressWarnings("unused")
	private static final double fnWeight = 1.0;
	private static final double fpWeight = 0.11;
	@SuppressWarnings("unused")
	private static final double tnWeight = 1.0;
	
	public static double Sigma(double y){
		return 2.0/(1.0+Math.exp(5*y)) - 1.0;
	}
	
	public static double SDA(String dir, double threshold) throws IOException {
		Scanner sin = null;
		try{
			sin = new Scanner(new File(dir));
		} catch(IOException e) {
			throw new IOException("System IO error");
		}
		Map<String, Integer> property = new HashMap<String, Integer>();
		int property_num = 0;
		for(String name : sin.nextLine().split(",")){
			property.put(name, property_num);
			property_num ++;
		}
		List<List<String>> file = new ArrayList<List<String>>();
		while(sin.hasNext()){
			List<String> line = new ArrayList<String>();
			for(String val : sin.nextLine().split(",")){
				line.add(val);
			}
			file.add(line);
		}
		file.add(file.get(0));
		sin.close();
		double scores = 0.0;
		int cnt = 0, tp = 0, fp = 0;
		for(int i = 0, pre_label = -2, auth_r = -1; i < file.size(); i++){
			if(file.get(i).size() < property_num)
				continue;
			if(!property.containsKey("label") || !property.containsKey("anomaly_score"))
				throw new IOException("File no label or anomaly_score property");
			int label_c = property.get("label");
			int anomaly_score_c = property.get("anomaly_score");
			if(Double.parseDouble(file.get(i).get(label_c)) > 0.5){
				if(auth_r==-1 && Double.parseDouble(file.get(i).get(anomaly_score_c))>=threshold)
					auth_r = i;
				if(Double.parseDouble(file.get(i).get(anomaly_score_c))>=threshold)
					tp += 1;
				pre_label = i;
			} else if(pre_label+1 == i){
				if(auth_r == -1){
					scores += -1.0;
					cnt += 1;
				}
				else
					scores += Sigma((auth_r - pre_label)*30.0/(file.size()* 1.0))*tpWeight;
				auth_r = -1;
			} else{
				if(Double.parseDouble(file.get(i).get(anomaly_score_c))>=threshold)
					fp += 1;
				if(Double.parseDouble(file.get(i).get(anomaly_score_c))>=threshold)
					scores += Sigma((i - pre_label)*30.0/(file.size()*1.0))*fpWeight;
			}
			//if(Double.parseDouble(file.get(i).get(anomaly_score_c))>=threshold)
			//System.out.println(i + "," + pre_label + "," + scores + "," + file.get(i).get(label_c) + "," + file.get(i).get(anomaly_score_c));
		}
		System.out.println(dir+","+"count="+cnt+",tp="+tp+",fp="+fp);
		return scores;
	}
	public static double SA(String root_dir, double threshold) throws IOException{
		double Totals_scores = 0.0;
		File root = new File(root_dir);
		for(File file : root.listFiles()){
			if(file.isDirectory()){
				for(File dir : file.listFiles()){
					if(dir.isDirectory())
						continue;
					Totals_scores += SDA(dir.getPath(), threshold);
				}
			}
		}
		return Totals_scores;
	}
	public static void main(String[] args) throws IOException{
		System.out.println(SA("C:/workspace/timeseries/realsight/",0.31));
//		System.out.println(SA("D:/workspace/NAB/results/contextOSE/", 0.76552734376));
//		System.out.println(SDA("D:/workspace/NAB/src/results/neusoft/realTweets/neusoft_Twitter_volume_UPS.csv", 1.1));
//		System.out.println(SDA("D:/workspace/NAB/src/results/neusoft/realAWSCloudwatch/neusoft_ec2_cpu_utilization_53ea38.csv", 3.9));
	}
}
