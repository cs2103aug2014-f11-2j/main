package cs2103;

import java.util.ArrayList; 
import java.util.Date;
import java.util.Stack;
import net.fortuna.ical4j.model.Recur;


class CommandExecutor {
	private final StorageEngine storage;
	private ArrayList<Task> taskList;
	private Stack<TaskBackup> undoStack;
	
	public CommandExecutor(String dataFile){
		this.storage = new StorageEngine(dataFile);
		try {
			this.taskList = storage.getTaskList();
		} catch (CEOException e) {
			e.printStackTrace();
		}
	}
	
	public void addTask(String title, String description, String location, Date startTime, Date endTime, Recur recurrence) throws CEOException{
		Task task;
		if (startTime == null && endTime == null){
			task = new FloatingTask(null, title, false);
		}else if (endTime==null){
			task = new DeadlineTask(null, title, startTime, false);
		}else{
			task = new PeriodicTask(null, title, location, startTime, endTime, recurrence);
		}
		task.updateDescription(description);
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
	
	private Task getTaskByID(int taskID) throws CEOException{
		if (taskID > this.taskList.size() || taskID < 1){
			throw new CEOException(CEOException.INVALID_TASKID);
		}else{
			return this.taskList.get(taskID-1);
		}
	}
	
	public Task showTaskDetail(int taskID) throws CEOException{
		return getTaskByID(taskID);
	}
	
	public void deleteTask(int taskID) throws CEOException{
		this.taskList = storage.deleteTask(getTaskByID(taskID));
	}
	
	public void updateTask(int taskID, String title, String description, String location, boolean complete, boolean completeFlag, Date[] time, boolean timeFlag, Recur recurrence, boolean recurFlag) throws CEOException{
		Task task = getTaskByID(taskID);
		Task newTask;
		if (timeFlag){
			newTask = updateTaskType(task, time[0], time[1]);
		}else{
			newTask = copyTask(task);
		}
		if (title!=null){
			if (title.equals("")){
				throw new CEOException(CEOException.NO_TITLE);
			}else{
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
	}
	
	private Task copyTask(Task task) throws CEOException{
		if (task instanceof DeadlineTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(),((DeadlineTask)task).getDueTime(), ((DeadlineTask)task).getComplete());
		}else if (task instanceof FloatingTask){
			return new FloatingTask(task.getTaskUID(),task.getTitle(),((FloatingTask)task).getComplete());
		}else if (task instanceof PeriodicTask){
			return new PeriodicTask(task.getTaskUID(),task.getTitle(),((PeriodicTask)task).getLocation(), ((PeriodicTask)task).getStartTime(), ((PeriodicTask)task).getEndTime(), ((PeriodicTask)task).getRecurrence());
		}else{
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private Task updateTaskType(Task task, Date startTime, Date endTime) throws CEOException{
		if (startTime == null && endTime == null){
			return convertToFloating(task);
		}else if (endTime == null){
			return convertToDeadline(task, startTime);
		}else{
			return convertToPeriodic(task, startTime, endTime);
		}
	}
	
	private FloatingTask convertToFloating(Task task) throws CEOException{
		if (task instanceof FloatingTask){
			return new FloatingTask(task.getTaskUID(), task.getTitle(), ((FloatingTask)task).getComplete());
		}else if (task instanceof DeadlineTask){
			return new FloatingTask(task.getTaskUID(), task.getTitle(), ((DeadlineTask)task).getComplete());
		}else if (task instanceof PeriodicTask){
			return new FloatingTask(task.getTaskUID(), task.getTitle(), false);
		}else{
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private DeadlineTask convertToDeadline(Task task, Date startTime) throws CEOException{
		if (task instanceof FloatingTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(), startTime, ((FloatingTask)task).getComplete());
		}else if (task instanceof DeadlineTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(), startTime, ((DeadlineTask)task).getComplete());
		}else if (task instanceof PeriodicTask){
			return new DeadlineTask(task.getTaskUID(),task.getTitle(), startTime, false);
		}else{
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private PeriodicTask convertToPeriodic(Task task, Date startTime, Date endTime) throws CEOException{
		if (task instanceof FloatingTask || task instanceof DeadlineTask){
			return new PeriodicTask(task.getTaskUID(), task.getTitle(), null, startTime, endTime, null);
		}else if (task instanceof PeriodicTask){
			return new PeriodicTask(task.getTaskUID(), task.getTitle(), ((PeriodicTask) task).getLocation(), startTime, endTime, ((PeriodicTask) task).getRecurrence());
		}else{
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
		}else if (task instanceof DeadlineTask){
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
	
	private void backupTask(CommandLineUI.CommandType commandType, Task task){
		if (this.undoStack==null){
			this.undoStack = new Stack<TaskBackup>();
		}
		if (commandType.equals(CommandLineUI.CommandType.DELETE)){

		}
	}
	
	private final class TaskBackup{
		private CommandLineUI.CommandType commandType;
		private Task task;
		
		public TaskBackup(CommandLineUI.CommandType commandType, Task task){
			this.commandType = commandType;
			this.task = task;
		}
		
		public CommandLineUI.CommandType getCommandType(){
			return this.commandType;
		}
		
		public Task getBackupTask(){
			return this.task;
		}
	}
}
