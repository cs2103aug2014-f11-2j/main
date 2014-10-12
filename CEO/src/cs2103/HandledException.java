package cs2103;

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
	private static final String INVALID_RECUR = "Your input recurrence does not follow required pattern, please check your input and try again!";
	private static final String CLONE_FAILED = "Failed to clone task object";
	private static final String NETWORK_ERR = "Error occured in your network stack";
	private static final String UNEXPECTED_ERR = "An unexpected error occurred, operation failed";
	
	public static enum ExceptionType{
		INVALID_TASKID, INVALID_TASK_OBJ, INVALID_TASK_TYPE, INVALID_TIME, END_BEFORE_START, NO_TITLE, TASK_NOT_EXIST, INVALID_PARA, 
		INVALID_CMD, INVALID_COMPLETE, LESS_THAN_ONE_PARA, INVALID_RECUR, CLONE_FAILED, NETWORK_ERR, UNEXPECTED_ERR;
	}
	
	public HandledException(ExceptionType exceptionType){
		printErrorMsg(exceptionType);
		
	}
	
	private void printErrorMsg(ExceptionType exceptionType){
		switch (exceptionType){
		case INVALID_TASKID:
			printErr(INVALID_TASKID);
			break;
		case INVALID_TASK_OBJ:
			printErr(INVALID_TASK_OBJ);
			break;
		case INVALID_TASK_TYPE:
			printErr(INVALID_TASK_TYPE);
			break;
		case INVALID_TIME:
			printErr(INVALID_TIME);
			break;
		case END_BEFORE_START:
			printErr(END_BEFORE_START);
			break;
		case NO_TITLE:
			printErr(NO_TITLE);
			break;
		case TASK_NOT_EXIST:
			printErr(TASK_NOT_EXIST);
			break;
		case INVALID_PARA:
			printErr(INVALID_PARA);
			break;
		case INVALID_CMD:
			printErr(INVALID_CMD);
			break;
		case INVALID_COMPLETE:
			printErr(INVALID_COMPLETE);
			break;
		case LESS_THAN_ONE_PARA:
			printErr(LESS_THAN_ONE_PARA);
			break;
		case INVALID_RECUR:
			printErr(INVALID_RECUR);
			break;
		case CLONE_FAILED:
			printErr(CLONE_FAILED);
			break;
		case NETWORK_ERR:
			printErr(NETWORK_ERR);
			break;
		case UNEXPECTED_ERR:
			printErr(UNEXPECTED_ERR);
			break;
		default:
			break;
		}
	}
	
	private void printErr(String errorMsg){
		System.err.println(errorMsg);
	}
}
