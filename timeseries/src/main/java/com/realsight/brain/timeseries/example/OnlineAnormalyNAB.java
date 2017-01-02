package com.realsight.brain.timeseries.example;


import java.io.File;
import java.nio.file.Paths;

import com.realsight.brain.timeseries.main.AnormalyNABMain;

/**
 * @author Sun Muxin
 * 
 */ 
public class OnlineAnormalyNAB {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String root = new File(new File(System.getProperty("user.dir")).getParent()).getParent();
		String nabPath = Paths.get(root, "NAB").toString();
		AnormalyNABMain.run(nabPath);
	}
}
