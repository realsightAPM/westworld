package com.realsight.westworld.tsp.lib.model.reinforcement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.realsight.westworld.tsp.lib.model.htm.context.ActiveContext;
import com.realsight.westworld.tsp.lib.model.htm.context.CrossedContext;

public class SARSA {
	
	private double gamma = 1.0;
	private double alpha = 0.05;
	private double epsilon = 0.00;
	private Random rng = new Random();
	private List<String> actions = null;
	
	public SARSA(List<String> actions) {
		this.actions = actions;
	}
	
	public void learnQ(List<ActiveContext> prevActiveContexts,
			List<CrossedContext> contextsValuesList,
			String prevAction, 
			Double reward, 
			Double value) {
		for (ActiveContext prevActive : prevActiveContexts) {
			Integer prevContextID = prevActive.getContextID();
			double oldV = contextsValuesList.get(prevContextID).getRewards().getOrDefault(prevAction, 0.0);
			contextsValuesList.get(prevContextID).getRewards().put(prevAction, oldV+alpha*(value-oldV));
//			System.out.println(prevContextID + " " + oldV);
		}
	}
	
	public String chooseAction(List<ActiveContext> activeContexts,
			List<CrossedContext> contextsValuesList) {
		if (rng.nextDouble()<epsilon || activeContexts==null){
			return this.actions.get(rng.nextInt(this.actions.size()));
		}
		List<String> nextActions = new ArrayList<String>();
		Double mx = Double.NEGATIVE_INFINITY;
		for (String action : this.actions) {
			Double mn = Double.NEGATIVE_INFINITY;
			for (ActiveContext active : activeContexts) {
				Integer contextID = active.getContextID();
				Double V = contextsValuesList.get(contextID).getRewards().getOrDefault(action, 0.0);
//				System.out.println(action + " " + contextID + " " + V);
				mn = Math.max(mn, V);
			}
			if (mx < mn) {
				nextActions = new ArrayList<String>();
				nextActions.add(action);
				mx = mn;
			} else if (mx.equals(mn)) {
				nextActions.add(action);
			}
		}
//		if (mx < 0) return "b";
//		System.err.println(actionNames);
		if (nextActions.isEmpty())
			return null;
		return nextActions.get(rng.nextInt(nextActions.size()));
	}
	
	public void sarsa(String prevAction,
			String currAction,
			List<ActiveContext> prevActives,
			List<CrossedContext> contextsValuesList,
			Double reward) {
		if (prevActives==null || contextsValuesList==null)
			return ;
//		System.err.println(prevActives);
		for (ActiveContext prevActive : prevActives) {
			Integer contextID = prevActive.getContextID();
			Double qNext = contextsValuesList.get(contextID).getRewards().getOrDefault(currAction, 0.0);
			learnQ(prevActives, contextsValuesList, prevAction, reward, reward+gamma*qNext);
		}
	}
}
