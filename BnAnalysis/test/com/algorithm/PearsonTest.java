package com.algorithm;

import static org.junit.Assert.*;

import org.junit.Test;

import com.realsight.westworld.bnanalysis.algorithm.Pearson;

public class PearsonTest {

//	@Test
	public void testWriteCSV() throws Exception {
		Pearson pearson = new Pearson("inputjava_data1.csv");
		
		pearson.writeMatrixCSV();
		
//		pearson.getRelationRanking(1);
		
		System.out.println("\nFinish the test: check if the pearson relationship "
				+ "matrix has been writen in pearson_out_dir/pearson.csv");
	}

}
