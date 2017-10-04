package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import com.realsight.westworld.bnanalysis.basic.Pair;

public class WriteSolr {
	
	public WriteSolr() {

	}

	public void writeOneDoc(String solr_url, List<Pair<String, Object>> conf_list) throws SolrServerException, IOException {
//		String solrStr = "http://localhost:8080/solr/apm3";
		SolrClient solr = new HttpSolrClient.Builder(solr_url).build();
//		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		SolrInputDocument doc = new SolrInputDocument();
		for (int i = 0; i < conf_list.size(); i++) {
			doc.addField(conf_list.get(i).first, conf_list.get(i).second);
		}  
		
		while (true) {
			try {
				solr.add(doc);
				solr.commit();
				break;
			} catch (Exception e) {
				System.out.println("网络commit异常");
				e.printStackTrace();
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		solr.close();
		System.out.println("写入完成");
	}
	
	public void writeDocs(List<SolrOneDoc> docs) {
		
	}
	
	
	public static void main(String[] args) throws SolrServerException, IOException {
		
		WriteSolr wr = new WriteSolr();
	}
}
