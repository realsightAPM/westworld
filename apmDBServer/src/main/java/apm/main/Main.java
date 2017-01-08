package apm.main;

import apm.http.DataSpider;
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
