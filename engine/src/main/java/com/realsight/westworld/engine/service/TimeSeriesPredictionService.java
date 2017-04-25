package com.realsight.westworld.engine.service;

import com.basic.Pair;
import com.bnAnalysis.NeticaApi;
import com.realsight.westworld.engine.job.JobManager;
import com.realsight.westworld.engine.job.TimeSeriesPredictionJob;
import com.realsight.westworld.engine.model.Edge;
import com.realsight.westworld.engine.model.Vertice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

@SuppressWarnings("deprecation")
@Service
public class TimeSeriesPredictionService {

    public String get_version() throws Exception {
        return "1.0.0";
    }

    public boolean start() {
	    try {
            JobDetail jobDetail = JobBuilder.newJob(TimeSeriesPredictionJob.class)
                    .withIdentity("Job_1", "tsp").build();

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("trigger_1", "tsp")
                    .startNow()
                    .withSchedule(
                            SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(60).repeatForever() // 时间间隔
                    ).build();
            JobManager.getInstance().get_scheduler().scheduleJob(jobDetail, trigger);
        } catch (Exception e){
	        e.printStackTrace();
	        return false;
        }
        return true;
    }
    
    public Pair<ArrayList<Vertice>, ArrayList<Edge>> getEdgeList() throws Exception {
    	List<Vertice> vertice_list = new ArrayList<Vertice>();
    	List<Edge> edge_list = new ArrayList<Edge>();
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		
		List<String> list =  netica.getGONodes();
		
		for (int i = 0; i < list.size(); i++) {
			vertice_list.add(new Vertice(list.get(i)));
		}

		for (int i = 0; i < list.size(); i++) {
			List<String> tmp_list = netica.getChildren(list.get(i));
			for (int j = 0; j < tmp_list.size(); j++) {
				edge_list.add(new Edge(list.get(i), tmp_list.get(j)));
			}
		}
		netica.finalize();
		Pair<ArrayList<Vertice>, ArrayList<Edge>> pair = new Pair<ArrayList<Vertice>, ArrayList<Edge>>();
		pair.first = (ArrayList<Vertice>) vertice_list;
		pair.second = (ArrayList<Edge>) edge_list;
    	return pair;
    }

	public static void main(String[] args) {

	}
}
