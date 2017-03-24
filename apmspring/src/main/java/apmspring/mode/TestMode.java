package apmspring.mode;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class TestMode {
	
	private int id;
	private String name;
	private  Timestamp createTime;
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
}
