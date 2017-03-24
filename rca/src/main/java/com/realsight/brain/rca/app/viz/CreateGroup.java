package com.realsight.brain.rca.app.viz;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.realsight.brain.rca.cmd.CreateGroupCommand;

public class CreateGroup extends ServerResource {
	
	private static final Logger logger = LogManager.getLogger(CreateGroup.class.getName());
	
	@Get
    public synchronized String createGroup() {
        
        String group_name = (String) getRequestAttributes().get("group_name");
        String propertys = (String) getRequestAttributes().get("propertys");
        try {
			group_name = URLDecoder.decode(group_name, "UTF-8");
			propertys = URLDecoder.decode(propertys, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
               
        CreateGroupCommand cmd = new CreateGroupCommand(group_name, propertys);
        try {
        	cmd.execute();
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return "error";
		}
        return "success";
    }
	
}