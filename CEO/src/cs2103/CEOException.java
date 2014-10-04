package cs2103;

class CEOException extends Exception {
	private static final long serialVersionUID = 1L;
	public static final String INVALID_TASKID = "INVALID_TASKID";
	public static final String INVALID_TASK_OBJ = "INVALID_TASK_OBJECT";
	public static final String INVALID_TASK_TYPE = "INVALID_TASK_TYPE";
	public static final String INVALID_TIME = "INVALID_TIME";
	public static final String NO_TITLE = "NO_TITLE";
	public static final String ILLEGAL_FILE = "ILLEGAL_FILE";
	public static final String TASK_NOT_EXIST = "TASK_NOT_EXIST";
	public static final String WRITE_ERROR = "WRITE_ERROR";
	public static final String READ_ERROR = "READ_ERROR";
	public static final String INVALID_PARA = "INVALID_PARAMETER";
	public static final String INVALID_COMPLETE = "INVALID_COMPLETE";
	public static final String LESS_THAN_ONE_PARA = "LESS_THAN_ONE_PARAMETER";
	public static final String INVALID_RECUR = "INVALID_RECURRENCE";
	public CEOException(String errorMSG){
		if (errorMSG.equals(ILLEGAL_FILE)||errorMSG.equals(READ_ERROR)){
			System.exit(-1);
		}else if (errorMSG.equals(WRITE_ERROR)){
			System.exit(-1);
		}
	}
}
