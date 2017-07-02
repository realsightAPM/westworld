package com.realsight.westworld.engine.service;

import com.google.common.collect.RangeMap;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.io.WriteCSV;
import com.realsight.westworld.bnanalysis.service.NeticaApi;
import com.realsight.westworld.bnanalysis.service.TSPService;
import com.realsight.westworld.engine.io.ReadSolr;
import com.realsight.westworld.engine.job.JobManager;
import com.realsight.westworld.engine.job.TimeSeriesPredictionJob;
import com.realsight.westworld.engine.model.Edge;
import com.realsight.westworld.engine.model.Inferance;
import com.realsight.westworld.engine.model.Vertice;

//import norsys.netica.NeticaException;

import java.io.IOException;
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

	public ReadSolr readSolr;
	
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
    
    public double postInfer(List<Inferance> infers) throws Exception {
    	
    	NeticaApi netica = new NeticaApi();
    	netica.loadNet();
    	netica.loadRangeMap();
    	
    	List<Pair<String, float[]>> hoods = new ArrayList<Pair<String, float[]>>();
    	for (int i = 0; i < infers.size()-1; i++) {
    		
    		String[] strs = infers.get(i).getInfer().split(":");
    		if (strs.length < 2) continue;
    		String[] inferStrs = strs[1].split(",");
    		float[] tmp_hood = new float[inferStrs.length];
    		for (int j = 0; j < tmp_hood.length; j++) {
    			tmp_hood[j] = Float.valueOf(inferStrs[j]);
    		}
    		hoods.add(new Pair<String, float[]>(strs[0], tmp_hood));
    	}
    	
    	String var = infers.get(infers.size()-1).getInfer();
    	String state = "" + ((char) ('a'+ netica.rangeMap.get(var).length-1));
    	
    	double res = netica.getInfer(hoods, new Pair<String, String>(infers.get(infers.size()-1).getInfer(), state), "likelihood");
    	
    	netica.finalize();
    	return res;
    }
    
    public List<Double> getTSP(String csv_file, int current, String tar_var) throws Exception {
    	TSPService tspService = new TSPService();
    	Map<String, Double> map = tspService.getTSPService(csv_file, current);
    	if (map == null) {
    		System.out.println("map是null");
    		return null;
    	}
    	
    	NeticaApi netica = new NeticaApi();
    	netica.loadNet();
    	netica.loadRangeDouble();
    	List<Pair<String, String>> pairList = new ArrayList<Pair<String, String>> ();
    	for (String it : map.keySet()) {
    		if (it.equals(tar_var)) continue;
    		double tmp = map.get(it);
    		pairList.add(new Pair<String, String>(it, netica.mapDoubleState(tmp, it)));
    	}
    	
//    	String state = "" + ((char) ('a'+ netica.rangeMap.get(tar_var).length-1));
//    	Pair<String, String> pair = new Pair<String, String>(tar_var, state);
    	
    	List<Double> res = netica.getInfer(pairList, tar_var);
    	netica.finalize();
    	return res;
    }
    
    public List<Pair<String, Integer>> getLogCount() throws IOException {
    	readSolr = new ReadSolr();
    	List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
    	for (String it : readSolr.urlList) {
    		list.add(new Pair<String, Integer> (it, readSolr.urlHashCount.get(it)));
    	}
    	WriteCSV writer = new WriteCSV();
    	
    	
    	int m = readSolr.urlArray.size();
    	int n = readSolr.urlArray.get(0).size();

    	String[] attrList = new String[m];
    	Double[][] matrix = new Double[m][n];
    	
    	for (int i = 0; i < m; i++) {
    		for (int j = 0; j < n; j++) {
    			matrix[i][j] = readSolr.urlArray.get(i).get(j);
    		}
    	}
    	
    	int cnt = 0;
    	for (String key : readSolr.urlHash.keySet()) {
    		attrList[cnt] = "_"+(new Integer(cnt)).toString()+"";
    		cnt++;
    	}
    	
    	writer.writeArrayCSV(readSolr.urlArray, attrList, "./", "log.csv");
    	return list; 
    }
    

	public static void main(String[] args) {

	}
}
