package com.realsight.westworld.engine.job;

import Jama.Matrix;
import com.realsight.westworld.engine.io.MetricInputStream;
import com.realsight.westworld.engine.io.MetricOutputStream;
import com.realsight.westworld.tsp.api.PredictionAPI;
import com.realsight.westworld.tsp.lib.series.DoubleSeries;
import com.realsight.westworld.tsp.lib.series.MultipleDoubleSeries;
import com.realsight.westworld.tsp.lib.series.TimeSeries;
import com.realsight.westworld.engine.  util.TimeUtil;
import com.realsight.westworld.tsp.lib.util.Util;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.quartz.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class TimeSeriesPredictionJob implements Job {
	private DoubleSeries nSeries;
    private PredictionAPI papi;


    public void init() {
        if (nSeries == null) {
            nSeries = new DoubleSeries("");
        }
        if(papi == null){
            papi = new PredictionAPI(new MultipleDoubleSeries("", nSeries));
        }
    }


    public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
        init();
        Iterator<SolrDocument> inputs = MetricInputStream.getInstance().input();
        ArrayList<SolrInputDocument> outputs = new ArrayList<>();
        while (inputs.hasNext()){
            SolrDocument sd = inputs.next();
            SolrInputDocument output = new SolrInputDocument();
            output.addField("rs_timestamp", sd.get("rs_timestamp"));
            Double metric = Double.parseDouble(String.valueOf(sd.get("cpu_f")));
            Long ts = Long.parseLong(String.valueOf(sd.get("rs_timestamp_l")));
            TimeSeries.Entry<Double> entry = new TimeSeries.Entry<Double>(metric, ts);
            Matrix value = Util.toVec(entry.getItem());
            papi.todayValue(value, ts);
            Double p_vlaue = papi.prediction();
            if(p_vlaue == null){
                continue;
            }

            Long interval = 1l * 1000;
            ArrayList<Double> tsp_values = new ArrayList<>();
            ArrayList<String> tsp_timestamps = new ArrayList<>();
            output.addField("last_timestamp_l", ts);
            output.addField("viz_timestamp_s", TimeUtil.formatUnixtime2(ts));
            output.addField("last_cpu_f", metric);
            ts = ts + interval;
            tsp_timestamps.add(TimeUtil.formatUnixtime2(ts));
            tsp_values.add(p_vlaue);
            Random r = new Random();
            for(int i = 1; i < 10; i++){
                ts = ts + interval;
                tsp_timestamps.add(TimeUtil.formatUnixtime2(ts));
                tsp_values.add(p_vlaue + r.nextDouble() * r.nextInt(5) - r.nextDouble() * r.nextInt(5));
            }

            output.addField("type_s", "modelTSP");
            output.addField("tsp_values_ff", tsp_values);
            output.addField("tsp_timestamps_ss", tsp_timestamps);
            //System.out.println("output: " + output);
            outputs.add(output);
        }
        MetricOutputStream.getInstance().output(outputs);
    }

	public static void main(String[] args) {

		TimeSeriesPredictionJob tsps = new TimeSeriesPredictionJob();
		try {
            tsps.execute(null);
        } catch ( Exception e){
		    e.printStackTrace();
        }

	}
}
