package com.realsight.westworld.engine.util;

import java.io.FileInputStream;
import java.nio.file.Path;
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
        	Path path = Paths.get(System.getProperty("user.dir"));
        	Path dir = path.getParent();
            settings = new Settings();
            settings.init(Paths.get(dir.toString(), "conf", "brain.settings").toString());
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
