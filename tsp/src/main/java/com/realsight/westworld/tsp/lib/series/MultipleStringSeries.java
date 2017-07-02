package com.realsight.westworld.tsp.lib.series;

import static com.realsight.westworld.tsp.lib.util.Util.check;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultipleStringSeries extends TimeSeries<LinkedList<String>> {
    List<String> property_list;
    String name;

    public MultipleStringSeries(String name, Collection<String> names) {
        property_list = new ArrayList<String>(names);
        this.name = name;
    }

    public MultipleStringSeries(String name, StringSeries... series) {
        property_list = new ArrayList<String>();
        this.name = name;
        for (int i = 0; i < series.length; i++) {
            if (i == 0) {
                _init(series[i]);
            } else {
                addSeries(series[i]);
            }
        }
    }
    
    public MultipleStringSeries(String name, List<StringSeries> series) {
    	this.name = name;
        property_list = new ArrayList<String>();
        for (int i = 0; i < series.size(); i++) {
            if (i == 0) {
                _init(series.get(i));
            } else {
                addSeries(series.get(i));
            }
        }
    }

    public MultipleStringSeries(List<Entry<LinkedList<String>>> data, Collection<String> names) {
        property_list = new ArrayList<String>(names);
        mData = data;
    }

	void _init(StringSeries series) {
        mData = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<LinkedList<String>>>();
        for (Entry<String> entry : series) {
            LinkedList<String> list = new LinkedList<String>();
            list.add(entry.mT);
            add(new Entry<LinkedList<String>>(list, entry.mInstant));
        }
        property_list.add(series.mName);
    }
    public void addSeries(StringSeries series) {
    	check(this.isAscending());
        check(series.isAscending());

        Iterator<Entry<LinkedList<String>>> i1 = this.iterator();
        Iterator<Entry<String>> i2 = series.iterator();

        List<Entry<LinkedList<String>>> newEntries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<LinkedList<String>>>();

        while (i1.hasNext() && i2.hasNext()) {
            Entry<LinkedList<String>> n1 = i1.next();
            Entry<String> n2 = i2.next();

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
            	LinkedList<String> tmp = n1.getItem();
            	tmp.add(n2.getItem());
                newEntries.add(new Entry<LinkedList<String>>(tmp, n1.getInstant()));
            }
        }
        mData = newEntries;
        property_list.add(series.mName);
    }

    public MultipleStringSeries subSeries(int l, int r) throws Exception{
    	
    	if(l<0 || r>size())
    		throw new Exception("l<0 || r>size()");
    	List<Entry<LinkedList<String>>> newEntries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<LinkedList<String>>>();
    	for(int i = l; i < r; i++){
    		LinkedList<String> t = new LinkedList<String>();
    		for(int j = 0; j < this.mData.get(i).getItem().size(); j++)
    			t.add(this.mData.get(i).getItem().get(j));
    		newEntries.add(new Entry<LinkedList<String>>(t, this.mData.get(i).getInstant()));
    	}
    	return new MultipleStringSeries(newEntries, property_list);
    }
    
    
    
    public StringSeries getColumn(int col_index) {
    	List<Entry<String>> entries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<String>>();
        for(int i = 0; i < this.getData().size(); i++){
        	entries.add(new Entry<String>(this.getData().get(i).getItem().get(col_index), 
        			this.getData().get(i).getInstant()));
        }
        return new StringSeries(entries, name);
    }
    
    public StringSeries getColumn(String col_name) {
        int col_index = getProperty_list().indexOf(col_name);
        return getColumn(col_index);
    }
    
    public String getValue(int col_index, int row_index) {
        return this.mData.get(row_index).getItem().get(col_index);
    }
    
    public String getValue(String col_name, int row_index) {
        int col_index = getProperty_list().indexOf(col_name);
        return getValue(col_index, row_index);
    }

    public int indexOf(String col_name) {
        return property_list.indexOf(col_name);
    }

    public List<String> getProperty_list() {
        return property_list;
    }
    
    public int getColumnNum() {
        return this.property_list.size();
    }
    
    public String getName() {
    	return this.name;
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

