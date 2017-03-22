package com.realsight.brain.rca.app;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.realsight.brain.rca.app.viz.UploadLogData;

public class RCALogApp extends Application{
		
	@Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/upload/{propertys}", UploadLogData.class);
        return router;
    }
}
