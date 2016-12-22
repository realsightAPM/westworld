package apm.http;

import lombok.Data;


@Data
public class HttpData {
	public String part;//获取何种类型的监控数据
	public String message;
	
	public HttpData(String part,String message){
		this.message = message;
		this.part = part;
	}
}
