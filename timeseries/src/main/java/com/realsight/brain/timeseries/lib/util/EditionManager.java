package com.realsight.brain.timeseries.lib.util;

public interface EditionManager <T>{
	public T getEdition();
	public void setEdition(T edition);
	public <T> void upgrade();
	public boolean equal(T edition);
}
