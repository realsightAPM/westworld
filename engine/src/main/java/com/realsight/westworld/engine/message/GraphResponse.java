package com.realsight.westworld.engine.message;

public class GraphResponse {

	private String status;
	private Object vertices;
	private Object edges;
	
	public GraphResponse() {
		
	}

	public GraphResponse(String status, Object vertices, Object edges) {
		super();
		this.status = status;
		this.vertices = vertices;
		this.edges = edges;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getVertices() {
		return vertices;
	}

	public void setVertices(Object vertices) {
		this.vertices = vertices;
	}

	public Object getEdges() {
		return edges;
	}

	public void setEdges(Object edges) {
		this.edges = edges;
	}
	
	
}
