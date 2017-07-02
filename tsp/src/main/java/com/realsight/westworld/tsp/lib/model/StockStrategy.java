package com.realsight.westworld.tsp.lib.model;

import java.io.Serializable;

import com.realsight.westworld.tsp.lib.model.htm.StrategyHierarchy;

import Jama.Matrix;

public abstract class StockStrategy implements Serializable{
	/**
	 * @author √»
	 */
	
	private static final long serialVersionUID = 7516360057898106017L;
	
	protected StrategyHierarchy HTM = null;
	
	public StockStrategy() {}
	
	public abstract String todayPrice(Matrix yestoday, Matrix today, Matrix tomorrow, Long timestamp);
}
