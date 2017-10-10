package com.realsight.westworld.bnanalysis.server;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.realsight.westworld.bnanalysis.service.MetricBeatService;
import com.realsight.westworld.bnanalysis.solr.MetricNext;
import com.realsight.westworld.bnanalysis.solr.MetricOption;

public class MetricBeatServer {

	private MetricBeatServer() {}
	
	public MetricBeatServer(String sorl_url, String bn_name) {
		
		MetricOption option = new MetricOption(sorl_url, bn_name);
//		MetricNext next = new MetricNext();
		MetricBeatService service = new MetricBeatService();
		long start = option.startTime;
		while (true) {
			if (Calendar.getInstance().getTimeInMillis() > start + option.gap) {
				try {
					Date myDate = new Date(start);
					for (String hostname : option.hostNames) {
						service.runService(start, option, hostname, myDate);
					}
					start = start + option.gap;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("完成数据的上传。");
			} else {
				try {
					System.out.println("等待数据收集。");
					Thread.sleep(1000*60*10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		MetricBeatServer server = new MetricBeatServer("http://10.0.67.14:8080/solr/option", "bn_metrics15");
	}
	
}
