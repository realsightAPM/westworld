package com.realsight.westworld.bnanalysis.service;

import java.util.List;

import com.realsight.westworld.bnanalysis.api.NeticaApi;
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
	
	public double getBeliefOf(String var_state) throws NeticaException {  //ð�ŷָ�  "http_times:c"
		String[] strList = var_state.split(":");
		Node node = netica.net.getNode(strList[0]); //����Ӧ��һ����麯�� 1)��������2)var����3)state����
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
