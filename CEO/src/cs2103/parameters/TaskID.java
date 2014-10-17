package cs2103.parameters;

public class TaskID implements Parameter {
	public static final String type = "TASKID";
	private final int taskID;
	
	public TaskID(int taskID){
		this.taskID = taskID;
	}
	
	public int getTaskID(){
		return this.taskID;
	}
	@Override
	public String getType() {
		return type;
	}

}
