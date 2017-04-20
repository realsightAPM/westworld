package com.realsight.westworld.tsp.lib.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.realsight.westworld.tsp.lib.model.analysis.context.LoggerContext;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;

public class LoggerAnalysis {
	
	private static final Long time_win = 1000L * 60L * 30L;
	private Node root = null;
	private Long start_timestamp = -1L;
	private Long now_timestamp = -1L;
	
	private class Node {
		private long lazy_timestamp = -1L;
		private int total = 0;
		private Map<String, Node> children = null;
		private Deque<Entry<String>> infos = null;
		
		public Node() {
			this.children = new HashMap<String, Node>();
			this.total = 0;
			this.lazy_timestamp = -1L;
			this.infos = new LinkedList<Entry<String>>();
		}
		
		public Node getChild(String url_child) {
			if (! children.containsKey(url_child))
				children.put(url_child, new Node());
			Node child = children.get(url_child);
			child.setLazy_timestamp(lazy_timestamp);
			return child;
		}
		
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
				Long timestamp = this.infos.peekFirst().getInstant();
				if (this.lazy_timestamp < timestamp) {
					break;
				}
				this.infos.pollFirst();
			}
		}
		
		public void add(String info, Long timestamp) {
			this.total += 1;
			Long lazy_timestamp = timestamp - time_win;
			setLazy_timestamp(lazy_timestamp);
			this.infos.addLast(new Entry<String>(info, timestamp));
		}
		
		public List<String> getInfos() {
			List<String> t_infos = new ArrayList<String>();
			Iterator<Entry<String>> iter = this.infos.iterator();
			while(iter.hasNext()) {
				t_infos.add(iter.next().getItem());
			}
			return t_infos;
		}
		
		public Double getScore() {
			return 1.0*this.infos.size()/this.total;
		}
	}

	public LoggerAnalysis() {
		this.root = new Node();
	}
	
	
		
	private void insert(Node p, List<String> urls, String info, Long timestamp) {
		p.add(info, timestamp);
		if (urls.isEmpty())
			return ;
		String url_child = urls.get(0);
		urls.remove(0);
		Node child = p.getChild(url_child);
		insert(child, urls, info, timestamp);
	}
	
	public void insert(String url, String split_regex, String info, Long timestamp) {
		if (this.start_timestamp == -1L) {
			this.start_timestamp = timestamp;
		}
		
		this.now_timestamp = timestamp;
		String[] t_url = url.split(split_regex);
		List<String> urls = new ArrayList<String>();
		for (int i = 0; i < t_url.length; i++) {
			urls.add(t_url[i]);
		}
		insert(root, urls, info, timestamp);
	}
	
	private List<LoggerContext> getRank(Node p, String url, int min_number) {
		double num_hour = 1.0 * (this.now_timestamp-this.start_timestamp)/time_win + 1.0;
		List<LoggerContext> res = new ArrayList<LoggerContext>();
		if (p.getAppear() < min_number) return res;
		if (p.getScore()>0.0) res.add(new LoggerContext(
				p.getScore(), url, (int) ((p.getTotal()-p.getAppear())/num_hour), p.getAppear(), p.getInfos()));
		for (String url_child : p.getChildren().keySet()) {
			Node child = p.getChild(url_child);
			res.addAll(getRank(child, url+"/"+url_child, min_number));
		}
		return res;
	}
	
	public List<LoggerContext> getRank(int min_number) {
		List<LoggerContext> res = getRank(root, "root", min_number);
		Collections.sort(res);
		return res;
	}
	
	public static void main(String[] args) {
		LoggerAnalysis aa = new LoggerAnalysis();
		for (int i = 0; i < 1000; i++) {
			int j = i % 10;
			String url = "sb/" + j;
			aa.insert(url, "/", "" + j, i*10L);
		}
		List<LoggerContext> res = aa.getRank(2);
		for (LoggerContext lc : res) {
			System.out.println(lc.getUrl());
			System.out.println(lc.getAverage() + " " + lc.getAppear());
		}
	}
	
}
