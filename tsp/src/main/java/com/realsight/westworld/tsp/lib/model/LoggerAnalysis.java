package com.realsight.westworld.tsp.lib.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

import com.realsight.westworld.tsp.lib.model.hashcode.SOMHash;
import com.realsight.westworld.tsp.lib.model.hashcode.SpaceBiont;
import com.realsight.westworld.tsp.lib.util.Entry;

public class LoggerAnalysis {
	
	private Long time_win = -1L;
	private URLTree root = null;
	private Long start_timestamp = -1L;
	private Iterator<Entry<String, Entry<Entry<Double, Double>, Entry<Double, Double>>>> iter = null;
	private List<Entry<String, Entry<Entry<Double, Double>, Entry<Double, Double>>>> list = null;
	
	public LoggerAnalysis(Long time_win) throws Exception {
		this.time_win = time_win;
		this.root = new URLTree();
	}
	
	public LoggerAnalysis() throws Exception {
		this(1000L * 60 * 30);
	}
	
	private class URLTree {
		private long lazy_timestamp = -1L;
		private Map<String, URLTree> children = null;
		private Deque<Long> timestamps = null;
		private SOMHash elapsed_som = null;
		private SOMHash pv_som = null;
		private static final int neuron_num = 100;
		
		public URLTree() throws Exception {
			this.children = new HashMap<String, URLTree>();
			this.lazy_timestamp = -1L;
			this.timestamps = new LinkedList<Long>();
			this.elapsed_som = new SOMHash(SpaceBiont.CRAB, 1, neuron_num);
			this.pv_som = new SOMHash(SpaceBiont.CRAB, 1, neuron_num);
		}
		
		public URLTree getChild(String child_name) throws Exception {
			if (! children.containsKey(child_name))
				children.put(child_name, new URLTree());
			URLTree child = children.get(child_name);
			child.setLazy_timestamp(lazy_timestamp);
			return child;
		}
		
		public void setLazy_timestamp(long lazy_timestamp) {
			this.lazy_timestamp = lazy_timestamp;
			while(!this.timestamps.isEmpty()){
				Long timestamp = this.timestamps.peekFirst();
				if (this.lazy_timestamp < timestamp) {
					break;
				}
				this.timestamps.pollFirst();
			}
		}
		
		public Entry<Entry<Double, Double>, Entry<Double, Double>> add(Long elapsed, Long timestamp) {
			
			Long lazy_timestamp = timestamp - time_win;
			setLazy_timestamp(lazy_timestamp);
			Matrix elapsed_value = new Matrix(1, 1, elapsed);
			Matrix pv_value = new Matrix(1, 1, timestamps.size());
			this.timestamps.addLast(timestamp);
			double ff = elapsed;
			double fs = this.elapsed_som.matchingOptimalDistence(elapsed_value, true);
			double sf = timestamps.size();
			double ss = this.pv_som.matchingOptimalDistence(pv_value, true);
			Entry<Double, Double> f = new Entry<Double, Double>(ff, fs);
			Entry<Double, Double> s = new Entry<Double, Double>(sf, ss);
			return new Entry<Entry<Double, Double>, Entry<Double, Double>> (f,s);
		}
	}
	
	private void insert(String url, URLTree node, Iterator<String> sub_tree, Long elapsed, Long timestamp) {
		Entry<Entry<Double, Double>, Entry<Double, Double>> entry = node.add(elapsed, timestamp);
		if (!(url.equals("") || url.equals("/"))) {
			System.out.println(url + " : " + entry);
			this.list.add(new Entry<String, Entry<Entry<Double, Double>, Entry<Double, Double>>>(url, entry));
		}
		if (!sub_tree.hasNext())
			return ;
		try {
			String child_name = sub_tree.next();
			insert(url+"/"+child_name, node.getChild(child_name), sub_tree, elapsed, timestamp);
		} catch (Exception e) {	
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void analysis(String work_name, String split_regex, Long elapsed, Long timestamp) {
		if (this.start_timestamp == -1L) {
			this.start_timestamp = timestamp;
		}
		List<String> sub_names = new ArrayList<String>();
		for (String sub_name : work_name.split(split_regex)) {
			sub_names.add(sub_name);
		}
		this.list = new ArrayList<Entry<String, Entry<Entry<Double, Double>, Entry<Double, Double>>>>();
		insert("", root, sub_names.iterator(), elapsed, timestamp);
		this.iter = this.list.iterator();
	}
	
	public void analysis(String work_name, String split_regex, Long timestamp) {
		analysis(work_name, split_regex, 0L, timestamp);
	}

	public boolean hasNext() {
		// TODO Auto-generated method stub
		if (iter == null)
			return false;
		return iter.hasNext();
	}

	public Entry<String, Entry<Entry<Double, Double>, Entry<Double, Double>>> next() {
		// TODO Auto-generated method stub
		return iter.next();
	}
	
}
