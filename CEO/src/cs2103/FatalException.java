package cs2103;

public class FatalException extends Exception {
	private static final long serialVersionUID = 2812393341801137882L;
	
	private static final String ILLEGAL_FILE = "The iCalendar file is not valid, please check your file";
	private static final String WRITE_ERROR = "Encounter error while writing to the iCalendar file";
	private static final String READ_ERROR = "Encounter error while reading the iCalendar file";
	
	public static enum ExceptionType{
		ILLEGAL_FILE, WRITE_ERROR, READ_ERROR;
	}
	
	public FatalException(ExceptionType exceptionType) {
		String errorMsg = getErrorMsg(exceptionType);
		ErrorLogging log = ErrorLogging.getInstance();
		log.printErrorMsg(errorMsg);
		log.writeToLog(errorMsg, this);
	}
	
	private String getErrorMsg(ExceptionType exceptionType){
		switch (exceptionType){
		case ILLEGAL_FILE:
			return ILLEGAL_FILE;
		case WRITE_ERROR:
			return WRITE_ERROR;
		case READ_ERROR:
			return READ_ERROR;
		default:
			return null;
		}
	}
}
