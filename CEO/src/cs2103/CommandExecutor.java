package cs2103;

import java.util.ArrayList; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
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
			task = new FloatingTask(null, title, "NEEDS-ACTION");
		}else if (endTime==null){
			task = new DeadlineTask(null, title, stringToDate(startTime));
		}else{
			task = new PeriodicTask(null, title, stringToDate(startTime), stringToDate(endTime));
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
	
	public Task showTaskDetail(int taskID){
		return this.taskList.get(taskID);
	}
	
	public boolean deleteTask(int taskID) throws CEOException{
		if (taskID>=this.taskList.size()){
			throw new CEOException("Invalid TaskID");
		}
		return storage.deleteTask(taskList.get(taskID));
	}
	
	public boolean updateTask(int taskID, String title, String description, String location, String progress, String startTime, String endTime) throws CEOException, ParseException{
		Task task = taskList.get(taskID);
		if (title!=null){
			if (title!=""){
				task.updateTitle(title);
			}else{
				throw new CEOException("No Title Error");
			}
		}
		if (description!=null){
			task.updateDescription(description);
		}
		if (location!=null){
			task.updateLocation(location);
		}
		if (startTime==null && endTime==null){
			if (task instanceof FloatingTask){
				if (progress!=null){
					((FloatingTask) task).updateProgress(progress);
				}
			}
		}else if (startTime.equals("") && endTime.equals("")){
			if (task instanceof FloatingTask){
				if (progress!=null){
					((FloatingTask) task).updateProgress(progress);
				}
			}else{
				Task newTask = new FloatingTask(task.getTaskUID(),task.getTitle(),"NEEDS-ACTION");
				newTask.updateDescription(task.getDescription());
				newTask.updateLocation(task.getLocation());
				task = newTask;
			}
		}else if (endTime==null || endTime.equals("")){
			if (task instanceof DeadlineTask){
				((DeadlineTask) task).updateDueTime(stringToDate(startTime));
			}else{
				Task newTask = new DeadlineTask(task.getTaskUID(),task.getTitle(),stringToDate(startTime));
				newTask.updateDescription(task.getDescription());
				newTask.updateLocation(task.getLocation());
				task = newTask;
			}
		}else if (startTime.equals("") || startTime==null){
			throw new CEOException("Invalid Time");
		}else{
			if (task instanceof PeriodicTask){
				((PeriodicTask) task).updateTime(stringToDate(startTime), stringToDate(endTime));
			}else{
				Task newTask = new PeriodicTask(task.getTaskUID(),task.getTitle(),stringToDate(startTime), stringToDate(endTime));
				newTask.updateDescription(task.getDescription());
				newTask.updateLocation(task.getLocation());
				task = newTask;
			}
		}
		return storage.updateTask(task);
	}
	
	public Date stringToDate(String timeString) throws ParseException{
		TimeZone tz=TimeZone.getDefault();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd/HH:mm");
		dateFormat.setTimeZone(tz);
		return dateFormat.parse(timeString);
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
