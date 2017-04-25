package com.realsight.westworld.engine.controller;

import com.basic.Pair;
import com.bnAnalysis.NeticaApi;
import com.realsight.westworld.engine.Message.GraphResponse;
import com.realsight.westworld.engine.Message.Response;
import com.realsight.westworld.engine.model.Edge;
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

}
