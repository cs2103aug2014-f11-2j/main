//@author A0112673L
package cs2103.parameters;

public class Description implements Parameter {
	public static final String[] allowedLiteral = {"D", "description", "detail"};
	public static final String type = "DESCRIPTION";
	private final String description;
	
	/**
	 * @param description
	 */
	public Description(String description) {
		this.description = description;
	}
	
	/**
	 * @return String description of Description
	 */
	public String getValue() {
		return this.description;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param description
	 * @return Description object of string description, or null if description is null
	 */
	public static Description parse(String description) {
		if (description == null) {
			return null;
		} else {
			return new Description(description);
		}
	}
}
