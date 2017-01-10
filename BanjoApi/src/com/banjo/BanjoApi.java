package com.banjo;

/*** BinWu 2017/1/8 ***/


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public Map<Integer, String> nodeMap;
	public Map<Integer, ArrayList<Integer>> parentMap;
	int[][] statistic;

	public void train( String csvinput, String out_model_dir, int num_thread ) throws Exception {
		
		File outfile = new File("wekaOut");
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
		loader.setSource(new File(csvinput));
		System.out.println("\n输入"+csvinput);
		Instances data = loader.getDataSet();

		numAttr = data.numAttributes();
		numInst = data.numInstances();
		nodeMap = new HashMap<Integer, String>();
		
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
		Instances newData = Filter.useFilter(data, disc);
//		System.out.println(newData.attribute(0).value(4));

		/*** set the variables of this class ***/
		
		for (int i = 0; i < numAttr; i++) {
			nodeMap.put(i, newData.attribute(i).name());
		}
		
		/*********************** the properties of every attribute *********************/
		
		for (int i = 0; i < numAttr; i++) {
			System.out.println("\n");
			System.out.println("Attribute " +(i)+": "+newData.attribute(i).name());
			System.out.println(newData.attributeStats(i));
		}
		
		char[] ch = new char[26];
		for (int i = 0; i < 26; i++) {
			ch[i] = (char)('a'+ i);
		}
		
		/******************************* the map of ranges ****************************/
		File writename3 = new File(out_model_dir+"/map.txt");
		writename3.createNewFile(); // create file
		BufferedWriter out3 = new BufferedWriter(new FileWriter(writename3));
		
		for (int i = 0; i < numAttr; i++) {
			String[] str = newData.attribute(i).toString().split(" ");
			String[] range = str[2].replace("{", "").replace("'", "").replace("\\", "").replace("}", "").split(",");
			out3.write(newData.attribute(i).name()+": \n");
			for (int j = 0; j < range.length; j++) {
				out3.write(ch[j]);
				out3.write(": "+range[j]+"\t");
			}
			out3.write("\n");
		}
		out3.flush(); // push data in cache into file
		out3.close(); // close the file
		
		
		/****************************** output the .tsv for banjo ************/
		File writename2 = new File("wekaOut/input.txt");
		writename2.createNewFile(); // create file
		BufferedWriter out2 = new BufferedWriter(new FileWriter(writename2));
		for (int i = 0; i < numInst; i++) {
			Instance inst = newData.instance(i);
			for (int j = 0; j < numAttr; j++) {
				out2.write(inst.value(j) + "\t");
			}
			out2.write("\n");
			out2.flush(); // push data in cache into file
		}
		out2.close(); // close the file
		
		
		/****************************** save CSV ***************************/
		CSVSaver saver = new CSVSaver();
		saver.setInstances(newData);
		saver.setFile(new File("wekaOut/outfile.csv"));
		saver.writeBatch();
		
		/****************************** output the .tsv for netica ************/
		
		File writename = new File(out_model_dir+"/tsv.txt");
		writename.createNewFile(); // create file
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		for (int i = 0; i < numInst; i++) {
			Instance inst = newData.instance(i);
			for (int j = 0; j < numAttr; j++) {
				int x = (int)(inst.value(j));
				out.write(ch[x] + "\t");
			}
			out.write("\n");
			out.flush(); // push data in cache into file
		}
		out.close(); // close the file

		
		for (Object it : nodeMap.keySet()) {
			System.out.println(it+": "+nodeMap.get(it));
		}
		
		/*** run Banjo.jar ***/
		Banjo banjo = new Banjo();
		String[] args = new String[1];
		args[0] = "threads="+num_thread;
		banjo.execute(args, "wekaOut/input.txt");
		
		getStructure();
		
		getDne(out_model_dir);
	}
	
	public void getStructure() throws IOException { // This function cannot be runned along
		String pathname = "output/topGraph.txt"; // absolute dir or relative dit  
        File filename = new File(pathname);  
        InputStreamReader reader = new InputStreamReader(  
                new FileInputStream(filename)); // create an input stream object: reader  
        BufferedReader br = new BufferedReader(reader); // build an object which translates the data to that computer can read
   
        parentMap = new HashMap<Integer, ArrayList<Integer>>();
        for (int i = 0; i < numAttr; i++) {
        	parentMap.put(i, new ArrayList<Integer> ());
        }
        
        String line = "";  
        while ((line = br.readLine()) != null) {
            line = line.trim();
            int pos = line.indexOf("->");
            if (pos > -1) {
            	int sta = Integer.parseInt(line.substring(0, pos));
            	int end = Integer.parseInt(line.substring(pos+2, line.length()-1));
            	System.out.println(sta+"->"+end);
            	parentMap.get(end).add(sta);
            }
        }
        System.out.println(parentMap.size());
        for (Object it : parentMap.keySet()) {
        	System.out.println(it+": "+parentMap.get(it).toString());
        }
        br.close();
	}
	
	public void getDne(String out_model_dir) throws IOException { // This function cannot be runned along
		long stap = System.currentTimeMillis()/1000;
		File writename4 = new File(out_model_dir+"/neticaFile.dne");
		writename4.createNewFile(); // create file
		BufferedWriter out4 = new BufferedWriter(new FileWriter(writename4));
		
		/*** begin ***/
		out4.write("bnet APM {" + "\n" +
                   "autoupdate = FALSE;" + "\n" +
                   "whenchanged = "+stap+");\n\n"
                   );
		
		for (int i = 0; i < numAttr; i++) {
			out4.write("node "+nodeMap.get(i)+"{\n");
			out4.write("kind = NATURE;\n");
			out4.write("discrete = TRUE;\n");
			out4.write("states = (a, b, c, d, e);\n");
			out4.write("parents = (");
			for (Integer it : parentMap.get(i)) {
				out4.write(nodeMap.get(it)+", ");
			}
			out4.write(");\n");
			out4.write("whenchanged = "+stap+";\n\n");
		}
		
		
		
		/*** end ***/
		out4.write("\n\n};");
		out4.flush(); // push data in cache into file
		out4.close(); // close the file
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		BanjoApi banjoApi = new BanjoApi();
		banjoApi.train("inputjava_7.csv", "out_model_dir", 10);
		
	}

}
