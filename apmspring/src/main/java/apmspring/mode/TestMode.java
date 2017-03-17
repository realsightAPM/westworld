package apmspring.mode;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class TestMode {
	
	private int id;
	private String name;
	private  Timestamp createTime;
}
