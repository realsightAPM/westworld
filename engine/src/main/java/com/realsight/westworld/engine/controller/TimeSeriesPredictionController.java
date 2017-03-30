package com.realsight.westworld.engine.controller;

import com.realsight.westworld.engine.service.TimeSeriesPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TimeSeriesPredictionController {
	
	@Autowired
	TimeSeriesPredictionService tsps;
	
	@RequestMapping("/api/job/version")
	@ResponseBody
	public String get_version(){
		return tsps.get_version();
	}



	@RequestMapping("/api/job/start")
	@ResponseBody
	public String start(){
		boolean status = tsps.start();
		return String.valueOf(status);
	}

}
