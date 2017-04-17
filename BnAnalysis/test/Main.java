import java.nio.file.Path;
import java.nio.file.Paths;

import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries.Entry;
import com.realsight.westworld.tsp.api.OnlineTimeseriesPredictionAPI;
import com.realsight.westworld.tsp.lib.util.Util;
import com.realsight.westworld.tsp.lib.util.data.TimeseriesData;
import com.realsight.westworld.tsp.lib.util.plot.Plot;

import Jama.Matrix;

public class Main {
	
	public static void main(String[] args) {
		String root = System.getProperty("user.dir");
		Path dataPath = Paths.get(root, "inputjava_data2.csv");
		TimeseriesData ts = new TimeseriesData(dataPath.toString());
		DoubleSeries cpuSeries = ts.getPropertyDoubleSeries("cpu");
//		DoubleSeries sessionSeries = ts.getPropertyDoubleSeries("session_count");
//		sessionSeries.normly();
//		cpuSeries.normly();
		MultipleDoubleSeries mSeries = new MultipleDoubleSeries("metrics", cpuSeries);
		Plot.plot(mSeries);
		OnlineTimeseriesPredictionAPI otp = new OnlineTimeseriesPredictionAPI();
		otp.update(mSeries);
		for(int iter = 0; iter < 10; iter ++) {
			otp.train(mSeries);
		}
		DoubleSeries real = new DoubleSeries("real");
		DoubleSeries p = new DoubleSeries("p");
		for (int i = 0; i < mSeries.size(); i++) {
			Matrix value = Util.toVec(mSeries.get(i).getItem().iterator());
			Long timestamp = mSeries.get(i).getInstant();
			if(i > 600) {
				Matrix p_value = otp.prediction();
				real.add(new Entry<Double>(value.get(0, 0), i+0L));
				p.add(new Entry<Double>(p_value.get(0, 0), i+0L));
				System.out.println(p_value.get(0, 0) + "," + value.get(0, 0));
			}
			otp.todayValue(value, timestamp);
		}
		Plot.plot("p", real, p);
	}
}
