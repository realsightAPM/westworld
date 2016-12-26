package apm.globalinfo;
/*
 * 获取哪一种监控数据
 */
public enum Part implements EnumInfo{
	HEAPHISTORY("heaphisto"),
	
	SYSTEMINFO("lastValue");
	private final String name;
	
	public String getName(){
		return this.name;
	}
	
	Part(String name){
		this.name = name;
	}
	
	public static Part PartofString(String partStr){
		Part[] parts = Part.values();
		for(Part part:parts){
			if(part.getName().equals(partStr)){
				return part;
			}
		}
		return null;
	}
}
