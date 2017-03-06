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
import com.basic.WriteCSV;
import com.opencsv.CSVWriter;

public class Information {

	public Entropy entropy;
	public Double[][] infoMatrix;
	
	public Information() throws Exception {
		getInfoMatrix("read.csv");
	}
	
	public Information(String original_csv) throws Exception {
		getInfoMatrix(original_csv);
	}
	
	private double pairInfo(int x, int y) {
		return entropy.entropyList[x] - entropy.entropyConditionalMatrix[x][y];
	}
	
	private void getInfoMatrix(String original_csv) throws Exception {
		
		System.out.println("========================\ninfo相关性：\n");
		
		File outfile = new File("info_out_dir");
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
		
		/*** 获得Info矩阵 ***/
		entropy = new Entropy(original_csv);
		infoMatrix = new Double[entropy.prob.separate.numAttr][entropy.prob.separate.numAttr];
		
		for (int i = 0; i < entropy.prob.separate.numAttr; i++) {
			for (int j = 0; j < entropy.prob.separate.numAttr; j++) {
				infoMatrix[i][j] = pairInfo(i, j);
			}
		}
		
	}
	
	public void writeCSV() throws Exception {
		WriteCSV writeCSV = new WriteCSV();
		writeCSV.writeMatrixCSV(infoMatrix, entropy.prob.separate.attrList, "info_out_dir", "info.csv");
	}
	
	public static void main(String[] args) throws Exception {
		Information info = new Information();
		/* Default:
		 * Information info = new Information("read.csv");
		 */
		
		info.writeCSV();
		
		System.out.println("\nFinish the test: if create the infoMatrix.csv");
		
	}
	
}
