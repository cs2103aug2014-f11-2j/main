package cs2103;

import java.util.ArrayList; 
import java.util.Stack;
import java.text.ParseException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

//import net.fortuna.ical4j.model.Recur;

class CommandExecutor {
	private final StorageEngine storage;
	private ArrayList<Task> taskList;
	private Stack<TaskBackup> undoStack;
	private ArrayList<TaskBackup> trashList;
	
	public CommandExecutor(String dataFile){
		this.storage = new StorageEngine(dataFile);
		try {
			this.taskList = storage.getTaskList();
		} catch (CEOException e) {
			e.printStackTrace();
		}
	}
	
	public void addTask(String title, String description, String location, String startTime, String endTime) throws CEOException{
		try{
			Task task;
			if (startTime == null && endTime == null){
				task = new FloatingTask(null, title, false);
			}else if (endTime==null){
				task = new DeadlineTask(null, title, CommandParser.stringToDate(startTime), false);
			}else{
				task = new PeriodicTask(null, title, CommandParser.stringToDate(startTime), CommandParser.stringToDate(endTime));
			}
			task.updateDescription(description);
			task.updateLocation(location);
			storage.updateTask(task);
		}catch(ParseException e){
			throw new CEOException(CEOException.INVALID_TIME);
		}
		
	}
	
	public ArrayList<Task> getPeriodicList() throws CEOException{
		this.taskList = storage.getTaskList();
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof PeriodicTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getDeadlineList() throws CEOException{
		this.taskList = storage.getTaskList();
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof DeadlineTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getFloatingList() throws CEOException{
		this.taskList = storage.getTaskList();
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof FloatingTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getAllList() throws CEOException{
		this.taskList = storage.getTaskList();
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
		storage.deleteTask(getTaskByID(taskID));
	}
	
	public void updateTask(int taskID, String title, String description, String location, String complete, String startTime, String endTime) throws CEOException{
		Task task = getTaskByID(taskID);
		try{
			Task newTask = updateTaskType(task, startTime, endTime);
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
			if (location!=null){
				newTask.updateLocation(location);
			}
			if (complete!=null){
				if(newTask instanceof FloatingTask){
					((FloatingTask) newTask).updateComplete(CommandParser.parseComplete(complete));
				}else if (newTask instanceof DeadlineTask){
					((DeadlineTask) newTask).updateComplete(CommandParser.parseComplete(complete));
				}
			}
			storage.updateTask(newTask);
		}catch (ParseException e){
			throw new CEOException(CEOException.INVALID_TIME);
		}
	}
	
	private Task updateTaskType(Task task, String startTime, String endTime) throws CEOException, ParseException{
		Task newTask;
		if (startTime==null && endTime==null){
			if (task instanceof FloatingTask){
				newTask = new FloatingTask(task.getTaskUID(),task.getTitle(),((FloatingTask)task).getComplete());
			}else if (task instanceof DeadlineTask){
				newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),((DeadlineTask)task).getDueTime(), ((DeadlineTask)task).getComplete());
			}else if (task instanceof PeriodicTask){
				newTask = new PeriodicTask(task.getTaskUID(),task.getTitle(),((PeriodicTask)task).getStartTime(), ((PeriodicTask)task).getEndTime());
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
			newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),CommandParser.stringToDate(startTime), false);
		}else if ((!startTime.equals("")) && (!endTime.equals(""))){
			newTask = new PeriodicTask(task.getTaskUID(),task.getTitle(),CommandParser.stringToDate(startTime), CommandParser.stringToDate(endTime));
		}else{
			throw new CEOException(CEOException.INVALID_TIME);
		}
		newTask.updateLocation(task.getLocation());
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
	
	private void moveToTrash(Task task){
		if (this.trashList==null){
			this.trashList = new ArrayList<TaskBackup>();
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
		
		public String getTaskUID(){
			return this.task.getTaskUID();
		}
		
		@Override
		public boolean equals(Object o){
			if (o==null) return false;
			if (o instanceof TaskBackup && this.getCommandType().equals(CommandLineUI.CommandType.DELETE)){
				TaskBackup other = (TaskBackup) o;
				if (other.getCommandType().equals(CommandLineUI.CommandType.DELETE) && this.getTaskUID().equals(other.getTaskUID())){
					return true;
				}
			}
			return false;
		}
	}
}
