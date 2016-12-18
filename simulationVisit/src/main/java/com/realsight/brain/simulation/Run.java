package com.realsight.brain.simulation;

import com.realsight.brain.simulation.http.DataSpider;

public class Run {

	public static void main(String[] args) {
		DataSpider spider = new DataSpider();
		spider.start();
		
	}

}
