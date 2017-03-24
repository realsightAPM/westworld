package com.realsight.brain.rca.cmd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.realsight.brain.rca.io.DataManager;
import com.realsight.brain.rca.struct.Tree;

public class UploadLogDataCommand implements VoidCommand {
	private static final Logger logger = LogManager.getLogger(UploadLogDataCommand.class.getName());
	private final String propertys;
	
	public UploadLogDataCommand(String propertys) {
//		System.out.println(propertys);
		this.propertys = propertys;
	}
	
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		DataManager.getInstance().uploadLogData(propertys);
		JSONObject propertysJSON = new JSONObject(propertys);
		Long timestamp = Long.parseLong(propertysJSON.optString("timestamp").trim());
		Tree.getInstance().insert(propertysJSON.optString("url"), propertysJSON, timestamp);
		JSONArray RCARank = Tree.getInstance().getCorrelation(propertysJSON.optString("url"));
		DataManager.getInstance().uploadRCARankData(timestamp, RCARank);
		logger.info(propertys);
	}

}
