package com.realsight.brain.timeseries.lib.series;

import static com.realsight.brain.timeseries.lib.util.Util.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultipleDoubleSeries extends TimeSeries<LinkedList<Double>> {
    List<String> mNames;

    public MultipleDoubleSeries(Collection<String> names) {
        mNames = new ArrayList<>(names);
    }

    public MultipleDoubleSeries(DoubleSeries... series) {
        mNames = new ArrayList<>();
        for (int i = 0; i < series.length; i++) {
            if (i == 0) {
                _init(series[i]);
            } else {
                addSeries(series[i]);
            }
        }
    }

    public MultipleDoubleSeries(List<Entry<LinkedList<Double>>> data, Collection<String> names) {
        mNames = new ArrayList<>(names);
        mData = data;
    }
    
    void _init(DoubleSeries series) {
        mData = new ArrayList<>();
        for (Entry<Double> entry : series) {
            LinkedList<Double> list = new LinkedList<>();
            list.add(entry.mT);
            add(new Entry<LinkedList<Double>>(list, entry.mInstant));
        }
        mNames.add(series.mName);
    }

    public void addSeries(DoubleSeries series) {
    	check(this.isAscending());
        check(series.isAscending());

        Iterator<Entry<LinkedList<Double>>> i1 = this.iterator();
        Iterator<Entry<Double>> i2 = series.iterator();

        List<Entry<LinkedList<Double>>> newEntries = new ArrayList<>();

        while (i1.hasNext() && i2.hasNext()) {
            Entry<LinkedList<Double>> n1 = i1.next();
            Entry<Double> n2 = i2.next();

            while (!n2.mInstant.equals(n1.mInstant)) {
                if (n1.mInstant < n2.mInstant) {
                    while (i1.hasNext()) {
                        n1 = i1.next();
                        if (!(n1.mInstant<n2.mInstant)) {
                            break;
                        }
                    }
                } else if (n2.mInstant<n1.mInstant) {
                    while (i2.hasNext()) {
                        n2 = i2.next();
                        if (!(n2.mInstant<n1.mInstant)) {
                            break;
                        }
                    }
                }
            }

            if (n2.mInstant.equals(n1.mInstant)) {
            	LinkedList<Double> tmp = n1.getItem();
            	tmp.add(n2.getItem());
                newEntries.add(new Entry<LinkedList<Double>>(tmp, n1.getInstant()));
            }
        }
        mData = newEntries;
        mNames.add(series.mName);
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
    	return new MultipleDoubleSeries(newEntries, mNames);
    }
    
    public DoubleSeries getColumn(String name) {
        int index = getNames().indexOf(name);
        List<Entry<Double>> entries = new ArrayList<>();
        for(int i = 0; i < this.getData().size(); i++){
        	entries.add(new Entry<Double>(this.getData().get(i).getItem().get(index), 
        			this.getData().get(i).getInstant()));
        }
        return new DoubleSeries(entries, name);
    }

    public int indexOf(String name) {
        return mNames.indexOf(name);
    }

    public List<String> getNames() {
        return mNames;
    }

    @Override 
    public String toString() {
        return mData.isEmpty() ? "MultipleDoubleSeries{empty}" :
            "MultipleDoubleSeries{" +
                "mNames={" + mNames.size() +
                ", from=" + mData.get(0).getInstant() +
                ", to=" + mData.get(mData.size() - 1).getInstant() +
                ", size=" + mData.size() +
                '}';
    }
}

