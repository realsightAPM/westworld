package com.realsight.westworld.engine.controller;

import com.realsight.westworld.bnanalysis.algorithm.Pearson;
import com.realsight.westworld.bnanalysis.basic.Pair;
import com.realsight.westworld.bnanalysis.service.NeticaApi;
import com.realsight.westworld.bnanalysis.service.OriginRootCause;
import com.realsight.westworld.engine.message.GraphResponse;
import com.realsight.westworld.engine.message.Response;
import com.realsight.westworld.engine.model.Edge;
import com.realsight.westworld.engine.model.Inferance;
import com.realsight.westworld.engine.model.Vertice;
import com.realsight.westworld.engine.service.TimeSeriesPredictionService;

import java.util.ArrayList;
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
	
	@RequestMapping(value="/",method = RequestMethod.GET)
	public String index() {
		return "index";
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
	
	@RequestMapping("/api/job/getGraph")
	@ResponseBody
	public GraphResponse getGraph() throws Exception {
		Pair<ArrayList<Vertice>, ArrayList<Edge>> pair = tsps.getEdgeList();
		GraphResponse response = new GraphResponse("Done", pair.first, pair.second);
		return response;
	}
	
	@RequestMapping("/api/job/getPearson")
	@ResponseBody
	public Response getPearson() throws Exception {
		List<Pair<String, Double>> res_list = new ArrayList<Pair<String, Double>>();
		
		Pearson pearson = new Pearson("inputjava_data1.csv");
		
		Response response = new Response("Done", pearson.getRelationRanking(1));
		return response;
	}
	
	@RequestMapping("/api/job/getCause")
	@ResponseBody
	public Response getCause() throws Exception {
		List<Pair<String, Double>> list = new OriginRootCause().run("http_times");
		Response response = new Response("Done", list);
		return response;
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
		double res = tsps.getTSP("test.csv", 100, "http_times");
		Response response = new Response("Done", res);
		return response;
	}
}
