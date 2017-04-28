package com.realsight.westworld.bnanalysis.service;

import java.util.List;

import com.realsight.westworld.bnanalysis.basic.Pair;

import norsys.netica.NeticaException;
import norsys.netica.Node;

public class TSPredict {

	public NeticaApi netica;
	
	public TSPredict() {}
	
	public void loadNet() throws Exception {
		netica = new NeticaApi();
		netica.loadNet("netica_out_dir/Learned_netica_CPT.dne");
	}
	
	public void setStates(List<Pair<String, String>> tsp_states) throws NeticaException {
		for (Pair<String, String> it : tsp_states) {
			Node node = netica.net.getNode(it.first);
			node.finding().enterState(it.second);
		}
	}
	
	public double getBeliefOf(String var_state) throws NeticaException {  //冒号分割  "http_times:c"
		String[] strList = var_state.split(":");
		Node node = netica.net.getNode(strList[0]); //这里应有一个检查函数 1)有两项吗；2)var有吗；3)state有吗
		double belief = node.getBelief(strList[1]);
		return belief;
	}
	
	@Override
	public void finalize() throws NeticaException {
		netica.finalize();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
