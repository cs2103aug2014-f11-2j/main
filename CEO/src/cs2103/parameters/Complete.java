//@author A0112673L
package cs2103.parameters;

import cs2103.exception.HandledException;

public class Complete implements Parameter {
	public static final String type = "COMPLETE";
	public static final String[] allowedLiteral = {"C", "complete", "status"};
	private final boolean complete;
	
	/**
	 * @param complete
	 */
	public Complete(boolean complete) {
		this.complete = complete;
	}
	
	/**
	 * @return boolean value of Complete object
	 */
	public boolean getValue() {
		return this.complete;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param completeString
	 * @return Complete object for String completeString, or null if completeString is null
	 * @throws HandledException
	 */
	public static Complete parse(String completeString) throws HandledException {
		if (completeString == null) {
			return null;
		} else {
			return new Complete(parseComplete(completeString));
		}
	}
	
	/**
	 * @param complete
	 * @return boolean value of String complete
	 * @throws HandledException
	 */
	private static boolean parseComplete(String complete) throws HandledException {
		if (complete.equals("")) {
			return true;
		} else if (complete.equalsIgnoreCase("true")) {
			return true;
		} else if (complete.equalsIgnoreCase("false")) {
			return false;
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_COMPLETE);
		}
	}
}
