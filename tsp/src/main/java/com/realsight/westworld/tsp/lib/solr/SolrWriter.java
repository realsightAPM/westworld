package com.realsight.westworld.tsp.lib.solr;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.util.Entry;
import com.realsight.westworld.tsp.lib.util.TimeUtil;

public class SolrWriter{
	
	private static final String TYPE_NAME_PREFIX = "class ";
	private final String SOLR_URL;
	private final HttpSolrClient SOLR_Client;
	private final Integer rows;
	private boolean status = false;
	Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	
	public String getSOLR_URL() {
		return SOLR_URL;
	}
	
	public SolrWriter(String SOLR_URL, Integer rows) {
		if (SOLR_URL == null) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}
		
		this.rows = rows;
		this.SOLR_URL = SOLR_URL;
		this.SOLR_Client = new HttpSolrClient.Builder(this.SOLR_URL).build();
		this.SOLR_Client.setParser(new XMLResponseParser());
		this.docs = new ArrayList<SolrInputDocument>();
		this.status = true;
	}
	
	public SolrWriter(String SOLR_URL) {
		this(SOLR_URL, 100);
	}
	
	public String getClassName(Type type) {
	    if (type==null) {
	        return "";
	    }
	    String className = type.toString();
	    if (className.startsWith(TYPE_NAME_PREFIX)) {
	        className = className.substring(TYPE_NAME_PREFIX.length());
	    }
	    return className;
	}

	public Class<?> getClass(Type type) 
	            throws ClassNotFoundException {
	    String className = getClassName(type);
	    if (className==null || className.isEmpty()) {
	        return null;
	    }
	    return Class.forName(className);
	}
	
	public void write(Object src, Type typeOfSrc) throws Exception {
		Gson gson = new Gson();
		JSONObject json = new JSONObject(gson.toJson(src, typeOfSrc));
		Class<?> clazz = getClass(typeOfSrc);
		Field[] fields = clazz.getDeclaredFields();
		SolrInputDocument document = new SolrInputDocument();
		Calendar cal = Calendar.getInstance();
		document.addField("_id", cal.getTimeInMillis());
		for (Field field : fields) {
			String name = field.getName();
			if (json.isNull(name)) continue;
			if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
				document.addField(name+"_i", json.optInt(name));
			} else if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
				document.addField(name+"_l", json.optLong(name));
			} else if (field.getType().equals(Float.class) || field.getType().equals(float.class)) {
				document.addField(name+"_f", json.optDouble(name));
			} else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
				document.addField(name+"_d", json.optDouble(name));
			} else {
				document.addField(name+"_s", json.optString(name));
			}
		}
		docs.add(document);
		if(docs.size() >= this.rows){
			flush();
		}
	}
	
	public void write(@SuppressWarnings("unchecked") Entry<String, ?> ... entrys) throws Exception {
		SolrInputDocument document = new SolrInputDocument();
		for (Entry<String, ?> entry : entrys) {
			document.addField(entry.getFirst(), entry.getSecond());
		}
		docs.add(document);
		if(docs.size() >= this.rows){
			flush();
		}
	}
	
	public void write(List<Entry<String, ?>> entrys) throws Exception {
		@SuppressWarnings("unchecked")
		Entry<String, ?>[] entry = (Entry<String, ?>[]) new Entry<?, ?>[entrys.size()];
		for (int i = 0; i < entrys.size(); i++) {
			entry[i] = entrys.get(i);
		}
		write(entry);
	}
	
	public void write(MultipleDoubleSeries mSeries) throws Exception {
		List<String> names = mSeries.getProperty_list();
		for (int i = 0; i < mSeries.size(); i++) {
			@SuppressWarnings({ "unchecked" })
			Entry<String, ?>[] entrys = (Entry<String, ?>[]) new Entry<?, ?>[names.size()+3];
			entrys[0] = new Entry<String, String>("series_name_s", mSeries.getName());
			entrys[1] = new Entry<String, Long>("timestamp_l", mSeries.get(i).getInstant());
			entrys[2] = new Entry<String, String>("timestamp_s", TimeUtil.formatUnixtime2(mSeries.get(i).getInstant()));
			for (String name : names) {
				Entry<String, Double> entry = new Entry<String, Double>(name+"_d", mSeries.getValue(name, i));
				entrys[names.indexOf(name)+3] = entry;
			}
			write(entrys);
		}
	}
	
	public void flush() throws SolrServerException, IOException {
		if (! docs.isEmpty() && this.status) {
			this.SOLR_Client.add(docs);
			this.SOLR_Client.commit();
			this.docs.clear();
		}
	}

	public void close() throws IOException, SolrServerException {
		// TODO Auto-generated method stub
		this.flush();
		this.SOLR_Client.close();
		this.status = false;
	}
	
}
