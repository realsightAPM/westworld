package com.realsight.westworld.tsp.lib.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Jama.Matrix;
import com.realsight.westworld.tsp.lib.util.Util;

public class MultipleDoubleSeries extends TimeSeries<LinkedList<Double>> {
    List<String> property_list;
    String name;

    public MultipleDoubleSeries(String name, Collection<String> names) {
        property_list = new ArrayList<>(names);
        this.name = name;
    }

    public MultipleDoubleSeries(String name, DoubleSeries... series) {
        property_list = new ArrayList<>();
        this.name = name;
        for (int i = 0; i < series.length; i++) {
            if (i == 0) {
                _init(series[i]);
            } else {
                addSeries(series[i]);
            }
        }
    }
    
    public MultipleDoubleSeries(String name, List<DoubleSeries> series) {
    	this.name = name;
        property_list = new ArrayList<>();
        for (int i = 0; i < series.size(); i++) {
            if (i == 0) {
                _init(series.get(i));
            } else {
                addSeries(series.get(i));
            }
        }
    }

    public MultipleDoubleSeries(List<Entry<LinkedList<Double>>> data, Collection<String> names) {
        property_list = new ArrayList<>(names);
        mData = data;
    }

	void _init(DoubleSeries series) {
        mData = new ArrayList<>();
        for (Entry<Double> entry : series) {
            LinkedList<Double> list = new LinkedList<>();
            list.add(entry.mT);
            add(new Entry<LinkedList<Double>>(list, entry.mInstant));
        }
        property_list.add(series.mName);
    }

    public void addSeries(DoubleSeries series) {
    	Util.check(this.isAscending());
        Util.check(series.isAscending());

        Iterator<Entry<LinkedList<Double>>> i1 = this.iterator();
        Iterator<Entry<Double>> i2 = series.iterator();

        List<Entry<LinkedList<Double>>> newEntries = new ArrayList<>();

        while (i1.hasNext() && i2.hasNext()) {
            Entry<LinkedList<Double>> n1 = i1.next();
            Entry<Double> n2 = i2.next();

            while (!n2.mInstant.equals(n1.mInstant)) {
                if (n1.mInstant < n2.mInstant) {
//                	LinkedList<Double> tmp = n1.getItem();
//                	tmp.add(0.0);
//                	newEntries.add(new Entry<LinkedList<Double>>(tmp, n1.getInstant()));
                	n1 = i1.next();
                } else if (n2.mInstant<n1.mInstant) {
                	n2 = i2.next();
                }
            }

            if (n2.mInstant.equals(n1.mInstant)) {
            	LinkedList<Double> tmp = n1.getItem();
            	tmp.add(n2.getItem());
                newEntries.add(new Entry<LinkedList<Double>>(tmp, n1.getInstant()));
            }
        }
        mData = newEntries;
        property_list.add(series.mName);
    }

    public MultipleDoubleSeries subSeries(int l, int r) throws Exception{
    	
    	if(l<0 || r>size())
    		throw new Exception("l<0 || r>size()");
    	List<Entry<LinkedList<Double>>> newEntries = new ArrayList<>();
    	for(int i = l; i < r; i++){
    		LinkedList<Double> t = new LinkedList<Double>();
    		for(int j = 0; j < this.mData.get(i).getItem().size(); j++)
    			t.add(this.mData.get(i).getItem().get(j));
    		newEntries.add(new Entry<LinkedList<Double>>(t, this.mData.get(i).getInstant()));
    	}
    	return new MultipleDoubleSeries(newEntries, property_list);
    }
    
    public DoubleSeries getColumn(String name) {
        int index = getProperty_list().indexOf(name);
        List<Entry<Double>> entries = new ArrayList<>();
        for(int i = 0; i < this.getData().size(); i++){
        	entries.add(new Entry<Double>(this.getData().get(i).getItem().get(index), 
        			this.getData().get(i).getInstant()));
        }
        return new DoubleSeries(entries, name);
    }
    
    public DoubleSeries getColumn(int index) {
        List<Entry<Double>> entries = new ArrayList<>();
        for(int i = 0; i < this.getData().size(); i++){
        	entries.add(new Entry<Double>(this.getData().get(i).getItem().get(index), 
        			this.getData().get(i).getInstant()));
        }
        return new DoubleSeries(entries, getProperty_list().get(index));
    }

    public int indexOf(String name) {
        return property_list.indexOf(name);
    }

    public List<String> getProperty_list() {
        return property_list;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public List<Matrix> getTData() {
    	List<Matrix> tData = new ArrayList<Matrix>();
    	for (Entry<LinkedList<Double>> entry : this.mData) {
    		double[] vector = new double[entry.getItem().size()];
    		for (int i = 0; i < vector.length; i++) {
    			vector[i] = entry.getItem().get(i);
    		}
    		tData.add(new Matrix(vector, vector.length));
    	}
    	return tData;
    }
    
    public int size() {
    	return this.mData.size();
    }
    
    @Override 
    public String toString() {
        return mData.isEmpty() ? "MultipleDoubleSeries{empty}" :
            "MultipleDoubleSeries{" +
                "property_list={" + property_list.size() +
                ", from=" + mData.get(0).getInstant() +
                ", to=" + mData.get(mData.size() - 1).getInstant() +
                ", size=" + mData.size() +
                '}';
    }
}

