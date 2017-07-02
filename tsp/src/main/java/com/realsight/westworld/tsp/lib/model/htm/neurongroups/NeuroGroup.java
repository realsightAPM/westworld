package com.realsight.westworld.tsp.lib.model.htm.neurongroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realsight.westworld.tsp.lib.model.htm.context.ActiveContext;
import com.realsight.westworld.tsp.lib.model.reinforcement.SARSA;
import com.realsight.westworld.tsp.lib.util.Entry;
import com.realsight.westworld.tsp.lib.util.Triple;

public class NeuroGroup implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7024856854549187135L;
	private int maxActiveNeuronsNum;
	private int maxRemeberNeuronsNum;
	@SuppressWarnings("unused")
	private int maxValidActiveNeuronsNum;
	private List<Integer> leftFactsGroup;
	private List<Integer> activeNeuros;
	private NeuroGroupOperator neuroGroupOperator;
	private List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> potentialNewContextList;
	private boolean hasAction = false;
	private List<ActiveContext> prevActiveContexts;
	private List<ActiveContext> currActiveContexts;
	private SARSA sarsa = null;
	private String currAction = null;
	private String prevAction = null;
	
	public NeuroGroup(int maxActiveNeuronsNum, 
			int maxLeftSemiContextsLenght, 
			int maxRemeberNeuronsNum, 
			int maxValidActiveNeuronsNum,
			boolean hasAction, List<String> actions) {
		this.maxActiveNeuronsNum = maxActiveNeuronsNum;
		this.leftFactsGroup = new ArrayList<Integer>();
		this.neuroGroupOperator = new NeuroGroupOperator(maxLeftSemiContextsLenght, maxValidActiveNeuronsNum);
		this.potentialNewContextList = new ArrayList<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>>();
		this.maxRemeberNeuronsNum = maxRemeberNeuronsNum;
		this.maxValidActiveNeuronsNum = maxValidActiveNeuronsNum;
		this.hasAction = hasAction;
		this.sarsa = new SARSA(actions);
		this.prevActiveContexts = null;
		this.currActiveContexts = null;
	}
	
	public NeuroGroup(int maxActiveNeuronsNum, 
			int maxLeftSemiContextsLenght, 
			int maxRemeberNeuronsNum, 
			int maxValidActiveNeuronsNum) {
		this(maxActiveNeuronsNum, maxLeftSemiContextsLenght, maxRemeberNeuronsNum, maxValidActiveNeuronsNum, false, null);
	}

	public List<Integer> getLeftFactsGroup() {
		return leftFactsGroup;
	}

	public NeuroGroupOperator getNeuroGroupOperator() {
		return neuroGroupOperator;
	}

	public List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> getPotentialNewContextList() {
		return potentialNewContextList;
	}
	
	public void sleep() {
		this.leftFactsGroup.clear();
		this.potentialNewContextList.clear();
		this.activeNeuros = new ArrayList<Integer>();
		this.neuroGroupOperator.sleep();
//		this.neuroGroupOperator.contextCrosser(0, this.leftFactsGroup, false, false, this.potentialNewContextList);
//		this.neuroGroupOperator.contextCrosser(1, this.leftFactsGroup, false, false, this.potentialNewContextList);
	}
	
	private double activate(List<Integer> currSensFacts, boolean actionOpt) {
		
//		for(int i = 0; i < this.leftFactsGroup.size(); i++)
//			System.out.print(" {" + this.neuroGroupOperator.getLevelFromContextID(this.leftFactsGroup.get(i)) + 
//					", " + this.neuroGroupOperator.getActivedNumberFromContextID(this.leftFactsGroup.get(i)) + "} ");
//		System.out.print("-> ");
//		for(int i = 0; i < currSensFacts.size(); i++)
//			System.out.print(currSensFacts.get(i) + " ");
//		System.out.print("\n");
		currSensFacts = new ArrayList<Integer>(new HashSet<Integer>(currSensFacts));
		Collections.sort(currSensFacts);
		List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> potNewZeroLevelContext = 
				new ArrayList<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>>();
		int newContextFlag = -1;
		if ( (this.leftFactsGroup.size()>0) && (currSensFacts.size()>0) ) {
			Entry<List<Integer>, List<Integer>> p_1 = new Entry<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts);
			Entry<Double, Double> p_2 = new Entry<Double, Double>(0.0, 0.0);
			Map<String, Double> p_3 = new HashMap<String, Double>();
			Double level = 0.0;
			for (Integer fact : leftFactsGroup) {
				level = Math.max(this.neuroGroupOperator.getLevelFromContextID(fact), level);
			}
			p_3.put("level", level+1.0);
			potNewZeroLevelContext.add(new Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>(p_1, p_2, p_3));
            newContextFlag = this.neuroGroupOperator.getContextByFacts(potNewZeroLevelContext, 1);
		}
		this.neuroGroupOperator.contextCrosser(1, 
				currSensFacts,(newContextFlag>=0), 
				true,  
				new ArrayList<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>>());
		
		double percentSelectedContextActive = 0.0;
		if (this.neuroGroupOperator.getNumSelectedContext() > 0) {
			percentSelectedContextActive = this.neuroGroupOperator.getActiveContexts().size();
			percentSelectedContextActive /= this.neuroGroupOperator.getNumSelectedContext();
		}
		
