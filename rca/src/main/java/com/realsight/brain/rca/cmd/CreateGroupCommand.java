package com.realsight.brain.rca.cmd;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

import com.realsight.brain.rca.io.DataManager;
import com.realsight.brain.rca.struct.Group;

public class CreateGroupCommand implements VoidCommand {
	
	private static final Logger logger = LogManager.getLogger(CreateGroupCommand.class.getName());
	
	private final String group_name;
	private final String propertys;
	
	public CreateGroupCommand(String group_name, String propertys) {
		this.group_name = group_name;
		this.propertys = propertys;
	}
	
	@Override
	public void execute() throws SolrServerException, IOException {
		// TODO Auto-generated method stub
		if ( DataManager.getInstance().createGroup(group_name, propertys).equals("error")) {
			throw new FileNotFoundException("group create failed.");
		}
		if ( !Group.createGroup(group_name, propertys) ) {
			throw new NullPointerException("group create failed.");
		}
		logger.info(group_name+propertys);
	}

}
