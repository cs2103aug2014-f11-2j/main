package cs2103;

import java.util.ArrayList; 
import java.text.ParseException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

//import net.fortuna.ical4j.model.Recur;

class CommandExecutor {
	private final StorageEngine storage;
	
	public CommandExecutor(String dataFile){
		this.storage = new StorageEngine(dataFile);
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
	
	public ArrayList<Task> getPeriodicList(){
		ArrayList<Task> taskList = storage.getTaskList();
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:taskList){
			if (task instanceof PeriodicTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getDeadlineList(){
		ArrayList<Task> taskList = storage.getTaskList();
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:taskList){
			if (task instanceof DeadlineTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getFloatingList(){
		ArrayList<Task> taskList = storage.getTaskList();
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:taskList){
			if (task instanceof FloatingTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getAllList(){
		return storage.getTaskList();
	}
	
	public Task showTaskDetail(int taskID) throws CEOException{
		return storage.getTaskByID(taskID);
	}
	
	public void deleteTask(int taskID) throws CEOException{
		storage.deleteTask(storage.getTaskByID(taskID));
	}
	
	public void updateTask(int taskID, String title, String description, String location, String complete, String startTime, String endTime) throws CEOException{
		Task task = storage.getTaskByID(taskID);
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
	}
}
