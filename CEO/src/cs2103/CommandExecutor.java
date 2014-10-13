package cs2103;

import java.util.ArrayList; 
import java.util.Date;
import java.util.Stack;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;

class CommandExecutor {
	private static CommandExecutor executor;
	private final StorageEngine storage;
	private ArrayList<Task> taskList;
	private Stack<TaskBackup> undoStack;
	private Stack<TaskBackup> redoStack;
	
	private static enum ActionType {
		ADD, DELETE, UPDATE;
	}
	
	private CommandExecutor(String dataFile) throws HandledException, FatalException{
		this.storage = StorageEngine.getInstance(dataFile);
		this.taskList = storage.getTaskList();
	}
	
	public static CommandExecutor getInstance(String dataFile) throws HandledException, FatalException{
		if (executor == null){
			executor = new CommandExecutor(dataFile);
		}
		return executor;
	}
	
	public void addTask(String title, String description, String location, Date startTime, Date endTime, Recur recurrence) throws HandledException, FatalException{
		Task task;
		if (startTime == null && endTime == null){
			task = new FloatingTask(null, title, false);
		} else if (endTime == null){
			task = new DeadlineTask(null, title, startTime, false);
		} else {
			task = new PeriodicTask(null, title, location, startTime, endTime, recurrence);
		}
		task.updateDescription(description);
		backupTask(ActionType.ADD, task);
		this.taskList = storage.updateTask(task);
	}
	
	public ArrayList<PeriodicTask> getPeriodicList(){
		ArrayList<PeriodicTask> returnList = new ArrayList<PeriodicTask>();
		for (Task task:this.taskList){
			if (task instanceof PeriodicTask){
				returnList.add((PeriodicTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<DeadlineTask> getDeadlineList(){
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (Task task:this.taskList){
			if (task instanceof DeadlineTask){
				returnList.add((DeadlineTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<FloatingTask> getFloatingList(){
		ArrayList<FloatingTask> returnList = new ArrayList<FloatingTask>();
		for (Task task:this.taskList){
			if (task instanceof FloatingTask){
				returnList.add((FloatingTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getAllList(){
		return this.taskList;
	}
	
	public void deleteTask(int taskID) throws HandledException, FatalException{
		Task task = getTaskByID(taskID);
		backupTask(ActionType.DELETE, task);
		this.taskList = storage.deleteTask(task);
	}
	
	public void updateTask(int taskID, String title, String description, String location, boolean complete, boolean completeFlag, Date[] time, boolean timeFlag, Recur recurrence, boolean recurFlag) throws HandledException, FatalException{
		Task task = getTaskByID(taskID);
		Task newTask;
		if (timeFlag){
			newTask = updateTaskType(task, time[0], time[1]);
		} else {
			newTask = cloneTask(task);
		}
		if (title != null){
			newTask.updateTitle(title);
		}
		if (description != null){
			newTask.updateDescription(description);
		}
		if (location != null){
			newTask.updateLocation(location);
		}
		if (completeFlag){
			newTask.updateComplete(complete);
		}
		if (recurFlag){
			newTask.updateRecurrence(recurrence);
		}
		backupTask(ActionType.UPDATE, task);
		this.taskList = storage.updateTask(newTask);
	}
	
	private Task updateTaskType(Task task, Date startTime, Date endTime) throws HandledException{
		if (startTime == null && endTime == null){
			return task.toFloating();
		} else if (endTime == null){
			return task.toDeadline(startTime);
		} else {
			return task.toPeriodic(startTime, endTime);
		}
	}
	
	private Task cloneTask(Task task) throws HandledException{
		try {
			Task newTask = (Task) task.clone();
			return newTask;
		} catch (CloneNotSupportedException e) {
			throw new HandledException(HandledException.ExceptionType.CLONE_FAILED);
		}
	}
	
	public int undoTasks(int count) throws HandledException, FatalException{
		if (this.undoStack == null) return 0;
		if (count < 1){
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		} else if (count > this.undoStack.size()){
			count = this.undoStack.size();
		}
		int i;
		for (i=0;i < count;i++){
			undoTask(this.undoStack.pop());
		}
		return i;
	}
	
	public int redoTasks(int count) throws HandledException, FatalException{
		if (this.redoStack == null) return 0;
		if (count < 1){
			throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		} else if (count > this.redoStack.size()){
			count = this.redoStack.size();
		}
		int i;
		for (i=0;i < count;i++){
			redoTask(this.redoStack.pop());
		}
		return i;
	}
	
	public boolean updateTimeFromRecur(PeriodicTask task) throws HandledException, FatalException{
		DateTime now = new DateTime();
		if (task.getRecurrence() != null){
			if (task.getStartTime().before(now)){
				Date startTime = (task.getRecurrence().getNextDate(new DateTime(task.getStartTime()), now));
				Date endTime = new Date(task.getEndTime().getTime() - task.getStartTime().getTime() + startTime.getTime());
				task.updateTime(startTime, endTime);
				this.taskList = storage.updateTask(task);
				return true;
			}
		}
		return false;
	}
	
	private void undoTask(TaskBackup taskBackup) throws HandledException, FatalException{
		if (this.redoStack == null) this.redoStack = new Stack<TaskBackup>();
		this.redoStack.push(taskBackup);
		switch(taskBackup.getActionType()){
		case ADD:
			this.taskList = storage.deleteTask(taskBackup.getTask());
			break;
		case UPDATE:
		case DELETE:
			this.taskList = storage.updateTask(taskBackup.getTask());
			break;
		default:
			throw new HandledException(HandledException.ExceptionType.UNEXPECTED_ERR);
		}
	}
	
	private void redoTask(TaskBackup taskBackup) throws HandledException, FatalException{
		if (this.undoStack == null) this.undoStack = new Stack<TaskBackup>();
		this.undoStack.push(taskBackup);
		switch(taskBackup.getActionType()){
		case DELETE:
			this.taskList = storage.deleteTask(taskBackup.getTask());
			break;
		case UPDATE:
		case ADD:
			this.taskList = storage.updateTask(taskBackup.getTask());
			break;
		default:
			throw new HandledException(HandledException.ExceptionType.UNEXPECTED_ERR);
		}
	}
	
	public Task getTaskByID(int taskID) throws HandledException{
		if (taskID > this.taskList.size() || taskID < 1){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASKID);
		} else {
			return this.taskList.get(taskID-1);
		}
	}
	
	private void backupTask(ActionType actionType, Task task){
		if (this.undoStack==null){
			this.undoStack = new Stack<TaskBackup>();
		}
		undoStack.push(new TaskBackup(actionType, task));
	}
	
	private final class TaskBackup{
		private final ActionType actionType;
		private final Task task;
		
		public TaskBackup(ActionType actionType, Task task){
			this.actionType = actionType;
			this.task = task;
		}
		
		public ActionType getActionType(){
			return this.actionType;
		}
		
		public Task getTask(){
			return this.task;
		}
	}
}
