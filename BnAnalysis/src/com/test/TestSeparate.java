package com.test;

import java.util.List;

import Initialization.Separate;

public class TestSeparate {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Separate separate = new Separate();
		
		String[] attrList = separate.attrList;
		
		List<String[]> mapList = separate.mapList;
		
		for (int i = 0; i < mapList.size(); i++) {
			String[] str = mapList.get(i);
			System.out.println(attrList[i] + ": " + str.length);
			for (int j = 0; j < str.length; j++) {
				System.out.print(str[j]+"\t");
			}
			System.out.println();
		}
		
		separate.writeCAS();
		
	}

}
