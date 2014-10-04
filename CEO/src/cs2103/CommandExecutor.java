package cs2103;

import java.util.ArrayList; 
import java.util.Date;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
		}else if (recurrence==null){
			task = new PeriodicTask(null, title, location, startTime, endTime);
		}else{
			task = new RecurringTask(null, title, startTime, endTime, location, recurrence);
		}
		task.updateDescription(description);
		this.taskList = storage.updateTask(task);
	}
	
	public ArrayList<Task> getPeriodicList() throws CEOException{
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof PeriodicTask && !(task instanceof RecurringTask)){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getRecurringList(){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof RecurringTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getDeadlineList() throws CEOException{
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof DeadlineTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getFloatingList() throws CEOException{
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof FloatingTask && !(task instanceof DeadlineTask)){
				returnList.add(task);
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
		try{
			Task newTask = updateTaskType(task, startTime, endTime, recurrence);
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
			if (complete!=null){
				if(newTask instanceof FloatingTask){
					((FloatingTask) newTask).updateComplete(CommandParser.parseComplete(complete));
				}
			}
			if(newTask instanceof PeriodicTask){
				if (location!=null){
					((PeriodicTask) newTask).updateLocation(location);
				}
			}
			this.taskList = storage.updateTask(newTask);
		}catch (ParseException e){
			throw new CEOException(CEOException.INVALID_TIME);
		}
	}
	
	private Task updateTaskType(Task task, String startTime, String endTime, String recurrence) throws CEOException, ParseException{
		Task newTask;
		if (startTime==null && endTime==null && recurrence == null){
			if (task instanceof DeadlineTask){
				newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),((DeadlineTask)task).getDueTime(), ((DeadlineTask)task).getComplete());
			}else if (task instanceof FloatingTask){
				newTask = new FloatingTask(task.getTaskUID(),task.getTitle(),((FloatingTask)task).getComplete());
			}else if (task instanceof RecurringTask){
				newTask = new RecurringTask(task.getTaskUID(),task.getTitle(),((RecurringTask)task).getStartTime(), ((RecurringTask)task).getEndTime(), ((RecurringTask)task).getLocation(), ((RecurringTask)task).getRecurrence());
			}else if (task instanceof PeriodicTask){
				newTask = new PeriodicTask(task.getTaskUID(),task.getTitle(),((PeriodicTask)task).getStartTime(), ((PeriodicTask)task).getEndTime(), ((PeriodicTask)task).getLocation());
			}else{
				throw new CEOException(CEOException.INVALID_TASK_OBJ);
			}
		}else if (startTime.equals("") && endTime.equals("")){
			if (task instanceof FloatingTask){
				newTask = new FloatingTask(task.getTaskUID(),task.getTitle(),((FloatingTask)task).getComplete());
			}else{
				newTask = new FloatingTask(task.getTaskUID(),task.getTitle(),false);
			}
		}else if ((!startTime.equals("")) && endTime.equals("")){
			newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),stringToDate(startTime), false);
		}else if ((!startTime.equals("")) && (!endTime.equals(""))){
			if (recurrence == null){
				newTask = new PeriodicTask(task.getTaskUID(),task.getTitle(),stringToDate(startTime), stringToDate(endTime), null);
			}else{
				Recur recur = stringToRecur(recurrence);
				newTask = new RecurringTask(task.getTaskUID(),task.getTitle(),stringToDate(startTime), stringToDate(endTime), null, recur);
			}
		}else{
			throw new CEOException(CEOException.INVALID_TIME);
		}
		newTask.updateDescription(task.getDescription());
		return newTask;
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
