package apmspring.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import apmspring.mode.TestMode;
import apmspring.server.ServerDemo;

@Controller
public class DemoController {

	@Autowired
	private ServerDemo serverDemo;
	
	@RequestMapping("/gettestmessage")
	@ResponseBody
	public String getResult(){
		System.out.println("########");
		return "message";
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
