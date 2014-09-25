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
	
	public boolean updateTitle(int taskID, String title){
		return true;
	}
	
	public boolean updateProgress(int taskID, int progress){
		//progress: 0-->incomplete; 1-->in progress; 2-->completed
		return true;
	}
	
	public boolean updateDescription(int taskID, String description){
		return true;
	}
	
	public boolean updateLocation(int taskID, String location){
		return true;
	}
	
	public boolean updateCategory(int taskID, String category){
		return true;
	}
	
	public boolean updateTime(int taskID, String startTime, String endTime){
		return true;
	}
	
	public boolean updateRecurrence(int taskID, String recurrence){
		return true;
	}
	
	public boolean updateImportance(int taskID, int importance){
		return true;
	}
	
	public Date stringToDate(String timeString) throws ParseException{
		TimeZone tz=TimeZone.getDefault();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd/HH:mm");
		dateFormat.setTimeZone(tz);
		return dateFormat.parse(timeString);
	}
}
