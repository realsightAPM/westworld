package com.realsight.brain.rca.app.viz;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.realsight.brain.rca.cmd.DeleteGroupCommand;

public class DeleteGroup extends ServerResource {
	
	private static final Logger logger = LogManager.getLogger(DeleteGroup.class.getName());
	
	@Get
    public synchronized String deleteGroup() {
		
		String group_name = (String) getRequestAttributes().get("group_name");
		try {
			group_name = URLDecoder.decode(group_name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		
        DeleteGroupCommand cmd = new DeleteGroupCommand(group_name);
        try {
			cmd.execute();
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
        
        return "success";
    }
}