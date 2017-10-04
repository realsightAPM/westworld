package com.realsight.westworld.bnanalysis.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import com.realsight.westworld.bnanalysis.basic.Pair;

public class WriteSolrCloud {

	public WriteSolrCloud() {}
	
	public void writeOneDoc(String zkHost, List<Pair<String, Object>> conf_list) {
		CloudSolrClient solr = new CloudSolrClient(zkHost);
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
		
		try {
			solr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("写入完成");
	}
	
	public void writeDocs(String zkHost, String collection, List<SolrOneDoc> docs) {
		CloudSolrClient solr = new CloudSolrClient(zkHost);
		solr.setDefaultCollection(collection);
        solr.setZkClientTimeout(20000);    
        solr.setZkConnectTimeout(1000);
//        solr.connect();
        
		Collection<SolrInputDocument> docsCollection = new ArrayList<SolrInputDocument>();
		for (int i = 0; i < docs.size(); i++) {
			SolrInputDocument doc = new SolrInputDocument();
			for (int j = 0; j < docs.get(i).conf_list.size(); j++) {
				doc.addField(docs.get(i).conf_list.get(j).first, docs.get(i).conf_list.get(j).second);
			}
			docsCollection.add(doc);
		}
		
		while (true) {
			try {
				solr.connect();
				solr.add(docsCollection);
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
		
		try {
			solr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("写入完成");
	}
	
}
