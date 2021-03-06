package com.realsight.westworld.tsp.lib.util.plot;

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

import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;

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
	
	public static void plot(MultipleDoubleSeries mSeries){
		DoubleSeries[] series = new DoubleSeries[mSeries.getProperty_list().size()];
		for (int i = 0; i < mSeries.getProperty_list().size(); i++) {
			series[i] = mSeries.getColumn(i);
		}
		plot(mSeries.getName(), series);
	}
	
	public static void plot(String name, DoubleSeries ... subSeries) {
		// TODO Auto-generated method stub
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
    	for(int i = 0; i < subSeries[0].size(); i++){
    		x.add(0.0+subSeries[0].get(i).getInstant());
    		y.add(subSeries[0].get(i).getItem());
    	}
    	
    	final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet,x,y,subSeries[0].getName());
        
        for( int i=1; i<subSeries.length; i++ ){
        	List<Double> p = new ArrayList<Double>();
        	for (int j = 0, k = 0; j < subSeries[0].size(); ) {
//        		System.out.println(subSeries[i].size());
//        		p.add(subSeries[i].get(k).getItem());
//    			k++; j++;
        		if ( k >= subSeries[i].size() ) {
        			p.add(0.0);
        			j++;
        		} else if ( subSeries[i].get(k).getInstant().equals(subSeries[0].get(j).getInstant()) ) {
        			p.add(subSeries[i].get(k).getItem());
        			k++; j++;
        		} else if ( subSeries[i].get(k).getInstant() < subSeries[0].get(j).getInstant() ) {
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
	@SafeVarargs
	public static void plot(String name, List<Double> x, List<Double> ... y) {
		// TODO Auto-generated method stub
    	
    	final XYSeriesCollection dataSet = new XYSeriesCollection();
        
        
        for( int i=0; i<y.length; i++ ){
        	addSeries(dataSet,x,y[i],"y_"+i);
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

	public static void plot(String name, List<DoubleSeries> series) {
		// TODO Auto-generated method stub
		DoubleSeries[] mSeries = new DoubleSeries[series.size()];
		for (int i = 0; i < series.size(); i++) {
			mSeries[i] = series.get(i);
		}
		plot(name, mSeries);
	}
	
}
