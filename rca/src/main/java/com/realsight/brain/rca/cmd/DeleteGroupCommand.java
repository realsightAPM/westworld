package com.realsight.brain.rca.cmd;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

import com.realsight.brain.rca.io.DataManager;
import com.realsight.brain.rca.struct.Group;

public class DeleteGroupCommand implements VoidCommand {
	private static final Logger logger = LogManager.getLogger(DeleteGroupCommand.class.getName());
	private final String group_name;
	
	public DeleteGroupCommand(String group_name) {
		this.group_name = group_name;
	}
	
	@Override
	public void execute() throws SolrServerException, IOException {
		// TODO Auto-generated method stub
		DataManager.getInstance().deleteGroup(group_name);
		Group.deleteGroup(group_name);
		logger.info(group_name);
	}

}
