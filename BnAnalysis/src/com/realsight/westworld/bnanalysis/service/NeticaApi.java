package com.realsight.westworld.bnanalysis.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.basic.Separate;
import com.realsight.westworld.bnanalysis.io.ReadCSV;
import com.realsight.westworld.bnanalysis.io.WriteCSV;

import norsys.netica.*;

public class NeticaApi {

	public BanjoApi banjo;
	public Environ env;
	public Net net;
	public Map<String, String[]> rangeMap;
	
	public NeticaApi() throws Exception {}
	
	/************************************************************ 建网 *********************************************************/
	
	public void buildNet(String original_csv, int num_thread, int num_bins) throws Exception {
		
		banjo = new BanjoApi(original_csv, num_thread, num_bins);
		
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
		
//		Node.setConstructorClass("norsys.neticaEx.aliases.Node");
		env = new Environ(null);
		
		net = new Net();
		net.setName("apm");
		
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
		
		
		net.write(new Streamer("netica_out_dir/Learned_netica.dne"));

		WriteCSV writeCSV = new WriteCSV();
		writeCSV.writeMapList(mapList, attrList,"netica_out_dir", "range_list.csv");
		ReadCSV readCSV = new ReadCSV();
		rangeMap = readCSV.readRangeList("netica_out_dir/range_list.csv");
		
		NodeList nodes = net.getNodes();
		Streamer caseFile = new Streamer("separate_out_dir/separated.cas");
		net.reviseCPTsByCaseFile(caseFile, nodes, 1.0);
		net.write(new Streamer("netica_out_dir/Learned_netica_CPT.dne"));
//		net.compile();
	}
	
	/************************************************* load网络 *****************************************************/
	
	public void loadSimuNet() throws NeticaException, IOException {
		env = new Environ(null);
		net = new Net(new Streamer ("netica_out_dir/Learned_netica.dne"));
		net.setName("apm");
		
		NodeList nodes = net.getNodes();
		Streamer caseFile = new Streamer("simuLoad_out_dir/simuLoad.cas");
		net.reviseCPTsByCaseFile(caseFile, nodes, 1.0);
		
		net.write(new Streamer("simuTest.dne"));
		
		net.compile();
	}
	
	public void loadNet() throws NeticaException, IOException {
		env = new Environ(null);
		net = new Net (new Streamer ("netica_out_dir/Learned_netica.dne"));
		net.setName("apm");
		loadRangeMap();
		
		NodeList nodes = net.getNodes();
		Streamer caseFile = new Streamer("separate_out_dir/separated.cas");
		net.reviseCPTsByCaseFile(caseFile, nodes, 1.0);
		
		net.compile();
	}
	
	public void loadNet(String dne_file, String cas_file) throws NeticaException, IOException {
		
		env = new Environ(null);
		net = new Net (new Streamer (dne_file));
		net.setName("apm");
		loadRangeMap();
		
		NodeList nodes = net.getNodes();
		Streamer caseFile = new Streamer(cas_file);
		net.reviseCPTsByCaseFile(caseFile, nodes, 1.0);
		
		net.compile();
	}
	
	public void loadNet(String dne_file) throws NeticaException {
		
		env = new Environ(null);
		net = new Net (new Streamer (dne_file));
		net.compile();
	}
	
	/******************************************************** 将数字转化成字母状态 *******************************************/
	
	private String getStates(int numstate) {
		String str = "";
		for (int i = 0; i < numstate; i++) {
			str += (char) ('a'+i);
			if (i < numstate-1)
				str += ", ";
		}
		return str;
	}
	
	public String getState(int state) {
		String str = "";
		
		str += ((char) ('a'+state));
		
		return str;
	}
	
	/************************************************************ 变量及其对应的离散区间 **************************************************/
	
	public void loadRangeMap() throws IOException {
		ReadCSV readCSV = new ReadCSV();
		rangeMap = readCSV.readRangeList("netica_out_dir/range_list.csv");
	}
	
	public Map<String, String[]> getRangeMap() {
		return rangeMap;
	}
	
	public void printRangeMap() {
		System.out.println();
		for (String it : rangeMap.keySet()) {
			System.out.print(it + ":\t");
			for (String it2 : rangeMap.get(it)) {
				System.out.print(it2 + " ");
			}
			System.out.println();
		}
	}
	
	/***************************************************************** 获得变量概率分布 ***************************************************/
	
	public List<Double> getDistribution(String strVar) throws NeticaException {
		Node node = net.getNode(strVar);
		int numState = node.getNumStates();
		
		List<Double> resList = new ArrayList<Double>();
		
		for (int i = 0; i < numState; i++) {
			double tmp = node.getBelief(""+((char)('a'+i)));
			resList.add(tmp);
		}
		
		return resList;
	}
	
