package com.realsight.westworld.engine.controller;

import com.google.gson.JsonObject;
import com.realsight.westworld.bnanalysis.algorithm.Pearson;
import com.realsight.westworld.bnanalysis.api.NeticaApi;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.service.OriginRootCause;
import com.realsight.westworld.bnanalysis.solr.SolrResults;
import com.realsight.westworld.engine.message.GraphResponse;
import com.realsight.westworld.engine.message.Response;
import com.realsight.westworld.engine.message.ResponseList;
import com.realsight.westworld.engine.model.Edge;
import com.realsight.westworld.engine.model.Inferance;
import com.realsight.westworld.engine.model.PieData;
import com.realsight.westworld.engine.model.PostItem;
import com.realsight.westworld.engine.model.Vertice;
import com.realsight.westworld.engine.service.TimeSeriesPredictionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TimeSeriesPredictionController {
	
	@Autowired
	TimeSeriesPredictionService tsps;
	public String setTarget;
	public String csvFile = "log.csv";
//	public String csvFile = "inputjava_data1.csv";
	
	@RequestMapping(value="/",method = RequestMethod.GET)
	public String index() {
		return "index";
	}
	
	@RequestMapping(value="/two",method = RequestMethod.GET)
	public String index2() {
		return "graph";
	}
	
	@RequestMapping(value="/log",method = RequestMethod.GET)
	public String log() {
		return "log";
	}
	
	@ResponseBody
	public String get_version() throws Exception{
		return tsps.get_version();
	}



	@RequestMapping("/api/job/start")
	@ResponseBody
	public String start(){
		boolean status = tsps.start();
		return String.valueOf(status);
	}
	
	@GetMapping("/api/job/buildNet")
	@ResponseBody
	public Response buildNet() throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.buildNet(csvFile, 2, 3);
//		netica.buildNet("log.csv", 2, 3);
		netica.finalize();
		return (new Response("Done", "Build OK"));
	}
	
	@RequestMapping("/api/job/getGraph")
	@ResponseBody
	public GraphResponse getGraph() throws Exception {
		setTarget = "";
		Pair<ArrayList<Vertice>, ArrayList<Edge>> pair = tsps.getEdgeList();
		GraphResponse response = new GraphResponse("Done", pair.first, pair.second);
		List<String> node_list = new ArrayList<String> ();
		List<String> edge_list = new ArrayList<String> ();
		for (Vertice it : pair.first) {
			node_list.add(it.toString());
		}
		for (Edge it : pair.second) {
			edge_list.add(it.toString());
		}
		Calendar date = new GregorianCalendar(2016, 6, 20, 12, 0, 0);
		tsps.getLogCount();
//		for (int i = 0; i < 100; i++) {
		SolrResults resulter = new SolrResults("http://10.4.45.114:19983/solr/rca");
		//http://10.4.45.114:19983/solr/index.html
		date.add(Calendar.DATE, 1);
		resulter.addResult(new Pair<String, Object> ("result_s", "bn"));
		resulter.addResult(new Pair<String, Object> ("bn_name_s", "bn_"+0));
		resulter.addResult(new Pair<String, Object> ("rs_timestamp", date.getTime()));
		resulter.addResult(new Pair<String, Object> ("timestamp_l", date.getTimeInMillis()));
		resulter.addResult(new Pair<String, Object> ("nodes_s", node_list.toString()));
		resulter.addResult(new Pair<String, Object> ("edges_s", edge_list.toString()));
		resulter.addResult(new Pair<String, Object> ("query_list", tsps.readSolr.urlList));
		resulter.write();
//		}
		return response;
	}
	
	@PostMapping("/api/job/setTarget")
	@ResponseBody
	public Response setTarget(@RequestBody PostItem post_item) throws Exception {
		setTarget = post_item.getItem();
		Response response = new Response("Done", setTarget);
		return response;
	}
	
	@PostMapping("/api/job/getPearson")
	@ResponseBody
	public Response getPearson(@RequestBody PostItem post_item) throws Exception {
//		List<Pair<String, Double>> res_list = new ArrayList<Pair<String, Double>>();
		
//		System.out.println(item);
		
		Pearson pearson = new Pearson(csvFile);
		
		Response response = new Response("Done", pearson.getRelationRanking(post_item.getItem()));
		return response;
	}
	
	@PostMapping("/api/job/getHistoryData")
	@ResponseBody
	public Response getHistoryData(@RequestBody PostItem post_item) throws Exception {
		
		Pearson pearson = new Pearson(csvFile);
		
		Response response = new Response("Done", pearson.getHistoryData(post_item.getItem()));
		return response;
	}
	
	@PostMapping("/api/job/getPerform")
	@ResponseBody
	public ResponseList getPerform(@RequestBody PostItem postItem) throws Exception {
		if (setTarget.equals("")) return (new ResponseList("No",(new ArrayList<Object>())));
		List<Pair<String, Double>> list = new OriginRootCause().run(postItem.getItem());
		
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		String[] names = new String[3];
		names[0] = "low";
		names[1] = "medium";
		names[2] = "high";
		List<Double> list2 = netica.getDistribution(setTarget);
		List<PieData> res = new ArrayList<PieData> ();
		for (int i = 0; i < 3; i++) {
			res.add(new PieData(list2.get(i), names[i]));
		}
		netica.finalize();
		ResponseList response = new ResponseList();
		if (setTarget.equals(postItem.getItem())) {
			response.setStatus("Done");
		}
		else {
			response.setStatus("Done1");
		}
		List<Object> tmp_list = new ArrayList<Object>();
		tmp_list.add(list);
		tmp_list.add(res);
		response.setData(tmp_list);
		return response;
	}
	
	@PostMapping("/api/job/Infer")
	@ResponseBody
	public Response getInfer(@RequestBody PostItem post_item) throws Exception {
		
		String[] str = post_item.getItem().split(":");
		
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		List<PieData> res = new ArrayList<PieData>();
		System.out.println(str[0] + " " + str[1]);
		
		List<Double> list = netica.getInfer(str[0], str[1], setTarget);
		String[] names = new String[3];
		names[0] = "low";
		names[1] = "medium";
		names[2] = "high";
		for (int i = 0; i < 3; i++) {
			res.add(new PieData(list.get(i), names[i]));
		}
		netica.finalize();
		return (new Response("Done", res));
	}
	
	@PostMapping("/api/job/originalDistribute")
	@ResponseBody
	public Response getOriDis(@RequestBody PostItem postItem) throws Exception {
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		String[] names = new String[3];
		names[0] = "low";
		names[1] = "medium";
		names[2] = "high";
		List<Double> list = netica.getDistribution(postItem.getItem());
		List<PieData> res = new ArrayList<PieData> ();
		for (int i = 0; i < 3; i++) {
			res.add(new PieData(list.get(i), names[i]));
		}
		netica.finalize();
		return (new Response("Done", res));
	}
	
	@GetMapping("/api/job/getAttr")
	@ResponseBody
	public Response getAttr() throws Exception {
		List<String> list = new ArrayList<String> ();
		NeticaApi netica = new NeticaApi();
		netica.loadNet();
		netica.loadRangeMap();
		for (String it : netica.rangeMap.keySet()) {
			list.add(it);
		}
		Response response = new Response("Done", list);
		netica.finalize();
		return response;
	}

	@PostMapping("/api/job/postInfer")
	@ResponseBody
	public Response postInfer(@RequestBody List<Inferance> infers) throws Exception {
		System.out.println(infers);
		
		double res = tsps.postInfer(infers);
		
		Response response = new Response("Done", res);
		return response;
	}
	
	@GetMapping("/api/job/TSP")
	@ResponseBody
	public Response getTSp() throws Exception {
		String[] names = new String[3];
		names[0] = "low";
		names[1] = "medium";
		names[2] = "high";
		List<Double> list = tsps.getTSP("test.csv", 100, setTarget);
		List<PieData> res = new ArrayList<PieData> ();
		for (int i = 0; i < 3; i++) {
			res.add(new PieData(list.get(i), names[i]));
		}
		Response response = new Response("Done", res);
		return response;
	}

	@GetMapping("/log/api/job/logList")
	@ResponseBody
	public Response getLogList() throws IOException {
		System.out.print("日志列表测试");
		return (new Response("Done", tsps.getLogCount()));
	}
	
	public static void main(String[] args) throws Exception {
		NeticaApi netica = new NeticaApi();
//		netica.buildNet("inputjava_data1.csv", 2, 3);
		netica.buildNet("log.csv", 2, 3);
		netica.finalize();
	}
}
