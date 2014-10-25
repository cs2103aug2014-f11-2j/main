package cs2103.parameters;

public class DeleteOption implements Parameter {
	public static final String type = "DELETEOPTION";
	public static final String[] allowedLiteral = {"P", "p", "permanent", "forever"};
	private final boolean permanent;
	
	public DeleteOption(boolean permanent){
		this.permanent = permanent;
	}
	
	public boolean getValue(){
		return this.permanent;
	}
	
	public static DeleteOption parse(String permanent){
		if (permanent == null){
			return null;
		} else {
			return new DeleteOption(true);
		}
		
	}
	
	@Override
	public String getType() {
		return type;
	}

}