	/************************************************************* 获得结点  
	 * @throws NeticaException ***************************************************/
	
	public List<String> getGONodes() throws NeticaException {
		
		List<String> list = new ArrayList<String>();
		NodeList nodeList = net.getNodes();
		for (int i = 0; i < nodeList.size(); i++) {
			list.add(nodeList.get(i).toString());
		}
		
		return list;
	}
	
	/************************************************************* 获得父结点  ***************************************************/
	
	public List<String> getParents(Node node) throws NeticaException {
		return getParents(node.toString());
	}
	
	public List<String> getParents(String node_name) throws NeticaException {
		List<String> parents = new ArrayList<String>();
		boolean exist = false;
		for (String it : rangeMap.keySet()) {
			if (it.equals(node_name)) {
				exist = true;
			}
		}
		if (!exist) {
			System.out.println(node_name + " is not in the attrList");
		}
		NodeList nodeList = net.getNode(node_name).getParents();
		for (int i = 0; i < nodeList.size(); i++) {
			parents.add(nodeList.get(i).toString());
		}
		return parents;
	}
	
	
	/************************************************************* 获得孩子结点 ********************************************************/
	
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
	
	/******************************************************** 概率推理 ***************************************************/
	
	public double getInfer(List<Pair<String, String>> conds, Pair<String, String> target) throws NeticaException {
		
		double res;
		Node[] nodes = new Node[conds.size()];
		for (int i = 0; i < conds.size(); i++) {
			 nodes[i] = net.getNode(conds.get(i).first);
			 nodes[i].finding().enterState(conds.get(i).second);
		}
		Node targetNode = net.getNode(target.first);
		res = targetNode.getBelief(target.second);
		
		for (int i = 0; i < nodes.length; i++ ) {
			nodes[i].finding().clear();
		}
		
		return res;
	}
	
	public double getInfer(List<Pair<String, float[]>> hoods, Pair<String, String> target, String likelihood) throws NeticaException {
		double res;
		Node[] nodes = new Node[hoods.size()];
		for (int i = 0; i < hoods.size(); i++) {
			nodes[i] = net.getNode(hoods.get(i).first);
			nodes[i].finding().enterLikelihood(hoods.get(i).second);
		}
		
		Node targetNode = net.getNode(target.first);
		res = targetNode.getBelief(target.second);
		
		for (int i = 0; i < nodes.length; i++ ) {
			nodes[i].finding().clear();
		}
		
		return res;
	}
	
	public double getInfer(Pair<String, float[]> hood, Pair<String, String> target, String likelihood) throws NeticaException {
		double res;
		
		Node node = net.getNode(hood.first);
		node.finding().enterLikelihood(hood.second);
		Node targetNode = net.getNode(target.first);
		res = targetNode.getBelief(target.second);
		
		node.finding().clear();
		
		return res;
	}
	
	public double getInfer(String conds, String target) throws NeticaException {
		String[] strConds = conds.split(",");
		List<Pair<String, String>> condsList = new ArrayList<Pair<String, String>>();
		for (int i = 0; i < strConds.length; i++) {
			String[] strPair = strConds[i].split(":");
			condsList.add(new Pair<String, String>(strPair[0], strPair[1]));
		}
		
		String[] strTarget = target.split(":");
		
		Pair<String, String> pairTarget = new Pair<String, String>(strTarget[0], strTarget[1]); 
		return getInfer(condsList, pairTarget);
	}
	
	/************************************************************* 期望推理 *********************************************************/
	
	public double getExeption(String conds, String target) throws NeticaException {
		String[] strConds = conds.split(",");
		List<Pair<String, String>> condsList = new ArrayList<Pair<String, String>>();
		for (int i = 0; i < strConds.length; i++) {
			String[] strPair = strConds[i].split(":");
			condsList.add(new Pair<String, String>(strPair[0], strPair[1]));
		}
		
		return getExeption(condsList, target);
	}
	
	public double getExeption(List<Pair<String, String>> conds, String target) throws NeticaException {
		Node node = net.getNode(target);
		double res = 0;
		
		for (int i = 0; i < node.getNumStates(); i++) {
			double belief = getInfer(conds, new Pair<String, String> (target, getState(i)));
			res += i*belief;
		}
		return res;
	}
	
	public double getExeption(Pair<String, float[]> hood, String target, String likelihood) throws NeticaException {
		double res = 0;
		
		Node node = net.getNode(target);
		
		for (int i = 0; i < node.getNumStates(); i++) {
			double belief = getInfer(hood, new Pair<String, String> (target, getState(i)), "likelihood");
			res += i*belief;
		}
		
		return res;
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
