package cs2103;

import java.util.ArrayList; 
import java.util.Date;
import java.util.Stack;

import edu.emory.mathcs.backport.java.util.Collections;
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
			task = new FloatingTask(null, null, title, false);
		} else if (endTime == null){
			task = new DeadlineTask(null, null, title, startTime, false);
		} else {
			task = new PeriodicTask(null, null, title, location, startTime, endTime, recurrence);
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
		Collections.sort(returnList, PeriodicTask.getComparator());
		return returnList;
	}
	
	public ArrayList<DeadlineTask> getDeadlineList(){
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (Task task:this.taskList){
			if (task instanceof DeadlineTask){
				returnList.add((DeadlineTask) task);
			}
		}
		Collections.sort(returnList, DeadlineTask.getComparator());
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
	
	public ArrayList<DeadlineTask> getAlertDeadlineList(){
		ArrayList<DeadlineTask> taskList = getDeadlineList();
		ArrayList<DeadlineTask> alertList = new ArrayList<DeadlineTask>();
		for (DeadlineTask task:taskList){
			if (task.checkAlert()){
				alertList.add(task);
			}
		}
		return alertList;
	}

	public ArrayList<PeriodicTask> getAlertPeriodicList(){
		ArrayList<PeriodicTask> taskList = getPeriodicList();
		ArrayList<PeriodicTask> alertList = new ArrayList<PeriodicTask>();
		for (PeriodicTask task:taskList){
			if (task.checkAlert()){
				alertList.add(task);
			}
		}
		return alertList;
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
			newTask = task.convert(time);
		} else {
			newTask = cloneTask(task);
		}
		newTask.updateTitle(title);
		newTask.updateDescription(description);
		newTask.updateLocation(location);
		if (completeFlag){
			newTask.updateComplete(complete);
		}
		if (recurFlag){
			newTask.updateRecurrence(recurrence);
		}
		backupTask(ActionType.UPDATE, task);
		this.taskList = storage.updateTask(newTask);
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
	
	public <T extends Task> ArrayList<Task> filterType(Class<T> type){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task.getClass() == type){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> filterTime(ArrayList<Task> searchList, Date[] time){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:searchList){
			if (task.checkPeriod(time)){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> filterComplete(ArrayList<Task> searchList, boolean complete){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for(Task task:searchList) {
			if(task instanceof FloatingTask) {
				if((((FloatingTask)task).getComplete())==complete) {
					returnList.add(task);
				}
			}
			if(task instanceof DeadlineTask) {
				if((((DeadlineTask)task).getComplete())==complete) {
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> filterKeyword(ArrayList<Task> searchList, String keywordString) {
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:searchList){
			if (task.matches(keywordString)){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public boolean updateTimeFromRecur(PeriodicTask task) throws HandledException, FatalException{
		DateTime now = new DateTime();
		if (task.getRecurrence() != null){
			if (task.getEndTime().before(now)){
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
