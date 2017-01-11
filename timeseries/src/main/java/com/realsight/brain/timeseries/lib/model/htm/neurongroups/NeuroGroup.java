package com.realsight.brain.timeseries.lib.model.htm.neurongroups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.realsight.brain.timeseries.lib.util.Pair;

public class NeuroGroup {
	
	private int maxActiveNeuronsNum;
	private List<Integer> leftFactsGroup;
	private List<Integer> activeNeuros;
	private NeuroGroupOperator neuroGroupOperator;
	private List<Pair<List<Integer>, List<Integer>>> potentialNewContextList;
	
	public NeuroGroup(int maxActiveNeuronsNum, int maxLeftSemiContextsLenght) {
		this.maxActiveNeuronsNum = maxActiveNeuronsNum;
		this.leftFactsGroup = new ArrayList<Integer>();
		this.neuroGroupOperator = new NeuroGroupOperator(maxLeftSemiContextsLenght);
		this.potentialNewContextList = new ArrayList<Pair<List<Integer>, List<Integer>>>();
	}
	
	public void sleep() {
		this.leftFactsGroup = new ArrayList<Integer>();
		this.potentialNewContextList = new ArrayList<Pair<List<Integer>, List<Integer>>>();
		List<Integer> currSensFacts = new ArrayList<Integer>();
		this.activate(currSensFacts, false, -1L);
	}
	
	private double activate(List<Integer> currSensFacts, boolean rate, Long timestamp) {
		this.neuroGroupOperator.setRate(rate);
		this.neuroGroupOperator.setTimestamp(timestamp);
		
		for(int i = 0; i < this.leftFactsGroup.size(); i++)
			System.out.print(this.leftFactsGroup.get(i) + " ");
		System.out.print("-> ");
		for(int i = 0; i < currSensFacts.size(); i++)
			System.out.print(currSensFacts.get(i) + " ");
		System.out.print("\n");
		currSensFacts = new ArrayList<Integer>(new HashSet<Integer>(currSensFacts));
		Collections.sort(currSensFacts);
//		for(int i = 0; i < leftFactsGroup.size(); i++)
//			System.out.print(leftFactsGroup.get(i) + ",");
//		System.out.print("\n");
		List<Pair<List<Integer>, List<Integer>>> potNewZeroLevelContext = 
				new ArrayList<Pair<List<Integer>, List<Integer>>>();
		int newContextFlag = -1;
		if ( (this.leftFactsGroup.size()>0) && (currSensFacts.size()>0) ) {
			potNewZeroLevelContext.add(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts));
            newContextFlag = this.neuroGroupOperator.getContextByFacts(potNewZeroLevelContext, 1);
		}
		this.neuroGroupOperator.contextCrosser(1, currSensFacts, newContextFlag>=0, new ArrayList<Pair<List<Integer>, List<Integer>>>());
		double percentSelectedContextActive = 0.0;
		if (this.neuroGroupOperator.getNumSelectedContext() > 0) {
			percentSelectedContextActive = 
					1.0 * this.neuroGroupOperator.getActiveContexts().size() / this.neuroGroupOperator.getNumSelectedContext();
		}
		
		Set<Pair<List<Integer>, List<Integer>>> activeContext = new HashSet<Pair<List<Integer>, List<Integer>>>();
		activeContext.addAll(this.neuroGroupOperator.getPotentialNewContextList());
		
		if ( (this.leftFactsGroup.size()>0) && (currSensFacts.size()>0) ) {
//			System.out.println(activeContext.contains(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts)));
			activeContext.add(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts));
//			System.out.println(activeContext.contains(new Pair<List<Integer>, List<Integer>>(this.leftFactsGroup, currSensFacts)));
		}
		
//		for(Pair<List<Integer>, List<Integer>> t : activeContext) {
//			System.out.print("(");
//			for(int i = 0; i < t.getA().size(); i++)
//				System.out.print(t.getA().get(i) + ",");
//			System.out.print(");(");
//			for(int i = 0; i < t.getB().size(); i++)
//				System.out.print(t.getB().get(i) + ",");
//			System.out.print("), "+t.hashCode()+"\n");
//		}
		
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
		
		this.neuroGroupOperator.contextCrosser(0, this.leftFactsGroup, false, this.potentialNewContextList);
		double percentaddContextActive = 0.0;
		if ( activeContext.size() > 0 ) {
			double newContextNum = this.neuroGroupOperator.getNumAddedContexts();
			if ( newContextFlag >= 0 )
				newContextNum += 1.0;
			percentaddContextActive = newContextNum / activeContext.size();
//			System.out.println(percentSelectedContextActive + "," + newContextNum + "," + activeContext.size()+","+newContextFlag);
//			System.out.println((1.0 - percentSelectedContextActive + percentaddContextActive) / 2.0);
		}
		
//		System.out.println(percentaddContextActive);
		return (1.0 - percentSelectedContextActive + percentaddContextActive) / 2.0; 
	}
	
	public double learn(List<Integer> currSensFacts, Long timestamp) {
		return activate(currSensFacts, true, timestamp);
	}
	
	public double predict(List<Integer> currSensFacts, Long timestamp) {
		return activate(currSensFacts, false, timestamp);
	}

	public List<Integer> getActiveNeuros() {
		return activeNeuros;
	}
}

