//@author A0116713M
package cs2103.exception;

import cs2103.util.Logger;


public class HandledException extends Exception {
	private static final long serialVersionUID = -6882947317576882981L;
	
	private static final String INVALID_TASKID = "Your input task ID is not valid, please check your input and try again!";
	private static final String INVALID_TASK_OBJ = "An internal error occurred, operation failed.";
	private static final String INVALID_TASK_TYPE = "Your input task type is not valid, please check your input and try again!";
	private static final String INVALID_TIME = "Your input time cannot be parsed, please check your input and try again!";
	private static final String END_BEFORE_START = "Your end time is before start time, please check your input and try again";
	private static final String TASK_NOT_EXIST = "The task you intended to delete does not exist in the file.";
	private static final String INVALID_PARA = "Your input parameters are not valid, please check your input and try again!";
	private static final String INVALID_CMD = "Your input command contains error, please check your input and try again!";
	private static final String INVALID_COMPLETE = "Your input value for complete is invalid, please check your input and try again!";
	private static final String LESS_THAN_ONE_PARA = "You need to specify at least one parameter";
	private static final String CLONE_FAILED = "Failed to clone task object";
	private static final String UNEXPECTED_ERR = "An unexpected error occurred, operation failed";
	private static final String LOGIN_FAIL = "Unable to get Credential from Google, Google Sync is disabled";
	private static final String SYNC_FAIL = "Unable to sync your data with Google, Google Sync is disabled";
	private static final String NOT_DELETED = "The task you are trying to restore is not in the trash bin!";
	private static final String NO_TITLE = "You must specify a title for the task!";
	private final String errorMsg;
	
	public static enum ExceptionType{
		INVALID_TASKID, INVALID_TASK_OBJ, INVALID_TASK_TYPE, INVALID_TIME, END_BEFORE_START, TASK_NOT_EXIST, INVALID_PARA, 
		INVALID_CMD, INVALID_COMPLETE, LESS_THAN_ONE_PARA, CLONE_FAILED, UNEXPECTED_ERR, LOGIN_FAIL, SYNC_FAIL, NOT_DELETED, NO_TITLE;
	}
	
	public HandledException(ExceptionType exceptionType){
		this.errorMsg = getErrorMsg(exceptionType);
		Logger log = Logger.getInstance();
		log.writeErrLog(this.errorMsg, this);
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
		case UNEXPECTED_ERR:
			return UNEXPECTED_ERR;
		case LOGIN_FAIL:
			return LOGIN_FAIL;
		case SYNC_FAIL:
			return SYNC_FAIL;
		case NOT_DELETED:
			return NOT_DELETED;
		case NO_TITLE:
			return NO_TITLE;
		default:
			return null;
		}
	}
}
