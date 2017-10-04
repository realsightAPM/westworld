package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class SolrCloudTest {
	
	private static Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	
	static {
		root.setLevel(Level.WARN);
	}
	
	public static void main(String[] args) {
		SolrOneDoc results = new SolrOneDoc();
		int cnt = 0;
		while (true) {
			try {
				results.metricbeatOption();
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("count: " + (++cnt));
		}
	}
	
}
