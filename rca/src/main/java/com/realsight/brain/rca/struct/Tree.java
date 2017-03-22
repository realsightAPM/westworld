package com.realsight.brain.rca.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tree {
	
	private static Tree instance = null;
	private static final Long time_win = 1000L * 60L * 30L;
	private Node root = null;
	private Long start_timestamp = -1L;
	private Long now_timestamp = -1L;
	
	private class Node {
		private long lazy_timestamp = -1L;
		private int total = 0;
		private Map<String, Node> children = null;
		private Deque<JSONObject> infos = null;
		
		
		public Node() {
			this.children = new HashMap<String, Node>();
			this.total = 0;
			this.lazy_timestamp = -1L;
			this.infos = new LinkedList<JSONObject>();
		}
		
		public Node getChild(String url_child) {
			if (! children.containsKey(url_child))
				children.put(url_child, new Node());
			Node child = children.get(url_child);
			child.setLazy_timestamp(lazy_timestamp);
			return child;
		}
		
		@SuppressWarnings("unused")
		public Map<String, Node> getChildren() {
			return this.children;
		}
		
		public int getTotal() {
			return this.total;
		}
		
		public int getAppear() {
			return this.infos.size();
		}
		
		public void setLazy_timestamp(long lazy_timestamp) {
			this.lazy_timestamp = lazy_timestamp;
			while(!this.infos.isEmpty()){
				Long timestamp = this.infos.peekFirst().optLong("timestamp");
				if (this.lazy_timestamp < timestamp) {
					break;
				}
				this.infos.pollFirst();
			}
		}
		
		public void add(JSONObject info) {
			this.total += 1;
			Long timestamp = info.optLong("timestamp");
			Long lazy_timestamp = timestamp - time_win;
			setLazy_timestamp(lazy_timestamp);
			this.infos.push(info);
		}
		
		public JSONArray getInfos() {
			JSONArray t_infos = new JSONArray();
			Iterator<JSONObject> iter = this.infos.iterator();
			int sz = 0;
			while(iter.hasNext()) {
				t_infos.put(iter.next());
				sz += 1;
				if (sz > 3) break;
			}
			return t_infos;
		}
		
		public Double getRcaScore() {
			return 1.0*this.infos.size()/this.total;
		}
	}

	private Tree() {
		this.root = new Node();
	}
	
	private static synchronized void syncInit() {
		if (instance == null) {
			instance = new Tree();
		}
	}

	public static Tree getInstance() {
		if (instance == null) {
			syncInit();
		}
		return instance;
	}
	
	private class Triple implements Comparable<Triple>{
		private Double score = 0.0;
		private String url = null;
		private JSONArray infos = null;
		private int total = 0;
		private int appear = 0;
		
		public Double getScore() {
			return score;
		}

		public String getUrl() {
			return url;
		}

		public JSONArray getInfos() {
			return infos;
		}
		
		public int getTotal() {
			return total;
		}
		
		public int getAppear() {
			return appear;
		}

		public Triple(Double score, String url, int total, int appear, JSONArray infos) {
			this.score = score;
			this.url = url;
			this.infos = infos;
			this.total = total;
			this.appear = appear;
		}

		@Override
		public int compareTo(Triple t) {
			// TODO Auto-generated method stub
			if (this.getScore() > t.getScore())
				return -1;
			if (this.getScore() < t.getScore())
				return 1;
			return 0;
		}
	}
		
	private void insert(Node p, String url, JSONObject info) {
		p.add(info);
		if (url == null)
			return ;
		int index = url.indexOf("/");
		if (index == -1) {
			Node child = p.getChild(url);
			insert(child, null, info);
			return ;
		}
		String url_child = url.substring(0, index);
		Node child = p.getChild(url_child);
		insert(child, url.substring(index+1), info);
	}
	
	public void insert(String url, JSONObject info, Long timestamp) {
		if (this.start_timestamp == -1L) {
			this.start_timestamp = timestamp;
		}
		this.now_timestamp = timestamp;
		insert(root, url, info);
	}
	
	private List<Triple> getCorrelation(Node p, String url, String rcaurl) {
		List<Triple> res = new ArrayList<Triple>();
		if (p.getRcaScore()>0.0 && !rcaurl.isEmpty() && p.getTotal()>10) res.add(new Triple(
				p.getRcaScore(), rcaurl, p.getTotal(), p.getAppear(), p.getInfos()));
		if (url == null)
			return res;
		int index = url.indexOf("/");
		if (index == -1) {
			Node child = p.getChild(url);
			res.addAll(getCorrelation(child, null, rcaurl+"/"+url));
			return res;
		}
		String url_child = url.substring(0, index);
		Node child = p.getChild(url_child);
		res.addAll(getCorrelation(child, url.substring(index+1), rcaurl+"/"+url_child));
		return res;
	}
	
	public JSONArray getCorrelation(String url) throws JSONException {
		double num_hour = 1.0 * (this.now_timestamp-this.start_timestamp)/time_win;
		JSONArray res = new JSONArray();
		List<Triple> correlationList = getCorrelation(root, url, "");
		Collections.sort(correlationList);
		for (Triple c : correlationList) {
			JSONObject t = new JSONObject();
			t.put("url_s", c.getUrl());
			t.put("score_f", c.getScore());
			t.put("infos_s", c.getInfos());
			t.put("appear_f", c.getAppear());
			t.put("total_f", (c.getTotal()-c.getAppear())/num_hour);
			res.put(t);
		}
		return res;
	}
	
}
