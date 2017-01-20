package com.realsight.brain.timeseries.lib.model.htm.neurongroups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realsight.brain.timeseries.lib.model.htm.context.*;
import com.realsight.brain.timeseries.lib.util.Pair;

public class NeuroGroupOperator {
	private int maxLeftSemiContextsLenght;
	private Map<Integer,List<ContextValue>>[] factsDics;
	private List<ContextValue>[] semiContextValuesLists;
	private Map<Integer,Integer>[] semiContextDics;
	private List<ContextValue>[] crossedSemiContextsLists;
	private List<CrossedContext> contextsValuesList;
	private int newContextID;
	private List<ActiveContext> activeContexts;
	private int numSelectedContext = 0;
	private List<Pair<List<Integer>, List<Integer>>> potentialNewContextList;
	private int numAddedContexts;
	private boolean rate = true;
	private List<Long> beActivatedList;
	private Long timestamp;
	private Map<Long, Integer> history;
	
	public List<ActiveContext> getActiveContexts() {
		return activeContexts;
	}

	public int getNumSelectedContext() {
		return numSelectedContext;
	}

	public List<Pair<List<Integer>, List<Integer>>> getPotentialNewContextList() {
		return potentialNewContextList;
	}

	public int getNumAddedContexts() {
		return numAddedContexts;
	}
	
	public void setRate(boolean rate) {
		this.rate = rate;
	}
	
