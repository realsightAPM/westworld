package apm.globalinfo;

public enum Period {
	JOUR("jour"),
	WEEK("semaine"),
	MONTH("mois"),
	YEAR("annee");
	
	
	Period(String name){
		this.name = name;
	}
	private final String name;
	
	public String getName(){
		return this.name;
	}
}
