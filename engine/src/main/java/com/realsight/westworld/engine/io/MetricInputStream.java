package com.realsight.westworld.engine.io;

import com.realsight.westworld.engine.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jiajia on 17/3/27.
 */
public class MetricInputStream {
    public static MetricInputStream mis;
    public static SolrClient solr;

    public static MetricInputStream getInstance(){
        if (mis == null){
            mis = new MetricInputStream();
            String solrString = "http://10.4.55.171:8983/solr/test";
            solr = new HttpSolrClient.Builder(solrString).build();
        }
        return mis;
    }

    public Iterator<SolrDocument> input() {
        String timestamp_field = "rs_timestamp";
        String metrics = "rs_timestamp,rs_timestamp_l" + "," + "cpu_f";
        Long ts_end = System.currentTimeMillis();
        Long ts_start = ts_end - 10000 * 1000;
        Iterator iter = mis.query(timestamp_field, TimeUtil.formatUnixtime(ts_start), TimeUtil.formatUnixtime(ts_end), metrics);
        return iter;
    }

    public Iterator<SolrDocument> query(String tsfield, String start, String end, String metrics){
        ArrayList<SolrDocument> result = new ArrayList<SolrDocument>();
        SolrQuery query = new SolrQuery();
        String queryString = "*:*";
        query.setQuery(queryString);
        String filter_queries = String.format("%1$s:[%2$s TO %3$s]", tsfield, start, end);
        //"rs_timestamp:[2017-02-17T02:19:15.000Z TO 2017-02-17T02:34:15.000Z]"
        query.setFilterQueries(filter_queries);
        query.setFilterQueries("cpu_f:[* TO *]");
        query.setFields(StringUtils.split(metrics));
        query.setRows(200);

        try {
            QueryResponse response = solr.query(query);
            SolrDocumentList doc_list = response.getResults();
            for(SolrDocument doc: doc_list){
                result.add(doc);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result.iterator();
    }


    public static void main(String[] args) {
        MetricInputStream mis = MetricInputStream.getInstance();
        Iterator<SolrDocument> i = mis.input();
        while(i.hasNext()){
            SolrDocument sd = i.next();
            System.out.println(sd);
            String ts = String.valueOf(sd.get("rs_timestmap_l"));
            System.out.println(ts);
        }
    }
}
