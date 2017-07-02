package com.realsight.westworld.tsp.lib.util;

public interface EditionManager <T>{
	public T getEdition();
	public void setEdition(T edition);
	public void upgrade();
	public boolean equal(T edition);
}
