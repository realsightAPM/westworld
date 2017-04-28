package com.realsight.westworld.bnanalysis.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;

public class ReadCSV {

	public ReadCSV() {}
	
	public Map<String, String[]> readRangeList(String range_list) throws IOException {
		
		/*** 读取netica中的Range ***/
		
		Map<String, String[]> rangeMap = new HashMap<String, String[]> ();
		
		/***读取数据***/
		File file_read = new File(range_list);
        FileReader fReader = new FileReader(file_read);
        CSVReader csvReader = new CSVReader(fReader);
        
        List<String[]> list_read = csvReader.readAll();
        csvReader.close();
        
        for (int i = 0; i < list_read.size(); i++) {
        	String[] stmp = new String[list_read.get(i).length-1];
        	for (int j = 0; j < list_read.get(i).length-1; j++) {
        		stmp[j] = list_read.get(i)[j+1];
        	}
        	rangeMap.put(list_read.get(i)[0], stmp);
        }
        
        return rangeMap;
	}
}
