package com.realsight.westworld.engine.message;

import java.util.List;

public class ResponseList {

	private String status;
	private List<Object> data;
	
	public ResponseList() {
		
	}

	public ResponseList(String status, List<Object> data) {
//		super();
		this.status = status;
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}
}
