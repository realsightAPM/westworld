package com.realsight.brain.rca.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.service.CorsService;

import com.realsight.brain.rca.app.RCAGroupApp;
import com.realsight.brain.rca.app.RCALogApp;
import com.realsight.brain.rca.util.Settings;

import java.util.Arrays;
import java.util.HashSet;



public class RCAServer extends Application {
    private static final Logger logger = LogManager.getLogger(RCAServer.class.getName());
    private Component rca_server;
//    private Router router = null;

    RCAServer(int port) throws Exception {
    	rca_server = new Component();
    	rca_server.getContext().getParameters().set("maxThreads", "512");
    	rca_server.getContext().getParameters().set("lowThreads", "128");
    	rca_server.getServers().add(Protocol.HTTP, port);

        CorsService corsService = new CorsService();
        corsService.setAllowedOrigins(new HashSet<String>(Arrays.asList("/rca/*")));
        corsService.setAllowedCredentials(true);

        rca_server.getServices().add(corsService);
        rca_server.getDefaultHost().attach("/rca/group", new RCAGroupApp());
        rca_server.getDefaultHost().attach("/rca/log", new RCALogApp());
        logger.info("SCAServer create.");
    }

    public void start() throws Exception {
        rca_server.start();
    }

    public void stop() throws Exception {
    	rca_server.stop();
    }

    public static void main(String[] args) throws Exception {
        if (args.length!=1){
            System.err.println("Wrong Argument Length. You need to give one argument.");
        }
        String cmd = args[0];
        int port = Integer.parseInt(Settings.getInstance().get("rca_port"));
        RCAServer rca_server = new RCAServer(port);
        if (cmd.equals("start")) {
        	rca_server.start();
        }
        else if (cmd.equals("stop")) {
        	rca_server.stop();
        }
        else {
            System.err.println("Bad Argument. Argument must be start or stop.");
        }
    }
}
