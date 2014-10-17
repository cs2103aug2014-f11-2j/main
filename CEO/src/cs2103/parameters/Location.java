package cs2103.parameters;

public class Location implements Parameter {
	public static final String type = "LOCATION";
	private final String location;
	
	public Location(String location){
		this.location = location;
	}
	
	public String getLocation(){
		return this.location;
	}
	
	@Override
	public String getType() {
		return type;
	}
}
