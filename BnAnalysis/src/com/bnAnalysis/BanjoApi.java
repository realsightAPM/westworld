package com.bnAnalysis;

/*** BinWu 2017/1/8 ***/


import java.io.*;
import java.util.*;
import com.basic.Separate;
import edu.duke.cs.banjo.application.Banjo;
import edu.duke.cs.banjo.utility.BANJO;

public class BanjoApi {
	
	/*** these variables are used to record the state of the whole data set ***/
	Separate separate;
	public List<ArrayList<Integer>> parentMap;
	
	public BanjoApi() throws Exception {
		train("read.csv", 2, 3);
	}
	
	public BanjoApi( String csvinput, int num_thread, int num_bins ) throws Exception {
		train(csvinput, num_thread, num_bins);
	}

	private void train( String csvinput, int num_thread, int num_bins ) throws Exception {
		
		/*** Separate Data ***/
		separate = new Separate(csvinput, num_bins);
		
		/*** run Banjo.jar ***/
		Banjo banjo = new Banjo();
		String[] args = new String[1];
		args[0] = "threads="+num_thread;
		banjo.execute(args, setProperties("separate_out_dir/separated_tsv.txt"));
		
		buildStructure();
	}
	
	private Properties setProperties(String input_file) {
		Properties properties = new Properties();
		String vars = new String();
		String[] attrList = separate.attrList;
		int attrLen = attrList.length;

		for (int i = 0; i < attrLen; i++) {
			vars += attrList[i];
			if (i < attrLen - 1)
				vars += " ";
		}
		properties.setProperty(BANJO.SETTING_OBSERVATIONSFILE.toLowerCase(), input_file);
		
        properties.setProperty(BANJO.SETTING_DATASET.toLowerCase(), 
        		String.valueOf(attrLen) + "-vars-" + String.valueOf(separate.numInst) + "-observations");
        properties.setProperty(BANJO.DATA_BANJOXMLTAG_VARCOUNT.toLowerCase(), String.valueOf(attrLen));
        properties.setProperty(BANJO.SETTING_VARIABLENAMES.toLowerCase(), vars);
		
		return properties;
	}
	
	private void buildStructure() throws IOException { // This function cannot be runned along
		
		String pathname = "banjo_out_dir/topGraph.txt"; // absolute dir or relative dit
        File filename = new File(pathname);  
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // create an input stream object: reader  
        BufferedReader br = new BufferedReader(reader); // build an object which translates the data to that computer can read
   
        parentMap = new ArrayList<ArrayList<Integer>>(separate.numAttr);
        for (int i = 0; i < separate.numAttr; i++) {
        	parentMap.add(new ArrayList<Integer>());
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
        for (int i = 0; i < separate.numAttr; i++) {
        	System.out.println(i + ": " + parentMap.get(i).toString());
        }
        br.close();
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		BanjoApi banjoApi = new BanjoApi();
		
	}

}
