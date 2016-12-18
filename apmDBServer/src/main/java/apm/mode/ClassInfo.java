package apm.mode;

import lombok.Data;

/*
 * 一个类的信息
 */
@Data
public class ClassInfo {
	private int instances;//又多少个实例
	private int bytes;//占内存多少字节
	private String jvmName;//
	private String name;
	private boolean perGen;
}
