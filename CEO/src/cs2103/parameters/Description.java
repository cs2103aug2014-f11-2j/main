package cs2103.parameters;

public class Description implements Parameter {
	public static final String type = "DESCRIPTION";
	private final String description;
	
	public Description(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
