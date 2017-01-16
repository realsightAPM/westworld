package apm.main;

import apm.http.DataSpider;
import apm.mode.SystemInfo;
import apm.webstress.Stress;

public class Main {

	public static void main(String[] args) {
		DataSpider spider = new DataSpider();
		spider.start();
		
		Stress stress = new Stress();
		try {
			stress.run();
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}

}
