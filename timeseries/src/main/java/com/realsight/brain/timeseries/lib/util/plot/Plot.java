package com.realsight.brain.timeseries.lib.util.plot;

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
	//Plot the data
	public static void plot(final INDArray x, final INDArray y, final INDArray... predicted) {
        final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet,x,y,"True Function (Labels)");

        for( int i=0; i<predicted.length; i++ ){
            addSeries(dataSet,x,predicted[i],String.valueOf(i));
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Plot Example",      // chart title
                "Time",                     // x axis label
                "Number of Sunspot", 		// y axis label
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

    private static void addSeries(final XYSeriesCollection dataSet, final INDArray x, final INDArray y, final String label){
        final double[] xd = x.data().asDouble();
        final double[] yd = y.data().asDouble();
        final XYSeries s = new XYSeries(label);
        for( int j=0; j<xd.length; j++ ) s.add(xd[j],yd[j]);
        dataSet.addSeries(s);
    }
    
    @SafeVarargs
	public static void plot(final List<Double> y, final List<Double>... predicted){
    	List<Double> x = new ArrayList<Double>();
    	for(int i = 0; i < y.size(); i++){
    		x.add(i+0.0);
    	}
    	final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet,x,y,"True Function (Labels)");
        
        for( int i=0; i<predicted.length; i++ ){
            addSeries(dataSet,x,predicted[i],String.valueOf(i));
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "SBSBSB SBSBSB",      // chart title
                "Time",                     // x axis label
                "Number of Sunspot", 		// y axis label
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

	public static void plot(DoubleSeries s, DoubleSeries ... series) {
		// TODO Auto-generated method stub
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
    	for(int i = 0; i < s.size(); i++){
    		x.add((0.0+i));
    		y.add(s.get(i).getItem());
    	}
    	
    	final XYSeriesCollection dataSet = new XYSeriesCollection();
        addSeries(dataSet,x,y,"True Function (Labels)");
        
        for( int i=0; i<series.length; i++ ){
        	List<Double> p = new ArrayList<Double>();
        	for(int j = 0; j < s.size(); j++){
        		if(j < series[i].size())
        			p.add(series[i].get(j).getItem());
        		else p.add(0.0);
        	}
            addSeries(dataSet,x,p,String.valueOf(i));
        }

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "SBSBSB SBSBSB",      // chart title
                "Time",                     // x axis label
                "Number of Sunspot", 		// y axis label
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
