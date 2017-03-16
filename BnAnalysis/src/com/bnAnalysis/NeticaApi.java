package com.bnAnalysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.basic.ReadCSV;
import com.basic.Separate;
import com.basic.WriteCSV;

import norsys.netica.*;
import norsys.neticaEx.aliases.Node;

public class NeticaApi {

	public BanjoApi banjo;
	public Environ env;
	public Net net;
	public Map<String, String[]> rangeMap;
	
	public NeticaApi(String original_csv_or_dneFile, int num_thread) throws Exception {
		buildNet(original_csv_or_dneFile, num_thread);
	}
	
	public NeticaApi() throws NeticaException, IOException {
		loadNet();
	}
	
	private void buildNet(String original_csv, int num_thread) throws Exception {
		
		banjo = new BanjoApi(original_csv, num_thread);
		
		System.out.println("========================\nnetica的.dne：\n");
		
		File outfile = new File("netica_out_dir");
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
		
		Node.setConstructorClass("norsys.neticaEx.aliases.Node");
		env = new Environ(null);
		
		net = new Net();
		net.setName("testNetica");
		
		Separate separate = banjo.separate;
		Node[] nodeList = new Node[separate.numAttr];
		String[] attrList = separate.attrList;
		List<String[]> mapList = separate.mapList;
		int numAttr = separate.numAttr, numInst = separate.numInst;
		
		/*** build nodes ***/
		System.out.println("node的个数：" + nodeList.length);
		for (int i = 0; i < separate.numAttr; i++) {
//			System.out.println(getStates(mapList.get(i).length));
			nodeList[i] = new Node(attrList[i], getStates(mapList.get(i).length), net);
		}
		
		/*** build links ***/
		for (int i = 0 ; i < separate.numAttr; i++) {
			for (int j = 0; j < banjo.parentMap.get(i).size(); j++) {
				nodeList[i].addLink(nodeList[banjo.parentMap.get(i).get(j).intValue()]);
			}
		}
		
		/*** counting case ***/
		NodeList nodes = net.getNodes();
		Streamer caseFile = new Streamer("separate_out_dir/separated.cas");
		net.reviseCPTsByCaseFile(caseFile, nodes, 1.0);
		
		net.write(new Streamer("netica_out_dir/Learned_netica.dne"));

		WriteCSV writeCSV = new WriteCSV();
		writeCSV.writeMapList(mapList, attrList,"netica_out_dir", "range_list.csv");
		ReadCSV readCSV = new ReadCSV();
		rangeMap = readCSV.readRangeList("netica_out_dir/range_list.csv");
	}
	
	private void loadNet() throws NeticaException, IOException {
		loadNet("netica_out_dir/Learned_netica.dne");
	}
	
	private void loadNet(String dne_file) throws NeticaException, IOException {
		env = new Environ(null);
		net = new Net (new Streamer (dne_file));
		ReadCSV readCSV = new ReadCSV();
		rangeMap = readCSV.readRangeList("netica_out_dir/range_list.csv");
	}
	
	private String getStates(int numstate) {
		String str = "";
		for (int i = 0; i < numstate; i++) {
			str += (char) ('a'+i);
			if (i < numstate-1)
				str += ", ";
		}
		return str;
	}
	
	public Map<String, String[]> getRangeMap() {
		return rangeMap;
	}
	
	public void printRangeMap() {
		for (String it : rangeMap.keySet()) {
			System.out.print(it + ":\t");
			for (String it2 : rangeMap.get(it)) {
				System.out.print(it2 + " ");
			}
			System.out.println();
		}
	}
	
	public List<String> getChildren(Node node) throws NeticaException {
		return getChildren(node.toString());
	}
	
	public List<String> getChildren(String node_name) throws NeticaException {
		List<String> children = new ArrayList<String>();
		boolean exist = false;
		for (String it : rangeMap.keySet()) {
			if (it.equals(node_name)) {
				exist = true;
			}
		}
		if (!exist) {
			System.out.println(node_name + " is not in the attrList");
		}
		NodeList nodeList = net.getNode(node_name).getChildren();
		for (int i = 0; i < nodeList.size(); i++) {
			children.add(nodeList.get(i).toString());
		}
		return children;
	}
	
	public void printChildren(Node node) throws NeticaException {
		printChildren(node.toString());
	}
	
	public void printChildren(String node_name) throws NeticaException {
		List<String> children = getChildren(node_name);
		System.out.print("\nChildren of " + node_name + " are: ");
		for (String it : children) {
			System.out.print(it + " ,");
		}
	}
	
	@Override
	public void finalize() throws NeticaException {
		net.finalize();   // not strictly necessary, but a good habit
		env.finalize();
	}
	
	public static void main(String[] args) throws Exception {
		
		NeticaApi netica = new NeticaApi();
		
		System.out.println("输出netica学习的网络结果");
	}
	
}
