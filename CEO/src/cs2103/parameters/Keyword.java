//@author A0112673L
package cs2103.parameters;

public class Keyword implements Parameter {
	public static final String type = "KEYWORD";
	private final String keyword;
	
	/**
	 * @param keyword
	 */
	public Keyword(String keyword) {
		this.keyword = keyword;
	}
	
	/**
	 * @return String value of Keyword keyword
	 */
	public String getValue() {
		return this.keyword;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param keyword
	 * @return Keyword object of string keyword, or null if keyword is null
	 */
	public static Keyword parse(String keyword) {
		if (keyword == null) {
			return null;
		} else {
			return new Keyword(keyword);
		}
	}
}
