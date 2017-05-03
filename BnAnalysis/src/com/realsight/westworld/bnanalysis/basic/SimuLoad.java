package com.realsight.westworld.bnanalysis.basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.realsight.westworld.bnanalysis.io.ReadFile;
import com.realsight.westworld.bnanalysis.service.NeticaApi;

import norsys.netica.NeticaException;
import norsys.netica.Node;

public class SimuLoad {
	
	// 这个类的主要目的是用来写一个模拟增压后的分布的.tsv文件
	
	public ReadFile readFile;
	public NeticaApi netica;
	public Map<String, ArrayList<Double>> hashMap;
	
	public SimuLoad() {}
	
	public SimuLoad(String original_csv, String simu_var, double times) throws Exception {
		runSimu(original_csv, simu_var, times);
	}
	
	private void runSimu(String original_csv, String simu_var, double times) throws Exception {
		
//		System.out.println("========================\n离散化：\n" + original_csv);
		
		
		File outfile = new File("simuLoad_out_dir");
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
		
		hashMap = new HashMap<String, ArrayList<Double>>();
		readFile = new ReadFile(original_csv);
		netica = new NeticaApi();
		netica.loadRangeMap();
		
		for (String it : netica.rangeMap.keySet()) {
			System.out.print(it+" ");
		}
		System.out.println();
		
		System.out.println(simu_var);
		
		if (!netica.rangeMap.containsKey(simu_var)) {
			System.out.println(simu_var + "is not a variable");
			return;
		}
		
		for (String it : readFile.attrList) {
			Set<Double> treeSet = new TreeSet<Double>();
			hashMap.put(it, new ArrayList<Double>());
			
			String[] str = netica.rangeMap.get(it);
			for (int i = 0; i < str.length; i++) {
				String[] tmp = str[i].replace('(', ' ').replace(')', ' ').replace('[', ' ').replace(']', ' ').trim().split("-");
				for (int j = 0; j < tmp.length; j++) {
					if (!tmp[j].equals("inf") && !tmp[j].equals("")) {
						treeSet.add(Double.valueOf(tmp[j]));
					}
				}
			}
			for (Double it1 : treeSet) {
				hashMap.get(it).add(it1);
				System.out.print(it1 + " ");
			}
			System.out.println();
		}
		
		writeCas(simu_var, times);
//		netica.loadSimuNet();
	}

	public List<Double> getNewDistribution(String strVar) throws NeticaException {
		Node node = netica.net.getNode(strVar);
		int numState = node.getNumStates();
		
		List<Double> resList = new ArrayList<Double>();
		
		for (int i = 0; i < numState; i++) {
			double tmp = node.getBelief(""+((char)('a'+i)));
			resList.add(tmp);
		}
		
		return resList;
	}
	
	private int getSimuState(double value, String var) {
		int pos = 0;
		List<Double> list = hashMap.get(var);
		for (; pos < list.size(); pos++) {
			if (value < list.get(pos)) {
				return pos;
			}
		}
		return pos;
	}
	
	private void writeCas(String target_var, double times) throws IOException {
//		System.out.println("========================\n输出模拟压力测试的离散化CAS文件：\n");
		
		/*** output separated cas file ***/
		File write_simuFile = new File("simuLoad_out_dir/simuLoad.cas");
		write_simuFile.createNewFile(); // create file
		BufferedWriter out_simuFile = new BufferedWriter(new FileWriter(write_simuFile));
		String[] attrList = readFile.attrList;
		int numAttr = attrList.length;
		int numInst = readFile.originalData.get(0).size();
		
		for (int i = 0; i < numAttr; i++) {
			out_simuFile.write(attrList[i]);
			if (i < numAttr-1)
				out_simuFile.write("\t");
		}
		
		out_simuFile.write("\r\n");
		
		System.out.println("显示模拟前后数据: "+times);
		
		for (int i = 0; i < numInst; i++) {
			for (int j = 0; j < numAttr; j++) {
				double tmp = readFile.originalData.get(j).get(i);
				if (attrList[j].equals(target_var)) {
					out_simuFile.write((char) ('a'+getSimuState(tmp*times, attrList[j])));
//					System.out.println(tmp + "\t" + tmp*times);
//					System.out.println(((char) ('a'+getSimuState(tmp, attrList[j]))) + "\t" + 
//							((char) ('a'+getSimuState(tmp*times, attrList[j]))));
				}
				else {
					out_simuFile.write((char) ('a'+getSimuState(tmp, attrList[j])));
				}
				if (j < numAttr-1)
					out_simuFile.write("\t");
			}
			out_simuFile.write("\r\n");
			out_simuFile.flush(); // push data in cache into file
		}
		out_simuFile.close(); // close the file
	}
	
	@Override
	public void finalize() throws NeticaException {
		netica.finalize();
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SimuLoad simu = new SimuLoad("inputjava_data1.csv", "session_count", 1);
		
		List<Double> list = simu.getNewDistribution("session_count");
		
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i) + " ");
		}
		System.out.println();
		
		simu.finalize();
		
	}

}
