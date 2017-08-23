package com.realsight.westworld.bnanalysis.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WriteCAS {

	public void writeCAS(List<ArrayList<String>> dataDisc, List<String> attrList, String write_cas) throws IOException {
		System.out.println("========================\n输出离散化CAS文件：\n");
		
		int numAttr, numInst;
		
		if (dataDisc == null) return;
		
		numAttr =dataDisc.size();
		
		
		if (numAttr == 0 || dataDisc.get(0) == null) numInst = 0;
		else numInst = dataDisc.get(0).size();
		
		/*** output separated cas file ***/
		File file = new File(write_cas);
		file.createNewFile(); // create file
		BufferedWriter outer = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < numAttr; i++) {
			outer.write(attrList.get(i));
			if (i < numAttr-1)
				outer.write("\t");
		}
		
		outer.write("\n");
		
		for (int i = 0; i < numInst; i++) {
			for (int j = 0; j < numAttr; j++) {
				outer.write(dataDisc.get(j).get(i));
				if (j < numAttr-1)
					outer.write("\t");
			}
			outer.write("\n");
			outer.flush(); // push data in cache into file
		}
		outer.close(); // close the file
	}
}
