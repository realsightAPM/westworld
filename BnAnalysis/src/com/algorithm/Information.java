package com.algorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.basic.Normalize;
import com.basic.Pair;
import com.basic.ReadFile;
import com.opencsv.CSVWriter;

public class Information {

	public Entropy entropy;
	public List<ArrayList<Double>> infoMatrix;
	
	public Information() throws Exception {
		getInfoMatrix("read.csv");
	}
	
	public Information(String original_csv) throws Exception {
		getInfoMatrix(original_csv);
	}
	
	private double pairInfo(int x, int y) {
		return entropy.entropyList.get(x) - entropy.entropyConditionalMatrix.get(x).get(y);
	}
	
	private void getInfoMatrix(String original_csv) throws Exception {
		
		System.out.println("========================\ninfo����ԣ�\n");
		
		File outfile = new File("info_out_dir");
		if(outfile.exists()) {
            System.out.println("Ŀ���ļ��Ѵ��ڣ�");
            String[] file_list = outfile.list();
            for (int i = 0; i < file_list.length; i++) {
            	File delfile = new File(outfile+"/"+file_list[i]);
            	delfile.delete();  
                System.out.println("��ɾ��" + file_list[i]);  
            }
        }
		else {
			outfile.mkdir();
			System.out.println("����Ŀ¼�ɹ���");
		}
		
		/*** ���Info���� ***/
		entropy = new Entropy(original_csv);
		infoMatrix = new ArrayList<ArrayList<Double>>(entropy.prob.separate.numAttr);
		
		for (int i = 0; i < entropy.prob.separate.numAttr; i++) {
			infoMatrix.add(new ArrayList<Double>(entropy.prob.separate.numAttr));
		}
		
		for (int i = 0; i < entropy.prob.separate.numAttr; i++) {
			for (int j = 0; j < entropy.prob.separate.numAttr; j++) {
				infoMatrix.get(i).add(pairInfo(i, j));
			}
		}
		
	}
	
	public void writeCSV() throws Exception {
		/***��������***/
		System.out.println("���info����Ծ���");
		
		int numAttr = entropy.prob.separate.numAttr;
		String[] attrList = entropy.prob.separate.attrList;
		String[] stmp = new String[numAttr+1];
		
		File file_write = new File("info_out_dir/info.csv");
        Writer writer = new FileWriter(file_write);  
        CSVWriter csvWriter = new CSVWriter(writer, ',');
        
        stmp[0] = "";
        for (int i = 1; i <= numAttr; i++) {
        	stmp[i] = attrList[i-1];
        }
        
		for (int i = 1; i <= numAttr; i++) {
			stmp[0] = attrList[i-1];
			for (int j = 1; j <= numAttr; j++) {
				stmp[j] = Double.toString(infoMatrix.get(i-1).get(j-1)); //Ĭ��6������
			}
			csvWriter.writeNext(stmp);
		}
		csvWriter.close();
	}
}