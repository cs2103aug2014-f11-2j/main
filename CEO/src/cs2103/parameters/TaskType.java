//@author A0112673L
package cs2103.parameters;

public class TaskType implements Parameter {
	public static final String[] allowedLiteral = {"K", "type", "kind", "tasktype"};
	public static final String type = "TASKTYPE";
	private final Value value;
	
	public static enum Value {
		ALL, FLOATING, DEADLINE, PERIODIC, DEFAULT, TRASH, INVALID;
	}
	
	public TaskType(Value value) {
		this.value = value;
	}
	
	public Value getValue() {
		return this.value;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param taskTypeString
	 * @return TaskType object from String taskTypeString
	 */
	public static TaskType parse(String taskTypeString) {
		return new TaskType(parseTaskType(taskTypeString));
	}
	
	/**
	 * @param taskTypeString
	 * @return Value enumerator of taskTypeString
	 */
	private static Value parseTaskType(String taskTypeString) {
		if (taskTypeString == null || taskTypeString.isEmpty()) {
			return Value.DEFAULT;
		}
		if (taskTypeString.equalsIgnoreCase("all")) {
			return Value.ALL;
		} else if (taskTypeString.equalsIgnoreCase("floating")) {
			return Value.FLOATING;
		} else if (taskTypeString.equalsIgnoreCase("deadline")) {
			return Value.DEADLINE;
		} else if (taskTypeString.equalsIgnoreCase("periodic")) {
			return Value.PERIODIC;
		} else if (taskTypeString.equalsIgnoreCase("trash")) {
			return Value.TRASH;
		} else {
			return Value.INVALID;
		}
	}
}
