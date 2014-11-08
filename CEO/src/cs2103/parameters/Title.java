//@author A0112673L
package cs2103.parameters;

public class Title implements Parameter {
	public static final String[] allowedLiteral = {"S", "title", "summary"};
	public static final String type = "TITLE";
	private final String title;
	
	/**
	 * @param title
	 */
	public Title(String title) {
		this.title = title;
	}
	
	/**
	 * @return String value of Title
	 */
	public String getValue() {
		return this.title;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param titleString
	 * @return Title object from String titleString, or null if titleString is null
	 */
	public static Title parse(String titleString) {
		if (titleString == null) {
			return null;
		} else {
			return new Title(titleString);
		}
	}
}
