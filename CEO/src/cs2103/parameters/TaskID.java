package cs2103.parameters;

import cs2103.CommonUtil;
import cs2103.exception.HandledException;

public class TaskID implements Parameter {
	public static final String type = "TASKID";
	private final int taskID;
	
	public TaskID(int taskID){
		this.taskID = taskID;
	}
	
	public int getValue(){
		return this.taskID;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public static TaskID parse(String taskIDString) throws HandledException{
		if (taskIDString == null){
			return null;
		} else {
			return new TaskID(CommonUtil.parseIntegerParameter(taskIDString));
		}
	}
}
