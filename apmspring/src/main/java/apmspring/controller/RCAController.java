package apmspring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import apmspring.mode.TestMode;
import apmspring.server.RCAServer;

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
	public TestMode getUser(@RequestParam int id ,@RequestParam String keywords){
		
		TestMode mode = new TestMode();
		mode.setId(10);
		mode.setName("wwwww");
		
		return mode;
		
	}
}
