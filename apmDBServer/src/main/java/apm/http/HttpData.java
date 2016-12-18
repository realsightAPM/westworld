package apm.http;

import lombok.Data;


@Data
class HttpData {
	String part;//获取何种类型的监控数据
	String message;
	
	public HttpData(String part,String message){
		this.message = message;
		this.part = part;
	}
}
