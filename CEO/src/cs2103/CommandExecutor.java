package cs2103;

import java.util.ArrayList; 
import java.text.ParseException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

//import net.fortuna.ical4j.model.Recur;

class CommandExecutor {
	private final StorageEngine storage;
	
	public CommandExecutor(String configFile){
		this.storage = new StorageEngine(configFile);
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
			throw new CEOException("Invalid time");
		}
		
	}
	
	public ArrayList<Task> listTask(String type){
		if (type.equalsIgnoreCase("PERIODIC")){
			return getPeriodicList();
		}else if (type.equalsIgnoreCase("DEADLINE")){
			return getDeadlineList();
		}else if (type.equalsIgnoreCase("FLOATING")){
			return getFloatingList();
		}else{
			return null;
		}
	}
	
	private ArrayList<Task> getPeriodicList(){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:storage.taskList){
			if (task instanceof PeriodicTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	private ArrayList<Task> getDeadlineList(){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:storage.taskList){
			if (task instanceof DeadlineTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	private ArrayList<Task> getFloatingList(){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:storage.taskList){
			if (task instanceof FloatingTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> listTask(){
		return storage.taskList;
	}
	
	public Task showTaskDetail(int taskID) throws CEOException{
		if (taskID>storage.taskList.size() || taskID < 1){
			throw new CEOException("Invalid TaskID");
		}
		return storage.taskList.get(taskID-1);
	}
	
	public void deleteTask(int taskID) throws CEOException{
		if (taskID>storage.taskList.size() || taskID < 1){
			throw new CEOException("Invalid TaskID");
		}
		storage.deleteTask(storage.taskList.get(taskID-1));
	}
	
	public void updateTask(int taskID, String title, String description, String location, String complete, String startTime, String endTime) throws CEOException{
		if (taskID>this.storage.taskList.size() || taskID < 1){
			throw new CEOException("Invalid TaskID");
		}
		Task task = storage.taskList.get(taskID-1);
		Task newTask;
		try{
			if (startTime==null && endTime==null){
				if (task instanceof FloatingTask){
					newTask = new FloatingTask(task.getTaskUID(),task.getTitle(),((FloatingTask)task).getComplete());
				}else if (task instanceof DeadlineTask){
					newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),((DeadlineTask)task).getDueTime(), ((DeadlineTask)task).getComplete());
				}else if (task instanceof PeriodicTask){
					newTask = new PeriodicTask(task.getTaskUID(),task.getTitle(),((PeriodicTask)task).getStartTime(), ((PeriodicTask)task).getEndTime());
				}else{
					throw new CEOException("Invalid Task object");
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
				throw new CEOException("Invalid Time");
			}
			newTask.updateLocation(task.getLocation());
			newTask.updateDescription(task.getDescription());
			if (title!=null){
				if (title.equals("")){
					newTask.updateTitle(title);
				}else{
					throw new CEOException("No Title Error");
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
			throw new CEOException("Invalid time");
		}
	}
	

}
