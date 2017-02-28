package Algorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import Initialization.Normalize;
import Initialization.Pair;
import Initialization.ReadFile;

public class Information {

	public Entropy eData;
	public int sampleSize, varSize;
	
	public Information() throws Exception {
		eData = new Entropy("read.csv");
		sampleSize = eData.prob.numInst;
		varSize = eData.prob.numAttr;
	}
	
	public Information(String original_csv) throws Exception {
		eData = new Entropy(original_csv);
		sampleSize = eData.prob.numInst;
		varSize = eData.prob.numAttr;
	}
	
	public double pairInfo(Pair attr1, Pair attr2) {
		return eData.getH(attr1) - eData.getConditionalH(attr1, attr2);
	}
	
	public void getMatrix() throws Exception {
		/***导出数据***/    
		System.out.println("下面是文件输出的程序：");
		
		File file_write = new File("write.csv");
        Writer writer = new FileWriter(file_write);  
        CSVWriter csvWriter = new CSVWriter(writer, ',');
		for (int i = 0; i < varSize; i++) {
			String[] stmp = new String[varSize];
			for (int j = 0; j < varSize; j++) {
				stmp[j] = Double.toString(pairInfo(new Pair(i, 6), new Pair(j, 6))); //默认6个属性
			}
			csvWriter.writeNext(stmp);
		}
		csvWriter.close();
	}
}
