package cs2103.parameters;

public class Title implements Parameter {
	public static final String type = "TITLE";
	private final String title;
	
	public Title(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
