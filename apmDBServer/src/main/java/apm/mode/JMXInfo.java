package apm.mode;

import lombok.Data;

@Data
public class JMXInfo {
	private String application;
	private String displayed;
	private String name;
	private boolean errorCounter;
	private String storageName;
	private String iconName;
	private String childCounterName;
}
