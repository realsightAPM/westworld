package com.realsight.westworld.tsp.lib.series;

import static com.realsight.westworld.tsp.lib.util.Util.check;

import java.util.*;

public class TimeSeries<T> implements Iterable<TimeSeries.Entry<T>>{
    public static class Entry<T> implements Comparable<Entry<T>> {
        T mT;
        Long mInstant;

        public Entry(T t, Long instant) {
            mT = t;
            mInstant = instant;
        }

        public T getItem() {
            return mT;
        }

        public Long getInstant() {
            return mInstant;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            @SuppressWarnings("rawtypes")
			Entry entry = (Entry) o;

            if (!mInstant.equals(entry.mInstant)) return false;
            if (mT != null ? !mT.equals(entry.mT) : entry.mT != null) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = mT != null ? mT.hashCode() : 0;
            result = 31 * result + mInstant.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Entry{" +
                "mInstant=" + mInstant +
                ", mT=" + mT.toString() +
                '}';
        }
        
		public int compareTo(Entry<T> arg) {
			// TODO Auto-generated method stub
			if(this.getInstant() > arg.getInstant())
				return 1;
			else if(this.getInstant() < arg.getInstant())
				return -1;
			return 0;
		}
    }

    List<Entry<T>> mData;

    public TimeSeries() {
        mData = new ArrayList<Entry<T>>();
    }

    protected TimeSeries(List<Entry<T>> data) {
        mData = data;
    }

    public int size() {
        return mData.size();
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    public boolean add(Entry<T> tEntry) {
        return mData.add(tEntry);
    }

    public void add(T item, Long instant) {
        add(new Entry<T>(item, instant));
    }

    public Iterator<Entry<T>> reversedIterator() {
        check(!(mData instanceof LinkedList));
        List<Entry<T>> res = new ArrayList<Entry<T>>();
        for(int i = mData.size()-1; i >= 0 ; i--){
        	res.add(mData.get(i));
        }
        return res.iterator();
    }

    public Iterator<Entry<T>> iterator() {
        return mData.iterator();
    }

    public List<Entry<T>> getData() {
        return mData;
    }

    public Entry<T> get(int index) {
    	if (index < 0)
    		index = 0;
        return mData.get(index);
    }

    public interface MergeFunction<T, F> {
        F merge(T t1, T t2);
    }

    public interface MergeFunction2<T1, T2, F> {
        F merge(T1 t1, T2 t2);
    }
    
    public boolean isAscending() {
        return size() <= 1 || get(0).getInstant()<=(get(1).mInstant);
    }

    public TimeSeries<T> toAscending() {
        if (!isAscending()) {
            return reverse();
        }
        return this;
    }

    public TimeSeries<T> toDescending() {
        if (isAscending()) {
            return reverse();
        }
        return this;
    }

    public TimeSeries<T> reverse() {
        ArrayList<Entry<T>> entries = new ArrayList<Entry<T>>(mData);
        Collections.reverse(entries);
        return new TimeSeries<T>(entries);
    }

    public TimeSeries<T> lag(int k) {
        return lag(k, false, null);
    }

    public TimeSeries<T> lag(int k, boolean addEmpty, T emptyVal) {
        check(k > 0);
        check(mData.size() >= k);

        ArrayList<Entry<T>> entries = new ArrayList<Entry<T>>(addEmpty ? mData.size() : mData.size() - k);
        if (addEmpty) {
            for (int i = 0; i < k; i++) {
                entries.add(new Entry<T>(emptyVal, mData.get(i).mInstant));
            }
        }

        for (int i = k; i < size(); i++) {
            entries.add(new Entry<T>(mData.get(i - k).getItem(), mData.get(i).getInstant()));
        }

        return new TimeSeries<T>(entries);
    }

    @SuppressWarnings("unchecked")
	public static <T1, T2, F> TimeSeries<F> and(TimeSeries<T1> t1, TimeSeries<T2> t2) {
        check(t1.isAscending());
        check(t2.isAscending());

        Iterator<Entry<T1>> i1 = t1.iterator();
        Iterator<Entry<T2>> i2 = t2.iterator();

        List<Entry<F>> newEntries = new ArrayList<Entry<F>>();

        while (i1.hasNext() && i2.hasNext()) {
            Entry<T1> n1 = i1.next();
            Entry<T2> n2 = i2.next();

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
                newEntries.add(new Entry<F>((F) n1.mT, n1.mInstant));
            }
        }

        return new TimeSeries<F>(newEntries);
    }

    @SuppressWarnings("unchecked")
	public static <T1, T2, F> TimeSeries<F> or(TimeSeries<T1> t1, TimeSeries<T2> t2) {
        check(t1.isAscending());
        check(t2.isAscending());

        Iterator<Entry<T1>> i1 = t1.iterator();
        Iterator<Entry<T2>> i2 = t2.iterator();

        List<Entry<F>> newEntries = new ArrayList<Entry<F>>();

        while (i1.hasNext() && i2.hasNext()) {
            Entry<T1> n1 = i1.next();
            Entry<T2> n2 = i2.next();

            while (!n2.mInstant.equals(n1.mInstant)) {
                if (n1.mInstant < n2.mInstant) {
                    while (i1.hasNext()) {
                    	newEntries.add(new Entry<F>((F) n1.mT, n1.mInstant));
                        n1 = i1.next();
                        if (!(n1.mInstant<n2.mInstant)) {
                            break;
                        }
                    }
                } else if (n2.mInstant<n1.mInstant) {
                    while (i2.hasNext()) {
                    	newEntries.add(new Entry<F>((F) n2.mT, n2.mInstant));
                        n2 = i2.next();
                        if (!(n2.mInstant<n1.mInstant)) {
                            break;
                        }
                    }
                }
            }

            if (n2.mInstant.equals(n1.mInstant)) {
                newEntries.add(new Entry<F>((F) n1.mT, n1.mInstant));
            }
        }

        return new TimeSeries<F>(newEntries);
    }
    
    public void sort() {
    	Collections.sort(this.getData());
    }
    
    @Override
    public String toString() {
        return mData.isEmpty() ? "TimeSeries{empty}" :
            "TimeSeries{" +
                "from=" + mData.get(0).getInstant() +
                ", to=" + mData.get(size() - 1).getInstant() +
                ", size=" + mData.size() +
                '}';
    }
}

