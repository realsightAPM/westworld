package apm.main;

import apm.http.DataSpider;
import apm.webstress.Stress;

public class Main {

	public static void main(String[] args) {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");

		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");

		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "error");

		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");  
		  
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "false");  
		  
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		DataSpider spider = new DataSpider();
		
		spider.start();
		Stress stress = new Stress();
		try {
			stress.run();
		}catch (Exception e) {
			e.printStackTrace();
		}
	
	/*	int time = 1000*60*60*3;
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			System.exit(0);
		}*/
		
	}

}
