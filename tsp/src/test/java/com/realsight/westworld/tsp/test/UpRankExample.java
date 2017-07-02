package com.realsight.westworld.tsp.test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.realsight.westworld.tsp.lib.solr.SolrReader;
import com.realsight.westworld.tsp.lib.text.TextWriter;

public class UpRankExample {
	public void read() {
		Path path = Paths.get(System.getProperty("user.dir"), "data", "in.txt");
		TextWriter tw = new TextWriter(path.toString());
		
		SolrReader sr = new SolrReader("http://localhost:8080/solr/stock", "timestamp_l:1493913600000");
		sr.setSort("up_d", false);
		while(sr.hasNextResponse()) {
			String str = sr.nextResponse();
			try {
				tw.write(str+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		tw.close();
	}
	
	public static void main(String[] args) throws Exception {
		(new UpRankExample()).read();
	}
}
