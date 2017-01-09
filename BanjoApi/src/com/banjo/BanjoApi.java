package com.banjo;

import java.io.BufferedWriter;
/*** BinWu 2017/1/8 ***/
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.duke.cs.banjo.application.Banjo;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

public class BanjoApi {
	/*** these variables are used to record the state of the whole data set ***/
	public int numAttr;
	public int numInst;

	public void train(String csvinput) throws Exception {
		// load csv
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(csvinput));
		System.out.println(csvinput);
		Instances data = loader.getDataSet();

		// set options
		String[] opts = new String[5];
		// choose the number of intervals
		opts[0] = "-B";
		opts[1] = "5";
		// choose the range of attributes on which to apply hte filter:
		opts[2] = "-R";
		opts[3] = "1-5";
		opts[4] = "-F";
		// apply discretization
		Discretize disc = new Discretize();
		disc.setOptions(opts);
		disc.setInputFormat(data);
		Instances newData = Filter.useFilter(data, disc);
//		System.out.println(newData.attribute(0).value(4));

		numAttr = newData.numAttributes();
		numInst = newData.numInstances();

//		Map<String, Integer> set = new HashMap<String, Integer>();
//		List<HashMap<String, Integer>> list = new ArrayList<HashMap<String, Integer>>();
//
//		for (int i = 0; i < numAttr; i++) {
//			list.add(new HashMap<String, Integer>());
//			for (int j = 0; j < newData.attribute(i).numValues(); j++) {
//				list.get(i).put(newData.attribute(i).value(j), j);
//			}
//		}

		// System.out.println(newData.numInstances());
		// System.out.println(newData.instance(6).toString(1));

//		for (int i = 0; i < list.size(); i++) {
//			for (Object it : list.get(i).keySet()) {
//				System.out.println(it + ": " + list.get(i).get(it));
//			}
//			System.out.println("\n");
//		}
		
		for (int i = 0; i < numAttr; i++) {
			System.out.println("\n");
			System.out.println("Attribute " +(i)+": "+newData.attribute(i).name());
			System.out.println(newData.attributeStats(i));
		}
		
		File writename = new File("input.txt");
		writename.createNewFile(); // create file
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		for (int i = 0; i < numInst; i++) {
			Instance inst = newData.instance(i);
//			out.write(i+": ");
			for (int j = 0; j < numAttr; j++) {
				out.write(inst.value(j) + "\t");
			}
			out.write("\n");
			out.flush(); // push data in cache into file
		}
//		out.write("I can write it into the file\n");
//		out.flush(); // push data in cache into file
		out.close(); // close the file

		// save CSV
		CSVSaver saver = new CSVSaver();
		saver.setInstances(newData);
		saver.setFile(new File("outfile.csv"));
		saver.writeBatch();

		Banjo banjo = new Banjo();
		banjo.execute(new String[0], "input.txt");
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		BanjoApi banjoApi = new BanjoApi();
		banjoApi.train("inputjava.csv");
		
	}

}
