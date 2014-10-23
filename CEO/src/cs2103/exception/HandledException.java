package cs2103.exception;


public class HandledException extends Exception {
	private static final long serialVersionUID = -6882947317576882981L;
	
	private static final String INVALID_TASKID = "Your input task ID is not valid, please check your input and try again!";
	private static final String INVALID_TASK_OBJ = "An internal error occurred, operation failed.";
	private static final String INVALID_TASK_TYPE = "Your input task type is not valid, please check your input and try again!";
	private static final String INVALID_TIME = "Your input time cannot be parsed, please check your input and try again!";
	private static final String END_BEFORE_START = "Your end time is before start time, please check your input and try again";
	private static final String NO_TITLE = "A Non-empty title must be specified!";
	private static final String TASK_NOT_EXIST = "The task you intended to delete does not exist in the file.";
	private static final String INVALID_PARA = "Your input parameters are not valid, please check your input and try again!";
	private static final String INVALID_CMD = "Your input command contains error, please check your input and try again!";
	private static final String INVALID_COMPLETE = "Your input value for complete is invalid, please check your input and try again!";
	private static final String LESS_THAN_ONE_PARA = "You need to specify at least one parameter";
	private static final String CLONE_FAILED = "Failed to clone task object";
	private static final String NETWORK_ERR = "Error occured in your network stack";
	private static final String UNEXPECTED_ERR = "An unexpected error occurred, operation failed";
	private static final String LOGIN_FAIL = "Unable to get Credential from Google, Google Sync is disabled";
	private final String errorMsg;
	
	public static enum ExceptionType{
		INVALID_TASKID, INVALID_TASK_OBJ, INVALID_TASK_TYPE, INVALID_TIME, END_BEFORE_START, NO_TITLE, TASK_NOT_EXIST, INVALID_PARA, 
		INVALID_CMD, INVALID_COMPLETE, LESS_THAN_ONE_PARA, CLONE_FAILED, NETWORK_ERR, UNEXPECTED_ERR, LOGIN_FAIL;
	}
	
	public HandledException(ExceptionType exceptionType){
		this.errorMsg = getErrorMsg(exceptionType);
		ErrorLogging log = ErrorLogging.getInstance();
		log.writeToLog(this.errorMsg, this);
	}
	
	public String getErrorMsg(){
		return this.errorMsg;
	}
	
	private String getErrorMsg(ExceptionType exceptionType){
		switch (exceptionType){
		case INVALID_TASKID:
			return INVALID_TASKID;
		case INVALID_TASK_OBJ:
			return INVALID_TASK_OBJ;
		case INVALID_TASK_TYPE:
			return INVALID_TASK_TYPE;
		case INVALID_TIME:
			return INVALID_TIME;
		case END_BEFORE_START:
			return END_BEFORE_START;
		case NO_TITLE:
			return NO_TITLE;
		case TASK_NOT_EXIST:
			return TASK_NOT_EXIST;
		case INVALID_PARA:
			return INVALID_PARA;
		case INVALID_CMD:
			return INVALID_CMD;
		case INVALID_COMPLETE:
			return INVALID_COMPLETE;
		case LESS_THAN_ONE_PARA:
			return LESS_THAN_ONE_PARA;
		case CLONE_FAILED:
			return CLONE_FAILED;
		case NETWORK_ERR:
			return NETWORK_ERR;
		case UNEXPECTED_ERR:
			return UNEXPECTED_ERR;
		case LOGIN_FAIL:
			return LOGIN_FAIL;
		default:
			return null;
		}
	}
}
