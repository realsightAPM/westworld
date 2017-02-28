package Initialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class ReadFile {
	
	public List<ArrayList<Double>> originalData;
	
	public ReadFile() throws Exception {
		originalData = getData("read.csv");
	}
	
	public ReadFile(String original_csv) throws Exception {
		originalData = getData(original_csv);
	}
	
	List<ArrayList<Double>> getData() throws Exception {
		return getData("read.csv");
	}

	List<ArrayList<Double>> getData(String original_csv) throws Exception {
		/***¶ÁÈ¡Êý¾Ý***/
		File file_read = new File(original_csv);
        FileReader fReader = new FileReader(file_read);
        CSVReader csvReader = new CSVReader(fReader);
        String[] strs_read = csvReader.readNext();
        
        List<String[]> list_read = csvReader.readAll();
        csvReader.close();
        List<ArrayList<Double>> data = null;
        int numInst = list_read.size();
        if (numInst == 0) {
        	System.out.println("There is no data in file!!!");
        	return data;
        }
        int numAttr = list_read.get(0).length;
        
        data = new ArrayList<ArrayList<Double>>(numAttr);
        for (int i = 0; i < numAttr; i++) {
        	data.add(new ArrayList<Double>(numInst));
        }
        
        for (int i = 0; i < numInst; i++) {
        	for (int j = 0; j < numAttr; j++) {
        		data.get(j).add(Double.valueOf(list_read.get(i)[j]));
        	}
        }
		return data;
	}
}
