package com.realsight.brain.simulation.globalinfo;

public enum WebRegex implements EnumInfo{
	DotJS("^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$"),
	PDF("pdf"),
	XML("xml");

	private final String name; 
	WebRegex(String name){
		this.name = name;
	}
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
