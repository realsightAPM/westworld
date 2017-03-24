package com.realsight.brain.rca.app.viz;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.realsight.brain.rca.cmd.UploadLogDataCommand;

public class UploadLogData extends ServerResource {
	
	private static final Logger logger = LogManager.getLogger(UploadLogData.class.getName());
	
	@Get
    public synchronized String insertLogData() {
		String propertys = (String) getRequestAttributes().get("propertys");
		try {
			propertys = URLDecoder.decode(propertys, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		UploadLogDataCommand cmd = new UploadLogDataCommand(propertys);
		try {
			cmd.execute();
		} catch (SolrServerException | IOException | JSONException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		
        return "success";
    }
}