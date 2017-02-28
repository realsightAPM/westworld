package com.bnAnalysis;

import java.util.ArrayList;
import java.util.List;

import Initialization.Separate;
import norsys.netica.*;
import norsys.neticaEx.aliases.Node;

public class NeticaApi {

	public Net net;
	public BanjoApi banjo;
	
	public NeticaApi() throws Exception {
		banjo = new BanjoApi();
		buildNet();
	}
	
	public NeticaApi(String csvinput, int num_thread) throws Exception {
		banjo = new BanjoApi(csvinput, num_thread);
		buildNet();
	}
	
	public double getCurrentRisk() {
		return 0;
	}
	
	private void buildNet() throws Exception {
		
		Node.setConstructorClass("norsys.neticaEx.aliases.Node");
		Environ env = new Environ(null);
		
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
		
		net.write(new Streamer("Learned_netica.dne"));
		
		net.finalize();
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
	
	public static void main(String[] args) throws Exception {
		
		NeticaApi netica = new NeticaApi();
		
		System.out.println("输出netica学习的网络结果");
	}
	
}
