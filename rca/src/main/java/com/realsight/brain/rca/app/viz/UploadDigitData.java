package com.realsight.brain.rca.app.viz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.realsight.brain.rca.cmd.UploadGroupDataCommand;

public class UploadDigitData extends ServerResource {
		
	private static final Logger logger = LogManager.getLogger(UploadDigitData.class.getName());
	
	@Get
    public synchronized String uploadDigitData() {
		String group_name = (String) getRequestAttributes().get("group_name");
        String values = (String) getRequestAttributes().get("values");
        try {
			group_name = URLDecoder.decode(group_name, "UTF-8");
			values = URLDecoder.decode(values, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		UploadGroupDataCommand cmd = new UploadGroupDataCommand(group_name, values);
		try {
			cmd.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			return "error";
		}
        return "success";
    }
}
