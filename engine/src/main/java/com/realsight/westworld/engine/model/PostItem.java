package com.realsight.westworld.engine.model;

public class PostItem {

	private String item;
	
	public PostItem() {
		
	}

	public PostItem(String item) {
//		super();
		this.item = item;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "PostItem [item=" + item + "]";
	}
	
	
}
