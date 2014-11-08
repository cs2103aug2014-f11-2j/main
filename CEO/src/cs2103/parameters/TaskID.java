//@author A0112673L
package cs2103.parameters;

import cs2103.exception.HandledException;
import cs2103.util.CommonUtil;

public class TaskID implements Parameter {
	public static final String type = "TASKID";
	private final int taskID;
	
	public TaskID(int taskID){
		this.taskID = taskID;
	}
	
	/**
	 * @return int value of taskID
	 */
	public int getValue() {
		return this.taskID;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param taskIDString
	 * @return TaskID object from String taskIDString, or null if taskIDString is null
	 * @throws HandledException
	 */
	public static TaskID parse(String taskIDString) throws HandledException {
		if (taskIDString == null) {
			return null;
		} else {
			return new TaskID(CommonUtil.parseIntegerParameter(taskIDString));
		}
	}
}
