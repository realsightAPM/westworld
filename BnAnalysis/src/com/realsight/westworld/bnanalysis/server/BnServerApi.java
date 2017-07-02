package com.realsight.westworld.bnanalysis.server;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;

import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.service.NeticaApi;
import com.realsight.westworld.bnanalysis.solr.SolrResults;

public class BnServerApi implements Runnable{
	
	public String csv_file;
	public String solr_url;
	public List<String> queryList;
	public NeticaApi netica;
	
	private BnServerApi() {};
	
	public BnServerApi(String csv_file, List<String> queryList, String solr_url) {
		this.csv_file = csv_file;
		this.queryList = queryList;
		this.solr_url = solr_url;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			writeSolr(buildNet(csv_file, netica));
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static synchronized Pair<List<String>, List<String>> buildNet(String csv_file, NeticaApi netica) throws Exception {
		
		netica = new NeticaApi();
		netica.buildNet(csv_file, 2, 3);
		Pair<List<String>, List<String>> pair = new Pair<List<String>, List<String>> (netica.getGONodes(), netica.getGoLinks());
		netica.finalize();
		return pair;
	}
	
	private void writeSolr(Pair<List<String>, List<String>> pair) throws SolrServerException, IOException {
		SolrResults resulter = new SolrResults(solr_url);
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "bn_name_s"));
		resulter.addResult(new Pair<String, Object> ("nodes_s", pair.first.toString()));
		resulter.addResult(new Pair<String, Object> ("edges_s", pair.second.toString()));
		String str = "";
		for (int i = 0; i < queryList.size(); i++) {
			str += "^"+queryList.get(i);
		}
		resulter.addResult(new Pair<String, Object> ("query_list_s", str.substring(1)));
		resulter.write();
	}
	
	public static void main(String[] args) {
		
	}

}
