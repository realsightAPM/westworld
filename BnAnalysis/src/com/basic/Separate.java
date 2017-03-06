package com.basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class Separate {
	
	private Instances data, newData;
	public List<ArrayList<Integer>> sepData;        // a list indicates an attribute;
	public String[] attrList;
	public List<String[]> mapList;
	public int numAttr, numInst;
	
	public Separate() throws Exception {   // read the default file "read.csv";
		initailize("read.csv");
	}
	
	public Separate(String original_csv) throws Exception {    // read the file original_csv;
		initailize(original_csv);
	}
	
	private void initailize(String original_csv) throws Exception {
		
		System.out.println("========================\n离散化：\n" + original_csv);
		
		File outfile = new File("separate_out_dir");
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
		
		/*** load csv ***/
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(original_csv));
		data = loader.getDataSet();
		numAttr = data.numAttributes();
		numInst = data.numInstances();

		sepData = new ArrayList<ArrayList<Integer>>(numAttr);
		for (int i = 0; i < numAttr; i++) {
			sepData.add(new ArrayList<Integer>());
		}
		mapList = new ArrayList<String[]>(numAttr);
		attrList = new String[numAttr];
		
		for (int i = 0; i < numAttr; i++) {
			attrList[i] = data.attribute(i).name();
		}
		
		/*** set options ***/
		String[] opts = new String[5];
		// choose the number of intervals
		opts[0] = "-B";
		opts[1] = "6";
		// choose the range of attributes on which to apply hte filter:
		opts[2] = "-R";
		opts[3] = "1-"+numAttr;
		opts[4] = "-F";
		
		/*** apply discretization ***/
		Discretize disc = new Discretize();
		disc.setOptions(opts);
		disc.setInputFormat(data);
		newData = Filter.useFilter(data, disc);
		
		/*** get the separated matrix ***/
		for (int i = 0; i < numInst; i++) {
			Instance inst = newData.instance(i);
			for (int j = 0; j < numAttr; j++) {
				sepData.get(j).add(new Integer((int) inst.value(j)));
			}
		}
		
		getInterval();
	}
	
	private void getInterval() {
		
//		System.out.println("获得离散区间");
		
//		for (int i = 0; i < numAttr; i++) {
//			System.out.println(newData.attribute(i).toString());
//		}
		
		for (int i = 0; i < numAttr; i++) {
			String[] str = newData.attribute(i).toString().split(" ");
			String[] range = str[2].replace("{", "").replace("'", "").replace("\\", "").replace("}", "").split(",");
			mapList.add(range);
		}
	}
	
	public void writeInterval() throws IOException {
		File write_map = new File("separate_out_dir/" + "map.txt");
		write_map.createNewFile(); // create file
		BufferedWriter out_map = new BufferedWriter(new FileWriter(write_map));
		
		for (int i = 0; i < numAttr; i++) {
			out_map.write(data.attribute(i).name()+": \n");
			for (int j = 0; j < mapList.get(i).length; j++) {
				out_map.write(j);
				out_map.write(": "+ mapList.get(i)[j] +"\t");
			}
			out_map.write("\n");
		}
		out_map.flush(); // push data in cache into file
		out_map.close(); // close the file
	}
	
	public void writeCSV() throws Exception {
		writeCSV("separated_csv.csv");
	}
	
	public void writeCSV(String write_csv) throws Exception {
		
		System.out.println("========================\n输出离散化CSV文件：\n");
		
		/*** output separated csv file ***/
		File write_separatedFile = new File("separate_out_dir/" + write_csv);
		write_separatedFile.createNewFile(); // create file
		BufferedWriter out_separatedFile = new BufferedWriter(new FileWriter(write_separatedFile));
		for (int i = 0; i < numInst; i++) {
			for (int j = 0; j < numAttr; j++) {
				out_separatedFile.write(sepData.get(j).get(i).toString());
				if (j < numAttr-1)
					out_separatedFile.write(",");
			}
			out_separatedFile.write("\n");
			out_separatedFile.flush(); // push data in cache into file
		}
		out_separatedFile.close(); // close the file
	}
	
	public void writeTSV() throws Exception {
		writeTSV("separated_tsv.txt");
	}
	
	public void writeTSV(String write_tsv) throws Exception {
		
		System.out.println("========================\n输出离散化TSV文件：\n");
		
		/*** output separated csv file ***/
		File write_separatedFile = new File("separate_out_dir/" + write_tsv);
		write_separatedFile.createNewFile(); // create file
		BufferedWriter out_separatedFile = new BufferedWriter(new FileWriter(write_separatedFile));
		for (int i = 0; i < numInst; i++) {
			for (int j = 0; j < numAttr; j++) {
				out_separatedFile.write(sepData.get(j).get(i).toString());
				if (j < numAttr-1)
					out_separatedFile.write("\t");
			}
			out_separatedFile.write("\r\n");
			out_separatedFile.flush(); // push data in cache into file
		}
		out_separatedFile.close(); // close the file
	}
	
	public void writeCAS() throws IOException {
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
		
		out_separatedFile.write("\r\n");
		
		for (int i = 0; i < numInst; i++) {
			for (int j = 0; j < numAttr; j++) {
				out_separatedFile.write('a'+sepData.get(j).get(i).intValue());
				if (j < numAttr-1)
					out_separatedFile.write("\t");
			}
			out_separatedFile.write("\r\n");
			out_separatedFile.flush(); // push data in cache into file
		}
		out_separatedFile.close(); // close the file
	}
}
