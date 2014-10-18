package cs2103.parameters;

public class TaskType implements Parameter {
	public static final String type = "TASKTYPE";
	private final Value value;
	
	public static enum Value {
		ALL, FLOATING, DEADLINE, PERIODIC, INVALID;
	}
	
	public TaskType(Value value){
		this.value = value;
	}
	
	public Value getValue(){
		return this.value;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public static TaskType parse(String taskTypeString){
		return new TaskType(parseTaskType(taskTypeString));
	}
	
	private static Value parseTaskType(String taskTypeString){
		if (taskTypeString == null){
			return Value.INVALID;
		}
		if (taskTypeString.equalsIgnoreCase("all")){
			return Value.ALL;
		} else if (taskTypeString.equalsIgnoreCase("floating")){
			return Value.FLOATING;
		} else if (taskTypeString.equalsIgnoreCase("deadline")){
			return Value.DEADLINE;
		} else if (taskTypeString.equalsIgnoreCase("periodic")){
			return Value.PERIODIC;
		} else {
			return Value.INVALID;
		}
	}
}
