package com.realsight.westworld.tsp.lib.util;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author shizifan
 * @date 16/8/2
 */


public class Settings {
    private static Settings settings;
    private Properties props = null;

    private Settings(){}

    public static Settings getInstance(){
        if (settings == null){
            settings = new Settings();
            settings.init(Paths.get(
            		Paths.get(System.getProperty("user.dir")).getParent().toString(), 
            		"conf",
            		"brain.settings"
            	).toString());
        }
        return settings;
    }

    private void init(String conf_path) {
        props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(conf_path);
            props.load(fis);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String get(String key){
        return props.getProperty(key);
    }

}
