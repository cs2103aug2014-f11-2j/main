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
	
	public ArrayList<Task> filterTime(Date[] time){
		//TODO @Chun Hui	
		ArrayList<Task> returnList = new ArrayList<Task>();
		// if time[0] and time[1] are both null, return getFloatingList()
		if(time[0]==null && time[1]==null){
			for (Task task:this.taskList){
				if (task instanceof FloatingTask){
					returnList.add((FloatingTask) task);
				}
			}
		}
		// if time[0] is not null and time[1] is null, get DeadlineList and return an arrayList of DeadlineTask items which dueTime is before time[0]
			else if(time[0]!=null && time[1]==null){
			for (Task task:this.taskList){
				if (task instanceof DeadlineTask){
					if(((DeadlineTask) task).getDueTime().compareTo(time[0])<0){
						returnList.add((DeadlineTask) task);
					}
				}
			}
		}
		// if time[0] and time[1] are both not null, get PeriodicList and return an arrayList of PeriodicTask items which startTime is between time[0] and time[1]
		else if(time[0]!=null && time[1]!=null){
			for (Task task:this.taskList){
				if (task instanceof PeriodicTask){
					if(((PeriodicTask) task).getStartTime().compareTo(time[0])>0){
						if(((PeriodicTask) task).getEndTime().compareTo(time[1])<0){
							returnList.add((PeriodicTask) task);
						}
					}
				}
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> filterTitle(ArrayList<Task> searchList, String titleKeyword){
		//TODO @Han
		ArrayList<Task> returnList = new ArrayList<Task>();
		//Go through each Task object in searchList, if the Task.getTitle() contains the titleKeyword, add into returnList
		return returnList;
	}
	
	public ArrayList<Task> filterDescription(ArrayList<Task> searchList, String descriptionKeyword){
		//TODO @Han
		ArrayList<Task> returnList = new ArrayList<Task>();
		//Go through each Task object in searchList, if the Task.getDescription() contains the descriptionKeyword, add into returnList
		return returnList;
	}
	
	public ArrayList<Task> filterLocation(ArrayList<Task> searchList, String locationKeyword){
		//TODO @Han
		ArrayList<Task> returnList = new ArrayList<Task>();
		//Go through each Task object in searchList, if the Task is an instance of PeriodicTask and Task.getLocation() contains the locationKeyword, add into returnList
		return returnList;
	}
	
	public ArrayList<Task> filterComplete(ArrayList<Task> searchList, boolean complete){
		//TODO @Han
		ArrayList<Task> returnList = new ArrayList<Task>();
		//Go through each Task object in searchList, if the Task is an instance of FloatingTask or DeadlineTask and Task.getComplete() matches the boolean complete, add into returnList
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
