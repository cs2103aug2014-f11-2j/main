package cs2103;

import java.util.ArrayList; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;  
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.ical4j.model.Recur;

class CommandExecutor {
	private final StorageEngine storage;
	private ArrayList<Task> taskList;
	
	public CommandExecutor(String configFile){
		this.storage = new StorageEngine(configFile);
		this.taskList = storage.getTaskList();
	}
	
	public boolean addTask(String title, String description, String location, 
			String category, String recurrence,  int importance, String startTime, String endTime){
		//You need to parse the time from String to Date
		return true;
	}
	
	public ArrayList<Task> listTask(int type){
		//type: 0-->floating; 1-->deadline; 2-->periodic
		return this.taskList;
	}
	
	public ArrayList<Task> listTask(){
		//Return all tasks
		return this.taskList;
	}
	
	public Task showTaskDetail(int taskID){
		//
		return this.taskList.get(taskID);
	}
	
	public boolean deleteTask(int taskID){
		//
		taskList.remove(taskID);
		return true;
	}
	public boolean updateTask(int taskID, String title, String description, String location, 
			String category, String recurrence,  int importance, String startTime, String endTime, boolean updateTimeFlag){
		
		return true;
	}
	
	public Date stringToDate(String timeString) throws ParseException{
		TimeZone tz=TimeZone.getDefault();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd/HH:mm");
		dateFormat.setTimeZone(tz);
		return dateFormat.parse(timeString);
	}
	
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
}
