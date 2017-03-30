package com.realsight.westworld.tsp.lib.model.htm.neurongroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.realsight.westworld.tsp.lib.model.htm.context.ActiveContext;
import com.realsight.westworld.tsp.lib.model.htm.context.ContextValue;
import com.realsight.westworld.tsp.lib.model.htm.context.CrossedContext;
import com.realsight.westworld.tsp.lib.util.Pair;

public class NeuroGroupOperator implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1721446875428083924L;
	
	private int maxLeftSemiContextsLenght;
	private Map<Integer,List<ContextValue>>[] factsDics;
	private List<ContextValue>[] semiContextValuesLists;
	private Map<Integer,Integer>[] semiContextDics;
	private List<ContextValue>[] crossedSemiContextsLists;
	private List<CrossedContext> contextsValuesList;
	private int newContextID;
	private List<ActiveContext> activeContexts;
	private double numSelectedContext = 0;
	private List<Pair<List<Integer>, List<Integer>>> potentialNewContextList;
	private int numAddedContexts;
//	private List<Long> beActivatedList;
	private Long timestamp;
	private Map<Long, Integer> history;
	private Double rate;
	private Double Rmax;
	private Double sumActiveContextsValue;
	private Double selectedContextValue;
	
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

	public Long getTimestamp() {
		return timestamp;
	}

	public Map<Long, Integer> getHistory() {
		return history;
	}

	public Double getRate() {
		return rate;
	}

	public Double getRmax() {
		return Rmax;
	}

	public void setMaxLeftSemiContextsLenght(int maxLeftSemiContextsLenght) {
		this.maxLeftSemiContextsLenght = maxLeftSemiContextsLenght;
	}

	public void setFactsDics(Map<Integer, List<ContextValue>>[] factsDics) {
		this.factsDics = factsDics;
	}

	public void setSemiContextValuesLists(List<ContextValue>[] semiContextValuesLists) {
		this.semiContextValuesLists = semiContextValuesLists;
	}

	public void setSemiContextDics(Map<Integer, Integer>[] semiContextDics) {
		this.semiContextDics = semiContextDics;
	}

	public void setCrossedSemiContextsLists(List<ContextValue>[] crossedSemiContextsLists) {
		this.crossedSemiContextsLists = crossedSemiContextsLists;
	}

	public void setContextsValuesList(List<CrossedContext> contextsValuesList) {
		this.contextsValuesList = contextsValuesList;
	}

	public void setNewContextID(int newContextID) {
		this.newContextID = newContextID;
	}

	public void setActiveContexts(List<ActiveContext> activeContexts) {
		this.activeContexts = activeContexts;
	}

	public void setNumSelectedContext(double numSelectedContext) {
		this.numSelectedContext = numSelectedContext;
	}

	public void setPotentialNewContextList(List<Pair<List<Integer>, List<Integer>>> potentialNewContextList) {
		this.potentialNewContextList = potentialNewContextList;
	}

	public void setNumAddedContexts(int numAddedContexts) {
		this.numAddedContexts = numAddedContexts;
	}

	public void setHistory(Map<Long, Integer> history) {
		this.history = history;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public void setRmax(Double rmax) {
		Rmax = rmax;
	}

	public void setSumActiveContextsValue(Double sumActiveContextsValue) {
		this.sumActiveContextsValue = sumActiveContextsValue;
	}

	public void setSelectedContextValue(Double selectedContextValue) {
		this.selectedContextValue = selectedContextValue;
	}

	public List<ActiveContext> getActiveContexts() {
		return activeContexts;
	}

	public double getNumSelectedContext() {
		return numSelectedContext;
	}

	public List<Pair<List<Integer>, List<Integer>>> getPotentialNewContextList() {
		return potentialNewContextList;
	}

	public int getNumAddedContexts() {
		return numAddedContexts;
	}
	
	
	public Double getSumActiveContextsValue() {
		return this.sumActiveContextsValue;
	}
	
	public Double getSelectedContextValue() {
		return this.selectedContextValue;
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
		this.rate = 0.9;
		this.Rmax = 1.0;
	}
	
	public int getNewContextList(Set<Pair<List<Integer>, List<Integer>>> newContexts) {
		int numAddedContexts = 0;
		for (Pair<List<Integer>, List<Integer>> activeContext : newContexts) {
			List<Integer> leftFacts = activeContext.getA();
			List<Integer> rightFacts = activeContext.getB();
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
	
	public int getContextByFacts(List<Pair<List<Integer>, List<Integer>>> newContextsList, int zeroLevel){
		int numAddedContexts = 0;
		for(Pair<List<Integer>, List<Integer>> activeContext : newContextsList){
			List<Integer> leftFacts = activeContext.getA();
			List<Integer> rightFacts = activeContext.getB();
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
		    	CrossedContext crossedContexts = new CrossedContext(Rmax/2.0, zeroLevel, leftHash, rightHash);
		    	this.contextsValuesList.add(crossedContexts);
		    	if (zeroLevel > 0) {
//		    		System.out.println("SBSBSB+ " + contextID);
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
	
	
	public void contextCrosser(int leftOrRight, List<Integer> factsList, boolean newContextFlag, boolean learnFlag, List<Pair<List<Integer>, List<Integer>>> potentialNewContexts) {
		if (leftOrRight == 0) {
			if ( potentialNewContexts.size() > 0 && learnFlag)
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
//        List<Long> beActivatedList = new ArrayList<Long>();
        double numSelectedContext = 0;
        this.selectedContextValue = 0.0;
        this.sumActiveContextsValue = 0.0;
        List<Pair<List<Integer>, List<Integer>>> potentialNewContextList = new ArrayList<Pair<List<Integer>, List<Integer>>>();

        for ( int i = 0; i < this.crossedSemiContextsLists[0].size(); i++) {
        	ContextValue leftSemiContextValues = this.crossedSemiContextsLists[0].get(i);
        	for (Map.Entry<Integer, Integer> entry : leftSemiContextValues.getContexIDs().entrySet()) {
        		int rightSemiContextID = entry.getKey();
        		int contextID = entry.getValue();
//        		System.out.println(this.contextsValuesList.get(contextID).getNumActivate());
//        		if (this.contextsValuesList.get(contextID).getNumActivate() <= 0) continue;
        		if ( this.newContextID != contextID ) {
        			ContextValue rightSemiContextValue = this.semiContextValuesLists[1].get(rightSemiContextID);
        			if ( leftSemiContextValues.getFromOrto().size() == leftSemiContextValues.getLenFact() ) {
        				numSelectedContext += 1;
        				this.sumActiveContextsValue += this.contextsValuesList.get(contextID).getNumActivate();
        				if ( rightSemiContextValue.getFromOrto().size() > 0 ) {
        					if ( rightSemiContextValue.getFromOrto().size() == rightSemiContextValue.getLenFact() ) {
        						double tmp = this.contextsValuesList.get(contextID).getNumActivate();
        						this.selectedContextValue += tmp;
        						tmp += Rmax/2.0;
//        						tmp = tmp*rate + (1.0-rate)*Rmax;
        						if (newContextFlag) this.contextsValuesList.get(contextID).setNumActivate(tmp);
        						activeContexts.add(new ActiveContext(contextID, this.contextsValuesList.get(contextID).getNumActivate(), 
        								this.contextsValuesList.get(contextID).getLeftHash(), this.contextsValuesList.get(contextID).getRightHash()));
        					} else if ( newContextFlag && this.contextsValuesList.get(contextID).getZeroLevel()==1 && 
        							leftSemiContextValues.getFromOrto().size()<= this.maxLeftSemiContextsLenght ){
        						Pair<List<Integer>, List<Integer>> p = 
        								new Pair<List<Integer>, List<Integer>>(leftSemiContextValues.getFromOrto(), rightSemiContextValue.getFromOrto());
        						potentialNewContextList.add(p);
        					}
        				} 
        				if ( newContextFlag && rightSemiContextValue.getFromOrto().size()!=rightSemiContextValue.getLenFact() ) {
        					double tmp = this.contextsValuesList.get(contextID).getNumActivate();
    						tmp = tmp*rate;
        					this.contextsValuesList.get(contextID).setNumActivate(tmp);
        				}
        					
        			} else if ( newContextFlag && this.contextsValuesList.get(contextID).getZeroLevel()==1 && rightSemiContextValue.getFromOrto().size()>0 && 
							 leftSemiContextValues.getFromOrto().size()<=this.maxLeftSemiContextsLenght ) {
        				Pair<List<Integer>, List<Integer>> p = 
								new Pair<List<Integer>, List<Integer>>(leftSemiContextValues.getFromOrto(), rightSemiContextValue.getFromOrto());
						potentialNewContextList.add(p);
        			}
        			if ( newContextFlag && leftSemiContextValues.getFromOrto().size()!=leftSemiContextValues.getLenFact() ) {
        				double tmp = this.contextsValuesList.get(contextID).getNumActivate();
						tmp = tmp*rate;
    					this.contextsValuesList.get(contextID).setNumActivate(tmp);
        			}
        		}
        	}
        }
        
        if (newContextFlag) this.newContextID = -1;
        this.activeContexts = activeContexts;
        this.numSelectedContext = numSelectedContext;
        this.potentialNewContextList = potentialNewContextList;
	}
}

