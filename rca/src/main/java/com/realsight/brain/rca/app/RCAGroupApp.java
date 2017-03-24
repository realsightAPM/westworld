package com.realsight.brain.rca.app;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.realsight.brain.rca.app.viz.CreateGroup;
import com.realsight.brain.rca.app.viz.DeleteGroup;
import com.realsight.brain.rca.app.viz.UploadDigitData;

public class RCAGroupApp extends Application{
	
	@Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attach("/delete/{group_name}", DeleteGroup.class);
        router.attach("/create/{group_name}/{propertys}", CreateGroup.class);
        router.attach("/upload/{group_name}/{values}", UploadDigitData.class);
        return router;
    }
}
