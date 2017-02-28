package com.realsight.brain.timeseries.lib.util.plot;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.nd4j.linalg.api.ndarray.INDArray;

import com.realsight.brain.timeseries.lib.series.DoubleSeries;

public class Plot {

	private static void addSeries(XYSeriesCollection dataSet, List<Double> x, List<Double> y, String label) {
		// TODO Auto-generated method stub
		final double[] xd = new double[x.size()];
        final double[] yd = new double[y.size()];
        for(int i = 0; i < x.size(); i++)
        	xd[i] = x.get(i);
        for(int i = 0; i < y.size(); i++)
        	yd[i] = y.get(i);
        final XYSeries s = new XYSeries(label);
        for( int j=0; j<xd.length; j++ ) s.add(xd[j],yd[j]);
        dataSet.addSeries(s);
	}

	public static void plot(String name, DoubleSeries series, DoubleSeries ... subSeries) {
		// TODO Auto-generated method stub
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
    	for(int i = 0; i < series.size(); i++){
    		x.add(0.0+series.get(i).getInstant());
    		y.add(series.get(i).getItem());
    	}
    	
    	final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet,x,y,series.getName());
        
        for( int i=0; i<subSeries.length; i++ ){
        	List<Double> p = new ArrayList<Double>();
        	for (int j = 0, k = 0; j < series.size(); ) {
        		if ( k >= subSeries[i].size() ) {
        			p.add(0.0);
        			j++;
        		} else if ( subSeries[i].get(k).getInstant().equals(series.get(j).getInstant()) ) {
        			p.add(subSeries[i].get(k).getItem());
        			k++; j++;
        		} else if ( subSeries[i].get(k).getInstant() < series.get(j).getInstant() ) {
        			k++;
        		} else {
        			p.add(0.0);
        			j++;
        		}
        	}
            addSeries(dataSet,x,p,subSeries[i].getName());
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
        		name,      // chart title
                "Time",                     // x axis label
                "Value", 		// y axis label
                dataSet,                    // data
                PlotOrientation.VERTICAL,
                true,                       // include legend
                true,                       // tooltips
                false                       // urls
        );

        final ChartPanel panel = new ChartPanel(chart);

        final JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
	}
    
}
