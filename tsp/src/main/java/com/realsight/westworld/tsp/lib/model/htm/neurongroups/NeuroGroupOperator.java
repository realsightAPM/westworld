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
import com.realsight.westworld.tsp.lib.model.htm.context.ContextValue;
import com.realsight.westworld.tsp.lib.model.htm.context.CrossedContext;
import com.realsight.westworld.tsp.lib.util.Entry;
import com.realsight.westworld.tsp.lib.util.Triple;

public class NeuroGroupOperator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1721446875428083924L;
	
	private int maxLeftSemiContextsLenght;
	private int maxValidActiveNeuronsNum;
	private Map<Integer,List<ContextValue>>[] factsDics;
	private List<ContextValue>[] semiContextValuesLists;
	private Map<Integer,Integer>[] semiContextDics;
	private List<ContextValue>[] crossedSemiContextsLists;
	private List<CrossedContext> contextsValuesList;
	private int newContextID;
	private List<ActiveContext> activeContexts;
	private double numSelectedContext = 0;
	private List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> potentialNewContextList;
	private int numAddedContexts;
	private Double sumActiveContextsValue;
	private int right_size = 0;
	
	public int getMaxLeftSemiContextsLenght() {
		return maxLeftSemiContextsLenght;
	}

	public Map<Integer, List<ContextValue>>[] getFactsDics() {
		return factsDics;
	}

	public List<ContextValue>[] getSemiContextValuesLists() {
		return semiContextValuesLists;
	}

	public Map<Integer, Integer>[] getSemiContextDics() {
		return semiContextDics;
	}

	public List<ContextValue>[] getCrossedSemiContextsLists() {
		return crossedSemiContextsLists;
	}

	public List<CrossedContext> getContextsValuesList() {
		return contextsValuesList;
	}

	public int getNewContextID() {
		return newContextID;
	}
	
	public List<ActiveContext> getActiveContexts() {
		return activeContexts;
	}

	public double getNumSelectedContext() {
		return numSelectedContext;
	}

	public List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> getPotentialNewContextList() {
		return potentialNewContextList;
	}

	public int getNumAddedContexts() {
		return numAddedContexts;
	}
	
	
	public Double getSumActiveContextsValue() {
		return this.sumActiveContextsValue;
	}
	
	@SuppressWarnings("unchecked")
	public NeuroGroupOperator(int maxLeftSemiContextsLenght, 
			int maxValidActiveNeuronsNum) {
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
		this.maxValidActiveNeuronsNum = maxValidActiveNeuronsNum;
		this.newContextID = -1;
	}
	
	public int getNewContextList(List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> newContexts) {
		Set<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> newContextsSet = 
				new HashSet<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>>();
		newContextsSet.addAll(newContexts);
		return getNewContextList(newContextsSet);
	}
	
	public int getNewContextList(Set<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> newContexts) {
		int numAddedContexts = 0;
		for (Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>> activeContext : newContexts) {
			List<Integer> leftFacts = activeContext.getFirst().getFirst();
			List<Integer> rightFacts = activeContext.getFirst().getSecond();
			Collections.sort(leftFacts);
			Collections.sort(rightFacts);
			int leftHash = leftFacts.hashCode();
			int rightHash = rightFacts.hashCode();
			
			if(!this.semiContextDics[0].containsKey(leftHash)){
				numAddedContexts += 1;
				continue;
			}
			int leftSemiContextID = this.semiContextDics[0].get(leftHash);
			if(!this.semiContextDics[1].containsKey(rightHash)){
				numAddedContexts += 1;
				continue;
			}
			int rightSemiContextID = this.semiContextDics[1].get(rightHash);
			if(!this.semiContextValuesLists[0].get(leftSemiContextID).getContexIDs().containsKey(rightSemiContextID)){
				numAddedContexts += 1;
				continue;
			}
		}
		return numAddedContexts;
	}
	
	public int getContextByFacts(
			List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> newContextsList, 
			int zeroLevel){
		int numAddedContexts = 0;
		for(Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>> activeContext : newContextsList){
			List<Integer> leftFacts = activeContext.getFirst().getFirst();
			List<Integer> rightFacts = activeContext.getFirst().getSecond();
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
				rightSemiContextValues.setFacts(rightFacts);
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
//		    	System.out.println("new contextID = " + (contextID+(1<<30)));
//			    	System.out.print((contextID+(1<<30)) + " : ");
//			    	for(int ii = 0; ii < leftFacts.size(); ii++)
//						System.out.print(leftFacts.get(ii) + " ");
//					System.out.print("-> ");
//					for(int ii = 0; ii < rightFacts.size(); ii++)
//						System.out.print(rightFacts.get(ii) + " ");
//					System.out.print("\n");
		    	numAddedContexts += 1;
		    	Double T = activeContext.getSecond().getFirst() + 1;
		    	Double F = activeContext.getSecond().getSecond();
		    	Integer level = (int) Math.round(activeContext.getThird().getOrDefault("level", 0.0));
		    	CrossedContext crossedContexts = new CrossedContext(T, F, zeroLevel, level, leftHash, rightHash, rightFacts, activeContext.getThird());
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
		return numAddedContexts;
	}
	
	public void sleep() {
		this.crossedSemiContextsLists[0].clear();
		this.crossedSemiContextsLists[1].clear();
	}
	
	public void contextCrosser(int leftOrRight, 
			List<Integer> factsList, 
			boolean newContextFlag, 
			boolean learnFlag, 
			List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> potentialNewContexts) {
		
		if (leftOrRight == 1) {
			right_size = factsList.size();
		}
		if (leftOrRight == 0) {
			if ( potentialNewContexts.size() > 0 && learnFlag){
				this.numAddedContexts = this.getContextByFacts (potentialNewContexts, 0);
			} else if( potentialNewContexts.size() > 0 ) {
				this.numAddedContexts = this.getNewContextList (potentialNewContexts);
			} else {
				this.numAddedContexts = 0;
			}
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
	
	public int getLevelFromContextID(int contextID) {
		if (contextID < (1<<30)) return 0;
		if (contextID >= this.contextsValuesList.size()+(1<<30)) return 0;
		return this.contextsValuesList.get(contextID-(1<<30)).getLevel();
	}
	
	public int getActivedNumberFromContextID(int contextID) {
		if (contextID < (1<<30)) return 0;
		if (contextID >= this.contextsValuesList.size()+(1<<30)) return 0;
		return (int) this.contextsValuesList.get(contextID-(1<<30)).getTP();
	}
	
	public List<ActiveContext> getPotentialActiveContexts() {
		List<ActiveContext> activeContexts = new ArrayList<ActiveContext>();
		for ( int i = 0; i < this.crossedSemiContextsLists[0].size(); i++) {
			ContextValue leftSemiContextValues = this.crossedSemiContextsLists[0].get(i);
			for (Map.Entry<Integer, Integer> entry : leftSemiContextValues.getContexIDs().entrySet()) {
				int rightSemiContextID = entry.getKey();
				int contextID = entry.getValue();
				if ( this.newContextID != contextID ) {
					ContextValue rightSemiContextValue = this.semiContextValuesLists[1].get(rightSemiContextID);
					if ( leftSemiContextValues.getFromOrto().size() == leftSemiContextValues.getLenFact() ) {
						if (this.contextsValuesList.get(contextID).getTP() + this.contextsValuesList.get(contextID).getFP() < maxValidActiveNeuronsNum) continue;
						activeContexts.add(new ActiveContext(contextID, this.contextsValuesList.get(contextID).getNumActivate(), 
								this.contextsValuesList.get(contextID).getLeftHash(), this.contextsValuesList.get(contextID).getRightHash(), 
								leftSemiContextValues.getFromOrto(), rightSemiContextValue.getFacts()));
      				}
      			}
      		}
      	}
		return activeContexts;
	}
	
	public double activeLogLikelihood(List<Integer> currSensFacts) {
		double activeNum = 0.0;
		List<ActiveContext> activeContexts = getPotentialActiveContexts();
		for (ActiveContext activeContext : activeContexts) {
			int contextID = activeContext.getContextID();
			double numActive = this.contextsValuesList.get(contextID).getNumActivate();
			boolean flag = true;
			for (Integer fact : activeContext.getRightFacts()) {
				if (currSensFacts.contains(fact)) continue;
				flag = false;
				break;
			}
			if (flag)
				activeNum += numActive;
		}
		return activeNum;
	}
	
	public void updateContextsAndGetActive(boolean newContextFlag) {

        List<ActiveContext> activeContexts = new ArrayList<ActiveContext>();
//        List<Long> beActivatedList = new ArrayList<Long>();
        double numSelectedContext = 0;
        this.sumActiveContextsValue = 0.0;
        int sumSize = 0;
        List<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>> potentialNewContextList = 
        		new ArrayList<Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>>>();

        for ( int i = 0; i < this.crossedSemiContextsLists[0].size(); i++) {
        	ContextValue leftSemiContextValues = this.crossedSemiContextsLists[0].get(i);
        	for (Map.Entry<Integer, Integer> entry : leftSemiContextValues.getContexIDs().entrySet()) {
        		int rightSemiContextID = entry.getKey();
        		int contextID = entry.getValue();
//        		if (this.contextsValuesList.get(contextID).getNumActivate() < 0.1) continue;
//        		System.err.println(contextID + " " + this.contextsValuesList.get(contextID).getNumActivate());
//        		if (this.contextsValuesList.get(contextID).getNumActivate() <= 0) continue;
        		if ( this.newContextID != contextID ) {
        			ContextValue rightSemiContextValue = this.semiContextValuesLists[1].get(rightSemiContextID);
        			if ( leftSemiContextValues.getFromOrto().size() == leftSemiContextValues.getLenFact() ) {
        				numSelectedContext += 1;
//        				System.out.println(Math.log(this.contextsValuesList.get(contextID).getNumActivate()));
//        				this.sumSelectedContextValue += this.contextsValuesList.get(contextID).getTP();
//        				this.sumSelectedContextValue += this.contextsValuesList.get(contextID).getFP();
//        				System.out.println(this.contextsValuesList.get(contextID).getNumActivate());
        				if ( rightSemiContextValue.getFromOrto().size() > 0 ) {
        					if ( rightSemiContextValue.getFromOrto().size() == rightSemiContextValue.getLenFact() ) {
        						if(rightSemiContextValue.getFromOrto().size() >= right_size) {
        							sumSize += 1;
        							this.sumActiveContextsValue += (this.contextsValuesList.get(contextID).getNumActivate());
        						}
     							if (newContextFlag) this.contextsValuesList.get(contextID).addNumActivate();
        						activeContexts.add(new ActiveContext(contextID, this.contextsValuesList.get(contextID).getTP(), 
        								this.contextsValuesList.get(contextID).getLeftHash(), this.contextsValuesList.get(contextID).getRightHash()));
        					} else if ( this.contextsValuesList.get(contextID).getZeroLevel()==1 && 
        							leftSemiContextValues.getFromOrto().size()<=this.maxLeftSemiContextsLenght ){
        						Entry<List<Integer>, List<Integer>> p_1 = 
        								new Entry<List<Integer>, List<Integer>>(leftSemiContextValues.getFromOrto(), rightSemiContextValue.getFromOrto());
                				Entry<Double, Double> p_2 = 
                						new Entry<Double, Double>(this.contextsValuesList.get(contextID).getTP(), this.contextsValuesList.get(contextID).getFP());
                				Map<String, Double> p_3 = new HashMap<String, Double>();
                				p_3.putAll(this.contextsValuesList.get(contextID).getRewards());
                				Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>> p = 
                						new Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>> (p_1, p_2, p_3);
                				potentialNewContextList.add(p);
        					}
        				}
        				if ( newContextFlag && rightSemiContextValue.getFromOrto().size()!=rightSemiContextValue.getLenFact() ) {
        					this.contextsValuesList.get(contextID).subNumActivate();
        				}
        			} else if ( this.contextsValuesList.get(contextID).getZeroLevel()==1 && rightSemiContextValue.getFromOrto().size()>0 && 
        					leftSemiContextValues.getFromOrto().size()>0 && leftSemiContextValues.getFromOrto().size()<=this.maxLeftSemiContextsLenght ) {
        				Entry<List<Integer>, List<Integer>> p_1 = 
								new Entry<List<Integer>, List<Integer>>(leftSemiContextValues.getFromOrto(), rightSemiContextValue.getFromOrto());
        				Entry<Double, Double> p_2 = 
        						new Entry<Double, Double>(this.contextsValuesList.get(contextID).getTP(), this.contextsValuesList.get(contextID).getFP());
        				Map<String, Double> p_3 = new HashMap<String, Double>();
        				p_3.putAll(this.contextsValuesList.get(contextID).getRewards());
        				Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>> p = 
        						new Triple<Entry<List<Integer>, List<Integer>>, Entry<Double, Double>, Map<String, Double>> (p_1, p_2, p_3);
        				potentialNewContextList.add(p);
        			}
        		}
        	}
        }
        if (activeContexts.size() == 0) 
        	this.sumActiveContextsValue = Double.NEGATIVE_INFINITY;
        if (newContextFlag) this.newContextID = -1;
        this.activeContexts = activeContexts;
        this.numSelectedContext = numSelectedContext;
        this.potentialNewContextList = potentialNewContextList;
        if (sumSize > 0)
        	this.sumActiveContextsValue /= sumSize;
	}
}