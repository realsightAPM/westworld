package com.realsight.brain.rca.io.solr;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class GroupBean {
	@Field
	private String id;
	@Field
	private String type;
	@Field
	private String name;
	@Field("links")
	private List<String> property_list;
	
	public GroupBean(){}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getProperty_list() {
		return property_list;
	}
	public void setProperty_list(List<String> property_list) {
		this.property_list = property_list;
	}
	
}
