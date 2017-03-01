package com.basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Normalize {
	public List<ArrayList<Double>> normalizedData;
	public ReadFile readFile;
	
	public Normalize() throws Exception {
		readFile = new ReadFile("read.csv");
		getNormalData(readFile.originalData);
	}
	
	public Normalize(List<ArrayList<Double>> original_data) {
		readFile = null;
		getNormalData(original_data);
	}
	
	public Normalize(String original_csv) throws Exception {
		readFile = new ReadFile(original_csv);
		getNormalData(readFile.originalData);
	}

	private void getNormalData(List<ArrayList<Double>> in_data) {
		
		System.out.println("========================\n归一化：\n");
		
		File outfile = new File("normalized_out_dir");
		if(outfile.exists()) {
            System.out.println("目标文件已存在！");
            String[] file_list = outfile.list();
            for (int i = 0; i < file_list.length; i++) {
            	File delfile = new File(outfile+"/"+file_list[i]);
            	delfile.delete();  
                System.out.println("已删除" + file_list[i]);  
            }
        }
		else {
			outfile.mkdir();
			System.out.println("创建目录成功！");
		}
		
		int numAttr = in_data.size();
		if (numAttr == 0) {
			System.out.println("There is no data in in_data!!!");
			return;
		}
		int numInst = in_data.get(0).size();
		
		normalizedData = new ArrayList<ArrayList<Double>>(numAttr);
		
		for (int i = 0; i < numAttr; i++) {
			normalizedData.add(new ArrayList<Double>(numInst));
			
        	Double lo = new Double(Collections.min(in_data.get(i)));
        	Double hi = new Double(Collections.max(in_data.get(i)));
        	Double gap = new Double(hi-lo);
        	
//        	System.out.println(lo+" "+" "+hi+" "+gap);
        	
        	for (int j = 0; j < numInst; j++) {
        		normalizedData.get(i).add((in_data.get(i).get(j)-lo)/gap);
        	}
        }
	}
	
	public void writeCSV() throws IOException {
		writeCSV("normalized.csv");
	}
	
	public void writeCSV(String write_csv) throws IOException {
		
		/*** output normallized csv file ***/
		File write_normallizedFile = new File("normalized_out_dir/" + write_csv);
		write_normallizedFile.createNewFile(); // create file
		BufferedWriter out_normallizedFile = new BufferedWriter(new FileWriter(write_normallizedFile));
		
		int numAttr = normalizedData.size();
		if (numAttr == 0) {
			System.out.println("There is no data in in_data!!!");
			return;
		}
		int numInst = normalizedData.get(0).size();
		
		for (int i = 0; i < numInst; i++) {
			for (int j = 0; j < numAttr; j++) {
				out_normallizedFile.write(normalizedData.get(j).get(i).toString());
				if (j < numAttr-1)
					out_normallizedFile.write(",");
			}
			out_normallizedFile.write("\n");
		}
		out_normallizedFile.close();
	}
	
	public static void main(String[] args) throws Exception {
		Normalize norm = new Normalize();
		System.out.println("执行归一化");
		
		norm.writeCSV();
		
		
	}
	
}
