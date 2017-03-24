package com.realsight.brain.rca.cmd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.realsight.brain.rca.io.DataManager;
import com.realsight.brain.rca.struct.Group;

public class UploadGroupDataCommand implements VoidCommand {
	private static final Logger logger = LogManager.getLogger(UploadGroupDataCommand.class.getName());
	private final String values;
	private final String group_name;
	
	public UploadGroupDataCommand(String group_name, String values) {
		this.group_name = group_name;
		this.values = values;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		Double score = Group.uploadDigitData(group_name, values);
		DataManager.getInstance().uploadGroupData(group_name, values, score);
		logger.info(values);
	}

}
