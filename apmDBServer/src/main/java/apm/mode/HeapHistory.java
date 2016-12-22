package apm.mode;

import java.util.List;

import lombok.Data;

@Data
public class HeapHistory {
	public List<ClassInfo> classes;
	public List<ClassInfo> permGenClasses;
	private String time;
	private long totalHeapBytes;
	private long totalHeapInstances;
	private long totalPermGenBytes;
	private int totalPermgenInstances;
	private boolean sourceDisplayed;
	private boolean deltaDisplayed;
	
	
}
