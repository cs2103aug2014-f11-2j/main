package cs2103.parameters;

public class Description implements Parameter {
	public static final String[] allowedLiteral = {"D", "description", "detail"};
	public static final String type = "DESCRIPTION";
	private final String description;
	
	public Description(String description){
		this.description = description;
	}
	
	public String getValue(){
		return this.description;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public static Description parse(String description){
		if (description == null){
			return null;
		} else {
			return new Description(description);
		}
	}
}
