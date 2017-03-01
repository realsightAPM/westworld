package com.bnAnalysis;

import com.algorithm.Pearson;

public class Relation {

	public void getPearson() throws Exception {
		Pearson pearson = new Pearson();
	}
	
	public void getEntropy() {
		
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("执行pearson相关性分析：");
		
		Pearson pearson = new Pearson();
		
		pearson.writeMatrixCSV();
		pearson.normal.writeCSV();
	}
}
