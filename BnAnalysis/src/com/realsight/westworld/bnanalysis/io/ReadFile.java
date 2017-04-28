package com.realsight.westworld.bnanalysis.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class ReadFile {
	
	public List<ArrayList<Double>> originalData;
	public String[] attrList;
	
	public ReadFile() throws Exception {
		getData("read.csv");
	}
	
	public ReadFile(String original_csv) throws Exception {
		getData(original_csv);
	}
	
	private void getData() throws Exception {
		getData("read.csv");
	}

	private void getData(String original_csv) throws Exception {
		
		System.out.println("========================\n读取原始csv数据：\n" + original_csv);
		
		/***读取数据***/
		File file_read = new File(original_csv);
        FileReader fReader = new FileReader(file_read);
        CSVReader csvReader = new CSVReader(fReader);
        attrList = csvReader.readNext();
        
        List<String[]> list_read = csvReader.readAll();
        csvReader.close();
        int numInst = list_read.size();
        if (numInst == 0) {
        	System.out.println("There is no data in file!!!");
        	return;
        }
        int numAttr = list_read.get(0).length;
        
        originalData = new ArrayList<ArrayList<Double>>(numAttr);
        for (int i = 0; i < numAttr; i++) {
        	originalData.add(new ArrayList<Double>(numInst));
        }
        
        for (int i = 0; i < numInst; i++) {
        	for (int j = 0; j < numAttr; j++) {
        		originalData.get(j).add(Double.valueOf(list_read.get(i)[j]));
        	}
        }
        
	}
	
	public static void main(String[] args) throws Exception {
		ReadFile read = new ReadFile();
		
		System.out.println("读取测试： ");
		
		for (int i = 0 ; i < read.attrList.length; i++)
			System.out.println(read.attrList[i]);
		
//		for (int i = 0; i < read.originalData.get(0).size(); i++) {
//			for (int j = 0; j < read.originalData.size(); j++) {
//				System.out.print(read.originalData.get(j).get(i)+" ");
//			}
//			System.out.println();
//		}
		
	}
	
}
