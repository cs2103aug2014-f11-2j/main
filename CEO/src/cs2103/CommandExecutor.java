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
	
	private CommandExecutor(String dataFile) throws CEOException{
		this.storage = StorageEngine.getInstance(dataFile);
		this.taskList = storage.getTaskList();
	}
	
	public static CommandExecutor getInstance(String dataFile) throws CEOException{
		if (executor == null){
			executor = new CommandExecutor(dataFile);
		}
		return executor;
	}
	
	public void addTask(String title, String description, String location, Date startTime, Date endTime, Recur recurrence) throws CEOException{
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
	
	public ArrayList<PeriodicTask> getPeriodicList() throws CEOException{
		ArrayList<PeriodicTask> returnList = new ArrayList<PeriodicTask>();
		for (Task task:this.taskList){
			if (task instanceof PeriodicTask){
				returnList.add((PeriodicTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<DeadlineTask> getDeadlineList() throws CEOException{
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (Task task:this.taskList){
			if (task instanceof DeadlineTask){
				returnList.add((DeadlineTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<FloatingTask> getFloatingList() throws CEOException{
		ArrayList<FloatingTask> returnList = new ArrayList<FloatingTask>();
		for (Task task:this.taskList){
			if (task instanceof FloatingTask){
				returnList.add((FloatingTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getAllList() throws CEOException{
		return this.taskList;
	}

	
	public Task showTaskDetail(int taskID) throws CEOException{
		return getTaskByID(taskID);
	}
	
	public void deleteTask(int taskID) throws CEOException{
		Task task = getTaskByID(taskID);
		backupTask(ActionType.DELETE, task);
		this.taskList = storage.deleteTask(task);
	}
	
	public void updateTask(int taskID, String title, String description, String location, boolean complete, boolean completeFlag, Date[] time, boolean timeFlag, Recur recurrence, boolean recurFlag) throws CEOException{
		Task task = getTaskByID(taskID);
		Task newTask;
		if (timeFlag){
			newTask = updateTaskType(task, time[0], time[1]);
		} else {
			try {
				newTask = (Task) task.clone();
			} catch (CloneNotSupportedException e) {
				throw new CEOException(CEOException.CLONE_FAILED);
			}
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
	
	private Task updateTaskType(Task task, Date startTime, Date endTime) throws CEOException{
		if (startTime == null && endTime == null){
			return task.toFloating();
		} else if (endTime == null){
			return task.toDeadline(startTime);
		} else {
			return task.toPeriodic(startTime, endTime);
		}
	}
	
	public int undoTasks(int count) throws CEOException{
		if (this.undoStack == null) return 0;
		if (count < 1){
			throw new CEOException(CEOException.INVALID_PARA);
		} else if (count > this.undoStack.size()){
			count = this.undoStack.size();
		}
		int i;
		for (i=0;i < count;i++){
			undoTask(this.undoStack.pop());
		}
		return i;
	}
	
	public int redoTasks(int count) throws CEOException{
		if (this.redoStack == null) return 0;
		if (count < 1){
			throw new CEOException(CEOException.INVALID_PARA);
		} else if (count > this.redoStack.size()){
			count = this.redoStack.size();
		}
		int i;
		for (i=0;i < count;i++){
			redoTask(this.redoStack.pop());
		}
		return i;
	}
	
	public boolean updateTimeFromRecur(PeriodicTask task) throws CEOException{
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
	
	private void undoTask(TaskBackup taskBackup) throws CEOException{
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
			throw new CEOException(CEOException.UNEXPECTED_ERR);
		}
	}
	
	private void redoTask(TaskBackup taskBackup) throws CEOException{
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
			throw new CEOException(CEOException.UNEXPECTED_ERR);
		}
	}
	
	private Task getTaskByID(int taskID) throws CEOException{
		if (taskID > this.taskList.size() || taskID < 1){
			throw new CEOException(CEOException.INVALID_TASKID);
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
