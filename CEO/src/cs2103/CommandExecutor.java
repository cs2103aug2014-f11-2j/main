package cs2103;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList; 
import java.util.Date;
import java.util.Stack;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.SimpleHostInfo;
import net.fortuna.ical4j.util.UidGenerator;


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
	
<<<<<<< HEAD
	public boolean addTask(String title, String description, String location, String category, 
			String recurrence,  int importance, String startTime, String endTime) throws ParseException, CEOException{
		//You need to parse the time from String to Date
		
		Date start = stringToDate(startTime);
		Date end = stringToDate(endTime);
		this.getTaskList().clear();
		
		Task task = new Task(title, description, location, category, recurrence, importance, start, end);
		//pull all task into arrayList
		this.getTaskList().add(task);
		//push taskList into storage engine, get boolean result
		return true;
	}
	
	public ArrayList<Task> listTask(int type){
		//type: 0-->floating; 1-->deadline; 2-->periodic
		
		this.getTaskList().clear();
		//pull task of type from storage engine into arrayList
		return this.getTaskList();
=======
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
		task.updateTaskUID(generateUid());
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
>>>>>>> master
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
			newTask = copyTask(task);
		}
		if (title!=null){
			if (title.equals("")){
				throw new CEOException(CEOException.NO_TITLE);
			} else {
				newTask.updateTitle(title);
			}
		}
		if (description!=null){
			newTask.updateDescription(description);
		}
		newTask = updateLocation(newTask, location);
		if (completeFlag){
			newTask = updateComplete(newTask, complete);
		}
		if (recurFlag){
			newTask = updateRecur(newTask, recurrence);
		}
		backupTask(ActionType.UPDATE, task);
		this.taskList = storage.updateTask(newTask);
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
	
	private Task copyTask(Task task) throws CEOException{
		if (task instanceof DeadlineTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(),((DeadlineTask)task).getDueTime(), ((DeadlineTask)task).getComplete());
		} else if (task instanceof FloatingTask){
			return new FloatingTask(task.getTaskUID(),task.getTitle(),((FloatingTask)task).getComplete());
		} else if (task instanceof PeriodicTask){
			return new PeriodicTask(task.getTaskUID(),task.getTitle(),((PeriodicTask)task).getLocation(), ((PeriodicTask)task).getStartTime(), ((PeriodicTask)task).getEndTime(), ((PeriodicTask)task).getRecurrence());
		} else {
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private Task updateTaskType(Task task, Date startTime, Date endTime) throws CEOException{
		if (startTime == null && endTime == null){
			return convertToFloating(task);
		} else if (endTime == null){
			return convertToDeadline(task, startTime);
		} else {
			return convertToPeriodic(task, startTime, endTime);
		}
	}
	
<<<<<<< HEAD

	public Task showTaskDetail(int taskID){
		//
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		return this.getTaskList().get(taskID);
	}
	
	public boolean deleteTask(int taskID){
		//
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		this.getTaskList().remove(taskID);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateTitle(int taskID, String title) throws CEOException{
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList 
		Task task = this.getTaskList().get(taskID);
		task.updateTitle(title);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateProgress(int taskID, int progress){
		//progress: 0-->incomplete; 1-->in progress; 2-->completed
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateImportance(progress);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateDescription(int taskID, String description){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateDescription(description);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateLocation(int taskID, String location){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateLocation(location);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateCategory(int taskID, String category){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateCategory(category);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateTime(int taskID, String startTime, String endTime) throws ParseException{
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		Date start = stringToDate(startTime);
		Date end = stringToDate(endTime);
		task.updateTime(start, end);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateRecurrence(int taskID, String recurrence){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateRecurrence(recurrence);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateImportance(int taskID, int importance){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateImportance(importance);
		//push arrayList into storage
		return true;
=======
	private FloatingTask convertToFloating(Task task) throws CEOException{
		if (task instanceof FloatingTask){
			return new FloatingTask(task.getTaskUID(), task.getTitle(), ((FloatingTask)task).getComplete());
		} else if (task instanceof DeadlineTask){
			return new FloatingTask(task.getTaskUID(), task.getTitle(), ((DeadlineTask)task).getComplete());
		} else if (task instanceof PeriodicTask){
			return new FloatingTask(task.getTaskUID(), task.getTitle(), false);
		} else {
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private DeadlineTask convertToDeadline(Task task, Date startTime) throws CEOException{
		if (task instanceof FloatingTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(), startTime, ((FloatingTask)task).getComplete());
		} else if (task instanceof DeadlineTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(), startTime, ((DeadlineTask)task).getComplete());
		} else if (task instanceof PeriodicTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(), startTime, false);
		} else {
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private PeriodicTask convertToPeriodic(Task task, Date startTime, Date endTime) throws CEOException{
		if (task instanceof FloatingTask || task instanceof DeadlineTask){
			return new PeriodicTask(task.getTaskUID(), task.getTitle(), null, startTime, endTime, null);
		} else if (task instanceof PeriodicTask){
			return new PeriodicTask(task.getTaskUID(), task.getTitle(), ((PeriodicTask) task).getLocation(), startTime, endTime, ((PeriodicTask) task).getRecurrence());
		} else {
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private Task updateLocation(Task task, String location) throws CEOException{
		if (task instanceof PeriodicTask){
			if (location != null){
				((PeriodicTask) task).updateLocation(location);
			}
		}
		return task;
	}
	
	private Task updateComplete(Task task, boolean complete){
		if (task instanceof FloatingTask){
			((FloatingTask) task).updateComplete(complete);
		} else if (task instanceof DeadlineTask){
			((DeadlineTask) task).updateComplete(complete);
		}
		return task;
	}
	
	private Task updateRecur(Task task, Recur recur){
		if (task instanceof PeriodicTask){
			((PeriodicTask) task).updateRecurrence(recur);
		}
		return task;
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

	private Uid generateUid() throws CEOException{
		try {
			UidGenerator ug = new UidGenerator(new SimpleHostInfo("gmail.com"), InetAddress.getLocalHost().getHostName().toString());
			return ug.generateUid();
		} catch (UnknownHostException e) {
			throw new CEOException(CEOException.UNEXPECTED_ERR);
		}
		
	}
	
	private void backupTask(ActionType actionType, Task task){
		if (this.undoStack==null){
			this.undoStack = new Stack<TaskBackup>();
		}
		undoStack.push(new TaskBackup(actionType, task));
>>>>>>> master
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
	
	public ArrayList<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(ArrayList<Task> taskList) {
		this.taskList = taskList;
	}
}
