package Algorithm;

import java.util.ArrayList;
import java.util.List;

import Initialization.Pair;
import Initialization.Separate;

public class Probability {

	public List<ArrayList<Integer>> pData;           // a list indicates an attribute;
	int numAttr, numInst;
	
	public Probability() throws Exception {          // read the default file "read.csv";
		Separate sep = new Separate("read.csv");
		pData = sep.sepData;
		numAttr = sep.numAttr;
		numInst = sep.numInst;
	}
	
	public Probability(String original_csv) throws Exception {  // read the file original_csv;
		Separate sep = new Separate(original_csv);
		pData = sep.sepData;
		numAttr = sep.numAttr;
		numInst = sep.numInst;
	}
	
	public double getProbability(Pair pair) {
		int sum = 0;
		for (int i = 0; i < numInst; i++) {
			if (pData.get(pair.first).get(i) == pair.second)
				sum ++;
		}
		return sum*1.0/numInst;
	}
	
	public double getUnionProbability(List<Pair> unionList) {
		int sum = 0, unionLen = unionList.size();
		for (int i = 0; i < numInst; i++) {
			int tmpSum = 0;
			for (int j = 0; j < unionLen; j++) {
				if (pData.get(i).get(unionList.get(j).first) == unionList.get(j).second)
					tmpSum ++;
			}
			if (unionLen == tmpSum)
				sum ++;
		}
		return sum*1.0/numInst;
	}
	
	public double getConditionalProbability(List<Pair> unionList, List<Pair> conList) {
		int sum = 0, conSum = 0;
		int unionLen = unionList.size();
		int conLen = conList.size();
		for (int i = 0; i < numInst; i++) {
			int tmpSum = 0;
			for (int j = 0; j < conLen; j++) {
				if (pData.get(i).get(conList.get(j).first) == conList.get(j).second)
					tmpSum ++;
			}
			if (tmpSum == conLen) {
				conSum ++;
				int tmpSum2 = 0;
				for (int k = 0; k < unionLen; k++) {
					if (pData.get(i).get(unionList.get(k).first) == unionList.get(k).second)
						conSum ++;
				}
				if (tmpSum2 == unionLen)
					sum ++;
			}
		}
		return sum*1.0/conSum;
	}
}
