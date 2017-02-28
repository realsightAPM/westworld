package Algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import Initialization.Normalize;
import Initialization.ReadFile;

public class Pearson {
	public List<ArrayList<Double>> data;      // a list indicates an attribute;
	public int sampleSize, varSize;
	
	public Pearson() throws Exception {       // read the default file "read.csv";
		data = (new Normalize((new ReadFile("read.csv")).originalData)).normalizedData;
	}
	
	public Pearson(String original_csv) throws Exception {     // read the file original_csv;
		data = (new Normalize((new ReadFile(original_csv)).originalData)).normalizedData;
	}
	
	public  double pairScore(int v1, int v2) {
		double sumX = 0.0;
		double sumY = 0.0;
		double sumX_Sq = 0.0;
		double sumY_Sq = 0.0;
		double sumXY = 0.0;
		int N = varSize;
		
		for (int i = 0; i < sampleSize; i++) {
			sumX += data.get(v1).get(i);
			sumY += data.get(v2).get(i);
			sumX_Sq += Math.pow(data.get(v1).get(i), 2);
			sumY_Sq += Math.pow(data.get(v2).get(i), 2);
			sumXY += data.get(v1).get(i) * data.get(v2).get(i);
		}
		
		double numerator = sumXY - sumX * sumY / N;
		double denominator = Math.sqrt((sumX_Sq - sumX * sumX / N) * (sumY_Sq - sumY * sumY / N));
		
		//分母不为0
		if (denominator < 0.00000001)
			return 0;
		else
			return numerator/denominator;
	}
	
	public void getMatrix() throws Exception {
		/***导出数据***/    
		System.out.println("下面是文件输出的程序：");
		
		File file_write = new File("write.csv");
        Writer writer = new FileWriter(file_write);  
        CSVWriter csvWriter = new CSVWriter(writer, ',');
		for (int i = 0; i < varSize; i++) {
			String[] stmp = new String[varSize];
			for (int j = 0; j < varSize; j++) {
				stmp[j] = Double.toString(pairScore(i, j));
			}
			csvWriter.writeNext(stmp);
		}
		csvWriter.close();
	}
	
}
