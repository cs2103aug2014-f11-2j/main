package cs2103;

public class FatalException extends Exception {
	private static final long serialVersionUID = 2812393341801137882L;
	public static enum ExceptionType{
		ILLEGAL_FILE, WRITE_ERROR, READ_ERROR;
	}
	public FatalException(ExceptionType exceptionType) {
		
	}
	
}
