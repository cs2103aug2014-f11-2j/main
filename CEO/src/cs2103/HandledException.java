package cs2103;

public class HandledException extends Exception {
	private static final long serialVersionUID = -6882947317576882981L;
	public static enum ExceptionType{
		INVALID_TASKID, INVALID_TASK_OBJ, INVALID_TASK_TYPE, INVALID_TIME, NO_TITLE, TASK_NOT_EXIST, INVALID_PARA, 
		INVALID_CMD, INVALID_COMPLETE, LESS_THAN_ONE_PARA, INVALID_RECUR, CLONE_FAILED, INVALID_UID, NETWORK_ERR, UNEXPECTED_ERR;
	}
	public HandledException(ExceptionType exceptionType){
		
	}
}
