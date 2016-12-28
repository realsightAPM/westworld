package apm.globalinfo;

public enum Save {
	SAVE(true),
	NOTSAVE(false);
	private boolean value;
	Save(boolean value){
		this.value = value;
	}
	
	boolean getValue(){
		return this.value;
	}
	
	
}
