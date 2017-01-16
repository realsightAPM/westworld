package apm.globalinfo;

public enum HttpMethod {
	GET("get"),
	POST("post");
	
	private final String name; 
	HttpMethod(String name){
		this.name = name;
	}
	public String getName() {	
		return name;
	}
}
