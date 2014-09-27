package cs2103;

import java.util.ArrayList; 
import java.text.ParseException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

//import net.fortuna.ical4j.model.Recur;

class CommandExecutor {
	private final StorageEngine storage;
	private ArrayList<Task> taskList;
	
	public CommandExecutor(String configFile){
		this.storage = new StorageEngine(configFile);
		this.taskList = storage.getTaskList();
	}
	
	public boolean addTask(String title, String description, String location, String startTime, String endTime) throws CEOException, ParseException{
		Task task;
		if (startTime == null && endTime == null){
			task = new FloatingTask(null, title, false);
		}else if (endTime==null){
			task = new DeadlineTask(null, title, CommandParser.stringToDate(startTime));
		}else{
			task = new PeriodicTask(null, title, CommandParser.stringToDate(startTime), CommandParser.stringToDate(endTime));
		}
		task.updateDescription(description);
		task.updateLocation(location);
		storage.updateTask(task);
		return true;
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
		for (Task task:this.taskList){
			if (task instanceof PeriodicTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	private ArrayList<Task> getDeadlineList(){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof DeadlineTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	private ArrayList<Task> getFloatingList(){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.taskList){
			if (task instanceof FloatingTask){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> listTask(){
		return this.taskList;
	}
	
	public Task showTaskDetail(int taskID) throws CEOException{
		if (taskID>this.taskList.size() || taskID < 1){
			throw new CEOException("Invalid TaskID");
		}
		return this.taskList.get(taskID-1);
	}
	
	public void deleteTask(int taskID) throws CEOException{
		if (taskID>=this.taskList.size() || taskID < 1){
			throw new CEOException("Invalid TaskID");
		}
		storage.deleteTask(taskList.get(taskID-1));
	}
	
	public void updateTask(int taskID, String title, String description, String location, String complete, String startTime, String endTime) throws CEOException, ParseException{
		if (taskID>=this.taskList.size() || taskID < 1){
			throw new CEOException("Invalid TaskID");
		}
		Task task = taskList.get(taskID-1);
		Task newTask;
		if (startTime==null && endTime==null){
			if (task instanceof FloatingTask){
				newTask = new FloatingTask(task.getTaskUID(),task.getTitle(),((FloatingTask)task).getComplete());
			}else if (task instanceof DeadlineTask){
				newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),((DeadlineTask)task).getDueTime());
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
			newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),CommandParser.stringToDate(startTime));
		}else if ((!startTime.equals("")) && (!endTime.equals(""))){
			newTask = new PeriodicTask(task.getTaskUID(),task.getTitle(),CommandParser.stringToDate(startTime), CommandParser.stringToDate(endTime));
		}else{
			throw new CEOException("Invalid Time");
		}
		newTask.updateLocation(task.getLocation());
		newTask.updateDescription(task.getDescription());
		if (title!=null){
			if (title!=""){
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
		if (complete!=null && newTask instanceof FloatingTask){
			CommandParser.parseComplete(complete);
		}
		storage.updateTask(newTask);
	}
	
	
	/*
	public Recur stringToRecur(String recurrence) throws CEOException{
		Pattern p = Pattern.compile("([0-9]+)([hdwmy])([0-9]+)");
		Matcher m = p.matcher(recurrence);
		if(m.find()){
			int interval=Integer.parseInt(m.group(1));
			int count=Integer.parseInt(m.group(3));
			String frequency;
			String found=m.group(2);
			if (found.equals("h")){
				frequency=Recur.HOURLY;
			}else if (found.equals("d")){
				frequency=Recur.DAILY;
			}else if (found.equals("w")){
				frequency=Recur.WEEKLY;
			}else if (found.equals("m")){
				frequency=Recur.MONTHLY;
			}else if (found.equals("y")){
				frequency=Recur.YEARLY;
			}else{
				throw new CEOException("Invalid Recurrence");
			}
			Recur recur=new Recur(frequency, count);
			recur.setInterval(interval);
			return recur;
		}else if (recurrence.equals("0")){
			return null;
		}else{
			throw new CEOException("Invalid Recurrence");
		}
	}
	*/
}
