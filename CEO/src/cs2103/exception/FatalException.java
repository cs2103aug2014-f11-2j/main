package cs2103.exception;


public class FatalException extends Exception {
	private static final long serialVersionUID = 2812393341801137882L;
	
	private static final String ILLEGAL_FILE = "The iCalendar file is not valid, please check your file";
	private static final String WRITE_ERROR = "Encounter error while writing to the iCalendar file";
	private static final String READ_ERROR = "Encounter error while reading the iCalendar file";
	private static final String NOT_INITIALIZED = "Program has not been successfully initialized";
	private final String errorMsg;
	
	public static enum ExceptionType{
		ILLEGAL_FILE, WRITE_ERROR, READ_ERROR, NOT_INITIALIZED;
	}
	
	public FatalException(ExceptionType exceptionType) {
		this.errorMsg = getErrorMsg(exceptionType);
		ErrorLogging log = ErrorLogging.getInstance();
		log.writeToLog(this.errorMsg, this);
	}
	
	public String printErrorMsg(){
		return this.errorMsg;
	}
	
	private String getErrorMsg(ExceptionType exceptionType){
		switch (exceptionType){
		case ILLEGAL_FILE:
			return ILLEGAL_FILE;
		case WRITE_ERROR:
			return WRITE_ERROR;
		case READ_ERROR:
			return READ_ERROR;
		case NOT_INITIALIZED:
			return NOT_INITIALIZED;
		default:
			return null;
		}
	}
}