//		for(int s = 0; s < this.neuroGroupOperator.getActiveContexts().size(); s++) {
//			ActiveContext t = this.neuroGroupOperator.getActiveContexts().get(s);
//			System.out.print(t.getContextID() + ",");
//		}
//		System.out.println("activate");
		
		Set<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> activeContext = 
				new HashSet<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>>();
		activeContext.addAll(this.neuroGroupOperator.getPotentialNewContextList());
		
		if ( (this.leftFactsGroup.size()>0) && (currSensFacts.size()>0) ) {
			Entry<List<Integer>, List<Integer>> p_1 = new Entry<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts);
			Entry<Double, Double> p_2 = new Entry<Double, Double>(0.0, 0.0);
			Map<String, Double> p_3 = new HashMap<String, Double>();
			activeContext.add(new Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>(p_1, p_2, p_3));
//			System.out.println(activeContext.contains(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts)));
		}
		
		Collections.sort(this.neuroGroupOperator.getActiveContexts());
		this.leftFactsGroup = new ArrayList<Integer>();
		for ( int i = 0; i<this.neuroGroupOperator.getActiveContexts().size() && i<this.maxActiveNeuronsNum; i++){
			this.leftFactsGroup.add(this.neuroGroupOperator.getActiveContexts().get(i).getContextID()+(1<<30));
		}
		for ( int i = 0; i < currSensFacts.size(); i++ ) {
			this.leftFactsGroup.add(currSensFacts.get(i));
		}
		Collections.sort(this.leftFactsGroup);
		this.activeNeuros = new ArrayList<Integer>();
		for ( int i = 0; i < this.neuroGroupOperator.getActiveContexts().size(); i++ ) {
			this.activeNeuros.add(this.neuroGroupOperator.getActiveContexts().get(i).getContextID());
		}
		
		this.potentialNewContextList = this.neuroGroupOperator.getPotentialNewContextList();
		
//		for(int s = 0; s < this.potentialNewContextList.size(); s++) {
//			Pair<List<Integer>, List<Integer>> t = this.potentialNewContextList.get(s);
//			System.out.print("(");
//			for(int i = 0; i < t.getA().size(); i++)
//				System.out.print(t.getA().get(i) + ",");
//			System.out.print(");(");
//			for(int i = 0; i < t.getB().size(); i++)
//				System.out.print(t.getB().get(i) + ",");
//			System.out.print(")\n");
//		}
		this.neuroGroupOperator.contextCrosser(0, this.leftFactsGroup, false, 
				this.neuroGroupOperator.getActiveContexts().size()<this.maxRemeberNeuronsNum, this.potentialNewContextList);
		double percentaddContextActive = 0.0;
		
//		this.neuroGroupOperator.contextCrosser(0, this.leftFactsGroup, false, 
//				true, this.potentialNewContextList);
//		double percentaddContextActive = 0.0;
		
		if ( activeContext.size() > 0 ) {
			double newContextNum = this.neuroGroupOperator.getNumAddedContexts();
			if ( newContextFlag >= 0 )
				newContextNum += 1.0;
			percentaddContextActive = newContextNum / activeContext.size();
//			System.out.println(percentSelectedContextActive + "," + newContextNum + "," + activeContext.size()+","+newContextFlag);
//			System.out.println((1.0 - percentSelectedContextActive + percentaddContextActive) / 2.0);
		}
		if (actionOpt == false){
			this.prevActiveContexts = this.currActiveContexts;
			this.currActiveContexts = this.neuroGroupOperator.getActiveContexts();
		}
		return (1.0 - percentSelectedContextActive)*0.50 + (percentaddContextActive)*0.50; 
	}
	
	public double learnSeries(List<Integer> currSensFacts) {
		return activate(currSensFacts, false);
	}
	
	public double predict(List<Integer> currSensFacts) {
		currSensFacts = new ArrayList<Integer>(new HashSet<Integer>(currSensFacts));
		Collections.sort(currSensFacts);
		return this.neuroGroupOperator.activeLogLikelihood(currSensFacts);
	}
	
	public String action() {
		if (! this.hasAction) return null;
		return sarsa.chooseAction(currActiveContexts, neuroGroupOperator.getContextsValuesList());
	}
	
	public void learnActions(String currAction, Double reward) {
		if (! this.hasAction) return ;
		this.prevAction = this.currAction;
		this.currAction = currAction;
		sarsa.sarsa(prevAction,
				currAction, 
				prevActiveContexts, 
				neuroGroupOperator.getContextsValuesList(),
				reward);
	}
	
	public double activeLogLikelihood(List<Integer> currSensFacts) {
		Collections.sort(currSensFacts);
		return this.neuroGroupOperator.activeLogLikelihood(currSensFacts);
	}
	
	public List<ActiveContext> getPotentialActiveContexts() {
		return this.neuroGroupOperator.getPotentialActiveContexts();
	}
	
	public List<Integer> getActiveNeuros() {
		return activeNeuros;
	}
}

