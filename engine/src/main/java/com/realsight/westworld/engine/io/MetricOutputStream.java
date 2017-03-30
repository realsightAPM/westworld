package com.realsight.westworld.engine.io;

import com.realsight.westworld.engine.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by jiajia on 17/3/27.
 */
public class MetricOutputStream {
    public static MetricOutputStream mos;
    public static SolrClient solr;

    public static MetricOutputStream getInstance(){
        if (mos == null){
            mos = new MetricOutputStream();
            String solrString = "http://10.4.55.171:8983/solr/test";
            solr = new HttpSolrClient.Builder(solrString).build();
        }
        return mos;
    }

    public void output(Collection<SolrInputDocument> outputs) {
        try {
            solr.add(outputs);
            solr.commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
