package apm.mode;

import java.util.List;

import lombok.Data;

@Data
public class HeapHistory {
	private List<ClassInfo> classesList;
	private List<ClassInfo> perGenClassesList;
	private String time;
	private long totalHeapBytes;
	private long totalHeapInstances;
	private long totalPermGenBytes;
	private int totalPermgenInstances;
	private boolean sourceDisplayed;
	private boolean deltaDisplayed;
}
