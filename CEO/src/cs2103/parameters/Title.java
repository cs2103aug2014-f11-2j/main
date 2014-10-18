package cs2103.parameters;

public class Title implements Parameter {
	public static final String[] allowedLiteral = {"S", "title", "summary"};
	public static final String type = "TITLE";
	private final String title;
	
	public Title(String title){
		this.title = title;
	}
	
	public String getValue(){
		return this.title;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public static Title parse(String titleString){
		if (titleString == null){
			return null;
		} else {
			return new Title(titleString);
		}
	}
}
