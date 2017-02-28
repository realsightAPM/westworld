package Initialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Normalize {
	public List<ArrayList<Double>> normalizedData;
	
	public Normalize() {
		normalizedData = null;
	}
	
	public Normalize(List<ArrayList<Double>> original_data) {
		normalizedData = getNormalData(original_data);
	}

	public List<ArrayList<Double>> getNormalData(List<ArrayList<Double>> in_data) {
		
		List<ArrayList<Double>> normal_data = null;
		int numInst = in_data.size();
		if (numInst == 0) {
			System.out.println("There is no data in in_data!!!");
			return normal_data;
		}
		int numAttr = in_data.get(0).size();
		
		for (int i = 0; i < numAttr; i++) {
        	Double lo = new Double(Collections.min(in_data.get(i)));
        	Double hi = new Double(Collections.max(in_data.get(i)));
        	Double gap = new Double(hi-lo);
        	
        	System.out.println(lo+" "+" "+hi+" "+gap);
        	
        	for (int j = 0; j < numInst; j++) {
        		in_data.get(i).set(j, (in_data.get(i).get(j)-lo)/gap);
        	}
        }
        
//        for (int i = 0; i < n; i++) {
//        	for (int j = 0; j < m; j++) {
//        		System.out.print(in_data.get(i).get(j));
//        		System.out.print(" ");
//        	}
//        	System.out.println();
//        	System.out.println(Collections.min(in_data.get(i))+"\t"+Collections.max(in_data.get(i)));
//        }
		
		return normal_data;
	}
}
