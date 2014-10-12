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
		printErrorMsg(exceptionType);
	}
	
	private void printErrorMsg(ExceptionType exceptionType){
		switch (exceptionType){
		case ILLEGAL_FILE:
			printErr(ILLEGAL_FILE);
			break;
		case WRITE_ERROR:
			printErr(WRITE_ERROR);
			break;
		case READ_ERROR:
			printErr(READ_ERROR);
			break;
		default:
			break;
		}
	}
	
	private void printErr(String errorMsg){
		System.err.println(errorMsg);
	}
}
