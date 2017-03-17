package com.basic;

import static org.junit.Assert.*;

import org.junit.Test;

public class SeparateTest {

	@Test
	public void testSeparate() throws Exception {
		Separate sep =  new Separate("inputjava_data1.csv", 3);
		for (int i = 0; i < sep.mapList.size(); i++) {
			for (int j = 0; j < sep.mapList.get(i).length; j++)
			System.out.print(sep.mapList.get(i)[j] + "\t");
			System.out.println();
		}
		
	}

}
