package com.realsight.brain.rca.test.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realsight.brain.rca.db.AppDataManager;
import com.realsight.brain.rca.util.Settings;
import com.realsight.brain.rca.util.TimeUtil;

public class LogData implements Runnable{
	
	private static final Logger logger = LogManager.getLogger(LogData.class.getName());
	
	public LogData() {}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String jm_pre = Settings.getInstance().get("rca.appurl");
		String jm_uri = jm_pre + "monitoring?period=jour&counter=log&part=lastError&format=json";
		ClientResource jm_client = new ClientResource(jm_uri);
		String result = null;
		try {
			result = jm_client.get().getText().toString();
		} catch (ResourceException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new Gson();
		Map<String, ArrayList<HashMap<String, String>>> reuslt1 = gson.fromJson(result,
		        new TypeToken<Map<String, ArrayList<HashMap<String, String>>>>() {
		        }.getType());

		ArrayList<HashMap<String, String>> result2 = reuslt1.get("list");
		for (HashMap<String, String> res : result2) {
		    Long ts = Long.parseLong(res.get("time"));
		    String timestamp = TimeUtil.formatUnixtime(ts);
		    String message = res.get("message");
		    String http = res.get("httpRequest");
//		    String stack = res.get("stackTrace");
		    String message_type = null;
		    String message_class = null;
		    StringBuffer message_text = new StringBuffer();
		    
		    logger.info(timestamp + " " + http + " " + message);
		    
		    Scanner sin = new Scanner(message);
		    if ( sin.hasNext() ) message_type = sin.next();
		    if ( sin.hasNext() ) message_class = sin.next();
		    while ( sin.hasNext() )
		    	message_text.append(sin.next());
		    sin.close();
		    
		    String sql = String.format("INSERT IGNORE INTO "
		    		+ "log (ts,http,message_type,message_class,message_text) "
		    		+ "VALUES(%1$d,\"%2$s\",\"%3$s\",\"%4$s\",\"%5$s\"); ", 
		    		ts, http, message_type, message_class, message_text);
		    AppDataManager.getInstance().execute(sql);
		    
		}
	}
}