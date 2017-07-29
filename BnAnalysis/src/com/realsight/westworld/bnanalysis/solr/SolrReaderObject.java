package com.realsight.westworld.bnanalysis.solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.common.SolrDocument;

import com.realsight.westworld.bnanalysis.Dao.Statistic;

public abstract class SolrReaderObject {

	public List<String> queryList;
	public HashMap<String, Integer> queryMap;
	public List<ArrayList<Double>> queryArray;
	public String[] attrList;
	
	public abstract void runRead(SolrDocument option, String time_field, long start, long end, Statistic stat, String... fq);
}