	public List<Long> getBeActivatedList() {
		return this.beActivatedList;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		if ( timestamp == -1 ) {
			for ( int i = 0; i < this.contextsValuesList.size(); i++ ) {
				this.contextsValuesList.get(i).setMemory(new ArrayList<Long>());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public NeuroGroupOperator(int maxLeftSemiContextsLenght) {
		this.maxLeftSemiContextsLenght = maxLeftSemiContextsLenght;
		this.factsDics = new Map[2];
		this.factsDics[0] = new HashMap<Integer, List<ContextValue>>();
		this.factsDics[1] = new HashMap<Integer, List<ContextValue>>();
		this.semiContextValuesLists = new List[2];
		this.semiContextValuesLists[0] = new ArrayList<ContextValue>();
		this.semiContextValuesLists[1] = new ArrayList<ContextValue>();
		this.semiContextDics = new Map[2];
		this.semiContextDics[0] = new HashMap<Integer, Integer>();
		this.semiContextDics[1] = new HashMap<Integer, Integer>();
		this.crossedSemiContextsLists = new List[2];
		this.crossedSemiContextsLists[0] = new ArrayList<ContextValue>();
		this.crossedSemiContextsLists[1] = new ArrayList<ContextValue>();
		this.contextsValuesList = new ArrayList<CrossedContext>();
		this.newContextID = -1;
		this.history = new HashMap<Long, Integer>();
	}
	
	public int getContextByFacts(List<Pair<List<Integer>, List<Integer>>> newContextsList, int zeroLevel){
		int numAddedContexts = 0;
		if ( rate ) {
			for(int i = 0; i < newContextsList.size(); i++){
				List<Integer> leftFacts = newContextsList.get(i).getA();
				List<Integer> rightFacts = newContextsList.get(i).getB();
				Collections.sort(leftFacts);
				Collections.sort(rightFacts);
				int leftHash = leftFacts.hashCode();
				int rightHash = rightFacts.hashCode();
			
				int nextLeftSemiContextNumber = semiContextDics[0].size();
				if(!this.semiContextDics[0].containsKey(leftHash)){
					this.semiContextDics[0].put(leftHash, nextLeftSemiContextNumber);
				}
				int leftSemiContextID = this.semiContextDics[0].get(leftHash);
				if (leftSemiContextID == nextLeftSemiContextNumber){
					ContextValue leftSemiContextValues = new ContextValue(leftFacts.size());
					semiContextValuesLists[0].add(leftSemiContextValues);
					for(Integer fact : leftFacts){
						if(!this.factsDics[0].containsKey(fact)){
							this.factsDics[0].put(fact, new ArrayList<ContextValue>());
						}
						List<ContextValue> semiContextList = this.factsDics[0].get(fact);
						semiContextList.add(leftSemiContextValues);
					}
				}
				
				int nextRightSemiContextNumber = semiContextDics[1].size();
				if(!this.semiContextDics[1].containsKey(rightHash)){
					this.semiContextDics[1].put(rightHash, nextRightSemiContextNumber);
				}
				int rightSemiContextID = this.semiContextDics[1].get(rightHash);
				if (rightSemiContextID == nextRightSemiContextNumber){
					ContextValue rightSemiContextValues = new ContextValue(rightFacts.size());
					semiContextValuesLists[1].add(rightSemiContextValues);
					for(Integer fact : rightFacts){
						if(!this.factsDics[1].containsKey(fact)){
							this.factsDics[1].put(fact, new ArrayList<ContextValue>());
						}
						List<ContextValue> semiContextList = this.factsDics[1].get(fact);
						semiContextList.add(rightSemiContextValues);
					}
				}
				int nextFreeContextIDNumber = this.contextsValuesList.size();
				if(!this.semiContextValuesLists[0].get(leftSemiContextID).getContexIDs().containsKey(rightSemiContextID)){
					this.semiContextValuesLists[0].get(leftSemiContextID).getContexIDs().put(rightSemiContextID, nextFreeContextIDNumber);
				}
			    int contextID = this.semiContextValuesLists[0].get(leftSemiContextID).getContexIDs().get(rightSemiContextID);
			    if (contextID == nextFreeContextIDNumber){
	//		    	System.out.println(contextID);
//			    	System.out.print((contextID+(1<<30)) + " : ");
//			    	for(int ii = 0; ii < leftFacts.size(); ii++)
//						System.out.print(leftFacts.get(ii) + " ");
//					System.out.print("-> ");
//					for(int ii = 0; ii < rightFacts.size(); ii++)
//						System.out.print(rightFacts.get(ii) + " ");
//					System.out.print("\n");
			    	numAddedContexts += 1;
			    	CrossedContext crossedContexts = new CrossedContext(0, zeroLevel, leftHash, rightHash);
			    	this.contextsValuesList.add(crossedContexts);
			    	if (zeroLevel > 0) {
			    		this.newContextID = contextID;
			    		return 0;
			    	}
			    } else {
			    	CrossedContext crossedContexts = this.contextsValuesList.get(contextID);
			    	if (zeroLevel > 0) {
			    		crossedContexts.setZeroLevel(1);
			    		return -1;
			    	}
			    }
			}
		} else {
			Set<Pair<List<Integer>, List<Integer>>> set = new HashSet<Pair<List<Integer>, List<Integer>>>();
			set.addAll(newContextsList);
			for ( Pair<List<Integer>, List<Integer>> newContexts : set) {
				List<Integer> leftFacts = newContexts.getA();
				List<Integer> rightFacts = newContexts.getB();
				Collections.sort(leftFacts);
				Collections.sort(rightFacts);
				int leftHash = leftFacts.hashCode();
				int rightHash = rightFacts.hashCode();
				if(!this.semiContextDics[0].containsKey(leftHash)){
					numAddedContexts += 1.0;
					continue;
				}
				int leftSemiContextID = this.semiContextDics[0].get(leftHash);
				
				if(!this.semiContextDics[1].containsKey(rightHash)){
					numAddedContexts += 1.0;
					continue;
				}
				int rightSemiContextID = this.semiContextDics[1].get(rightHash);
				
				if(!this.semiContextValuesLists[0].get(leftSemiContextID).getContexIDs().containsKey(rightSemiContextID)){
					numAddedContexts += 1.0;
					continue;
				}
			}
		}
		return numAddedContexts;
	}
	
	
	public void contextCrosser(int leftOrRight, List<Integer> factsList, boolean newContextFlag, List<Pair<List<Integer>, List<Integer>>> potentialNewContexts) {
		if (leftOrRight == 0) {
			if ( potentialNewContexts.size() > 0 )
				this.numAddedContexts = this.getContextByFacts (potentialNewContexts, 0);
			else 
				this.numAddedContexts = 0;
		}
		for(int i = 0; i < this.crossedSemiContextsLists[leftOrRight].size(); i++){
			this.crossedSemiContextsLists[leftOrRight].get(i).setFromOrto(new ArrayList<Integer>());
		}
		for (int i = 0; i < factsList.size(); i++) {
			Integer fact = factsList.get(i);
			if(!this.factsDics[leftOrRight].containsKey(fact)){
				this.factsDics[leftOrRight].put(fact, new ArrayList<ContextValue>());
			}
			for(int j = 0; j < this.factsDics[leftOrRight].get(fact).size(); j++) {
				this.factsDics[leftOrRight].get(fact).get(j).getFromOrto().add(fact);
			}
		}
		List<ContextValue> newCrossedValues = new ArrayList<ContextValue>();
		for ( int i = 0; i < this.semiContextValuesLists[leftOrRight].size(); i++ ) {
			ContextValue semiContextValues = this.semiContextValuesLists[leftOrRight].get(i);
            if ( semiContextValues.getFromOrto().size() > 0 ) {
                newCrossedValues.add(semiContextValues);
            }
		}
		this.crossedSemiContextsLists[leftOrRight] = newCrossedValues;
		if ( leftOrRight == 1 ) {
			updateContextsAndGetActive(newContextFlag);
		}
		
	}
	
	public void updateContextsAndGetActive(boolean newContextFlag) {

        List<ActiveContext> activeContexts = new ArrayList<ActiveContext>();
        List<Long> beActivatedList = new ArrayList<Long>();
        int numSelectedContext = 0;
        
        List<Pair<List<Integer>, List<Integer>>> potentialNewContextList = new ArrayList<Pair<List<Integer>, List<Integer>>>();

        for ( int i = 0; i < this.crossedSemiContextsLists[0].size(); i++) {
        	ContextValue leftSemiContextValues = this.crossedSemiContextsLists[0].get(i);
        	for (Map.Entry<Integer, Integer> entry : leftSemiContextValues.getContexIDs().entrySet()) {
        		int rightSemiContextID = entry.getKey();
        		int contextID = entry.getValue();
        		if ( this.newContextID != contextID ) {
        			ContextValue rightSemiContextValue = this.semiContextValuesLists[1].get(rightSemiContextID);
        			if ( leftSemiContextValues.getFromOrto().size() == leftSemiContextValues.getLenFact() ) {
        				numSelectedContext += 1;
        				
//        				System.out.println(rightSemiContextValue.getFromOrto().size());
        				if ( rightSemiContextValue.getFromOrto().size() > 0 ) {
        					if ( rightSemiContextValue.getFromOrto().size() == rightSemiContextValue.getLenFact() ) {
        						Iterator<Long> iter_memory = this.contextsValuesList.get(contextID).getMemory().iterator();
                				while ( iter_memory.hasNext() ) {
                					beActivatedList.add(iter_memory.next());
                				}
                				this.contextsValuesList.get(contextID).beActivated(timestamp);
        						int tmp = this.contextsValuesList.get(contextID).getNumActivate();
        						this.contextsValuesList.get(contextID).setNumActivate(tmp+1);
        						activeContexts.add(new ActiveContext(contextID, this.contextsValuesList.get(contextID).getNumActivate(), 
        								this.contextsValuesList.get(contextID).getLeftHash(), this.contextsValuesList.get(contextID).getRightHash()));
        					} else if ( this.contextsValuesList.get(contextID).getZeroLevel()==1 && 
        							newContextFlag && leftSemiContextValues.getFromOrto().size()<= this.maxLeftSemiContextsLenght ){
        						Pair<List<Integer>, List<Integer>> p = 
        								new Pair<List<Integer>, List<Integer>>(leftSemiContextValues.getFromOrto(), rightSemiContextValue.getFromOrto());
        						potentialNewContextList.add(p);
        					}
        				}
        					
        			} else if (  this.contextsValuesList.get(contextID).getZeroLevel()==1 && rightSemiContextValue.getFromOrto().size()>0 && 
							newContextFlag && leftSemiContextValues.getFromOrto().size()<=this.maxLeftSemiContextsLenght ) {
        				Pair<List<Integer>, List<Integer>> p = 
								new Pair<List<Integer>, List<Integer>>(leftSemiContextValues.getFromOrto(), rightSemiContextValue.getFromOrto());
						potentialNewContextList.add(p);
        			}
        		}
        	}
        }
        
        this.newContextID = -1;
        this.activeContexts = activeContexts;
        this.numSelectedContext = numSelectedContext;
        this.potentialNewContextList = potentialNewContextList;
        this.beActivatedList = beActivatedList;
        this.history.put(timestamp, activeContexts.size());
//      if (this.timestamp == -1L) {
//        	Iterator <Long> iter = this.beActivatedList.iterator();
//        	Map<Long, Integer> count = new HashMap<Long, Integer>(); 
//        	while(iter.hasNext()) {
//        		Long t = iter.next();
//        		if ( !count.containsKey(t) )
//        			count.put(t, 1);
//        		else {
//        			int c = count.get(t);
//        			count.put(t, c+1);
//        		}
//        	}
//        	for ( Map.Entry<Long, Integer> entry : count.entrySet()) {
//        		System.out.println(entry.getKey() + "\t" + (1.0*entry.getValue()/this.history.get(entry.getKey())));
//        	}
//        }
//        System.out.println("up - > " + activeContexts.size()+","+numSelectedContext+","+potentialNewContextList.size());
	}
}

