package com.realsight.westworld.tsp.lib.series;

import static com.realsight.westworld.tsp.lib.util.Util.check;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.realsight.westworld.tsp.lib.util.Util;

public class StringSeries extends TimeSeries<String> {

	String mName;

    public StringSeries(List<Entry<String>> data, String name) {
        super(data);
        mName = name;
    }

    public StringSeries(String name) {
        super();
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
    
    public StringSeries add(StringSeries other) {

        Iterator<Entry<String>> i1 = this.iterator();
        Iterator<Entry<String>> i2 = other.iterator();

        List<Entry<String>> newEntries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<String>>();

        Entry<String> n1 = null;
        Entry<String> n2 = null;
        
        if(i1.hasNext()) n1 = i1.next();
    	else n1 = null;
        if(i2.hasNext()) n2 = i2.next();
    	else n2 = null;
        while((n1!=null) && (n2!=null)){
        	if (n1.mInstant <= n2.mInstant) {
                newEntries.add(new Entry<String>(n1.mT, n1.mInstant));
                if(i1.hasNext()) n1 = i1.next();
            	else n1 = null;
            } else if (n2.mInstant<n1.mInstant) {
                newEntries.add(new Entry<String>(n2.mT, n2.mInstant));
                if(i2.hasNext()) n2 = i2.next();
            	else n2 = null;
            }
        } 
        while(n1!=null) {
        	newEntries.add(new Entry<String>(n1.mT, n1.mInstant));
        	if(i1.hasNext()) n1 = i1.next();
        	else n1 = null;
        }
        while(n2!=null) {
        	newEntries.add(new Entry<String>(n2.mT, n2.mInstant));
        	if(i2.hasNext()) n2 = i2.next();
        	else n2 = null;
        }
        return new StringSeries(newEntries, mName);
    }
    
    public StringSeries and(StringSeries other) {
    	check(this.isAscending());
        check(other.isAscending());

        Iterator<Entry<String>> i1 = this.iterator();
        Iterator<Entry<String>> i2 = other.iterator();

        List<Entry<String>> newEntries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<String>>();

        while (i1.hasNext() && i2.hasNext()) {
            Entry<String> n1 = i1.next();
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
                newEntries.add(new Entry<String>(n1.mT, n1.mInstant));
            }
        }

        return new StringSeries(newEntries, mName);
    }

    public StringSeries or(StringSeries other) {
    	check(this.isAscending());
        check(other.isAscending());

        Iterator<Entry<String>> i1 = this.iterator();
        Iterator<Entry<String>> i2 = other.iterator();

        List<Entry<String>> newEntries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<String>>();

        while (i1.hasNext() && i2.hasNext()) {
            Entry<String> n1 = i1.next();
            Entry<String> n2 = i2.next();

            while (!n2.mInstant.equals(n1.mInstant)) {
                if (n1.mInstant < n2.mInstant) {
                    while (i1.hasNext()) {
                    	newEntries.add(new Entry<String>(n1.mT, n1.mInstant));
                        n1 = i1.next();
                        if (!(n1.mInstant<n2.mInstant)) {
                            break;
                        }
                    }
                } else if (n2.mInstant<n1.mInstant) {
                    while (i2.hasNext()) {
                    	newEntries.add(new Entry<String>(n2.mT, n2.mInstant));
                        n2 = i2.next();
                        if (!(n2.mInstant<n1.mInstant)) {
                            break;
                        }
                    }
                }
            }

            if (n2.mInstant.equals(n1.mInstant)) {
                newEntries.add(new Entry<String>(n1.mT, n1.mInstant));
            }
        }

        return new StringSeries(newEntries, mName);
    }

    public String getLast() {
        return getData().get(size() - 1).getItem();
    }

    public StringSeries tail(int n) {
        return new StringSeries(getData().subList(size() - n, size()), getName());
    }

    public String[] toArray() {
    	String[] res = new String[mData.size()];
    	for(int i = 0; i < mData.size(); i++)
    		res[i] = mData.get(i).getItem();
        return res;
    }

    @Override
    public StringSeries toAscending() {
        return new StringSeries(super.toAscending().mData, getName());
    }

    @Override
    public StringSeries toDescending() {
        return new StringSeries(super.toDescending().mData, getName());
    }

    @Override
    public StringSeries lag(int k) {
        return new StringSeries(super.lag(k).mData, getName());
    }
    
    public StringSeries subSeries(int l, int r){
    	if(l<0) l = 0;
    	if(r>size()) r = size();
    	Util.check(l<=r, "DoubleSeries.subSeries error l > r,"+"l="+l+",r="+r);
    	List<Entry<String>> newEntries = new ArrayList<com.realsight.westworld.tsp.lib.series.TimeSeries.Entry<String>>();
    	for(int i = l; i < r; i++){
    		newEntries.add(new Entry<String>(mData.get(i).getItem(), this.mData.get(i).getInstant()));
    	}
    	return new StringSeries(newEntries, mName);
    }
    
    public int getIdxFromInstant(Long mInstant){
    	for(int i = 0; i < this.size(); i++){
    		if(mInstant == this.get(i).getInstant())
    			return i;
    	}
    	return -1;
    }
    
    
    @Override
    public String toString() {
        return mData.isEmpty() ? "DoubleSeries{empty}" :
            "DoubleSeries{" +
                "mName=" + mName +
                ", from=" + mData.get(0).getInstant() +
                ", to=" + mData.get(mData.size() - 1).getInstant() +
                ", size=" + mData.size() +
                '}';
    }
}

