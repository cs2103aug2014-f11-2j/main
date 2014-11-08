//@author A0112673L
package cs2103.parameters;

public class Location implements Parameter {
	public static final String[] allowedLiteral = {"L", "location", "place"};
	public static final String type = "LOCATION";
	private final String location;
	
	public Location(String location) {
		this.location = location;
	}
	
	public String getValue() {
		return this.location;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param location
	 * @return Location option from String location, or null if location is null
	 */
	public static Location parse(String location) {
		if (location == null) {
			return null;
		} else {
			return new Location(location);
		}
	}
}
