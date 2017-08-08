package com.realsight.westworld.bnanalysis.basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.realsight.westworld.bnanalysis.io.ReadFile;

public class SeparateFixed {

	public List<ArrayList<Integer>> sepData;        // a list indicates an attribute;
	public String[] attrList;
	public List<String[]> mapList;
	public int numAttr, numInst;
	
	public SeparateFixed(String original_csv) throws Exception {
		ReadFile file = new ReadFile(original_csv);
		attrList = file.attrList;
		
		sepData = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < file.originalData.size(); i++) {
			sepData.add(new ArrayList<Integer>());
		}
		
		numAttr = file.originalData.size();
		if (numAttr == 0) numInst = 0;
		else numInst = file.originalData.get(0).size();
		
		for (int i = 0; i < numAttr; i++) {
			for (int j = 0; j < numInst; j++) {
				int tmp_x = (int) (file.originalData.get(i).get(j)/0.2);
				if (tmp_x >= 5) tmp_x = 4; 
				sepData.get(i).add(tmp_x);
			}
		}
		
		writeCAS("separated.cas");
	}
	
	public void writeCAS(String write_cas) throws IOException {
		System.out.println("========================\n输出离散化CAS文件：\n");
		
		/*** output separated cas file ***/
		File write_separatedFile = new File("separate_out_dir/" + write_cas);
		write_separatedFile.createNewFile(); // create file
		BufferedWriter out_separatedFile = new BufferedWriter(new FileWriter(write_separatedFile));
		for (int i = 0; i < numAttr; i++) {
			out_separatedFile.write(attrList[i]);
			if (i < numAttr-1)
				out_separatedFile.write("\t");
		}
		
		out_separatedFile.write("\n");
		
		for (int i = 0; i < numInst; i++) {
			for (int j = 0; j < numAttr; j++) {
				out_separatedFile.write('a'+sepData.get(j).get(i).intValue());
				if (j < numAttr-1)
					out_separatedFile.write("\t");
			}
			out_separatedFile.write("\n");
			out_separatedFile.flush(); // push data in cache into file
		}
		out_separatedFile.close(); // close the file
	}
	
	public static void main(String[] args) throws Exception {
		SeparateFixed sepa = new SeparateFixed("data.csv");
	}
}
