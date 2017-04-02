package com.realsight.westworld.engine.controller;

import com.realsight.westworld.engine.mode.TestMode;
import com.realsight.westworld.engine.server.RCAServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RCAController {
	
	@Autowired
    RCAServer rca;
	
	@RequestMapping("/createmetric")
	@ResponseBody
	public String createMetric(@RequestParam String metric){
		return rca.createMetric(metric);
	}
	
	@RequestMapping("/gettestuser")
	@ResponseBody
	public TestMode getUser(@RequestParam int id , @RequestParam String keywords){
		
		TestMode mode = new TestMode();
		mode.setId(10);
		mode.setName("wwwww");
		
		return mode;
		
	}
}
