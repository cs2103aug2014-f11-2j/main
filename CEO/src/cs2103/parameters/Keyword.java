package cs2103.parameters;

public class Keyword implements Parameter {
	public static final String type = "KEYWORD";
	private final String keyword;
	
	public Keyword(String keyword){
		this.keyword = keyword;
	}
	
	public String getValue(){
		return this.keyword;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public static Keyword parse(String keyword){
		if (keyword == null){
			return null;
		} else {
			return new Keyword(keyword);
		}
	}
}
