package cs2103;

import java.util.ArrayList; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;  
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
}
