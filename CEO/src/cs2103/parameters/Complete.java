package cs2103.parameters;

public class Complete implements Parameter {
	public static final String type = "COMPLETE";
	private final boolean complete;
	
	public Complete(boolean complete){
		this.complete = complete;
	}
	
	public boolean getComplete(){
		return this.complete;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
