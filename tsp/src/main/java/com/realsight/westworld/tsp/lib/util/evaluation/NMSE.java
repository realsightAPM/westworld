package com.realsight.westworld.tsp.lib.util.evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.realsight.westworld.tsp.lib.csv.CsvReader;

public class NMSE {
	private static char delimiter = ',';
	private static Charset charset = Charset.forName("ISO-8859-1");
	public static double nmse(String localDir) throws FileNotFoundException{
		List<Double> model = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		double avr = 0.0;
		try {
			CsvReader cr = new CsvReader(localDir, delimiter, charset);
			cr.readHeaders();
			if(cr.getIndex("y") == -1)
				throw new IOException("File not exists y.");
			if(cr.getIndex("model") == -1)
				throw new IOException("File not exists model.");
			for(int i = 0; i < 100; i ++)
				cr.readRecord();
			while(cr.readRecord()){
				model.add(Double.parseDouble(cr.get("model")));
				y.add(Double.parseDouble(cr.get("y")));
				avr += Double.parseDouble(cr.get("y"));
			}
			cr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Plot.plot(y, model);
		avr /= y.size();
		double NMSE_A = 0;
		double NMSE_B = 0;
		for (int j = 0; j < y.size(); j++) {
			NMSE_A += Math.pow(y.get(j)-model.get(j), 2.0);
			NMSE_B += Math.pow(y.get(j)-avr, 2.0);
		}
		System.out.println("NMSE Eva Comleted.\nNMSE = " + NMSE_A/NMSE_B + ", MSE = " + Math.sqrt(NMSE_A/y.size())
				+ ", SD = " + Math.sqrt(NMSE_B/y.size()));
		return NMSE_A/NMSE_B;
	}
	public static void main(String[] args) throws FileNotFoundException{
		nmse("C:/Users/neusoft/AppData/Local/Temp/out-622836670992850694.csv");
	}
}
