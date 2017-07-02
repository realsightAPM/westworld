package com.mucfc;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class Test {

//	private static Logger logger = Logger.getLogger(Test.class);
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // System.out.println("This is println message.");  

    	Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    	logger.setLevel(Level.WARN);
        // 记录debug级别的信息  
        logger.debug("This is debug message.");  
        // 记录info级别的信息  
        logger.info("This is info message.");  
        // 记录error级别的信息  
        logger.error("This is error message.");  
    }  
}
