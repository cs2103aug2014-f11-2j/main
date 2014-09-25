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
	
	public boolean addTask(String title, String description, String location, String category, 
			String recurrence,  int importance, String startTime, String endTime) throws ParseException, CEOException{
		//You need to parse the time from String to Date
		
		Date start = stringToDate(startTime);
		Date end = stringToDate(endTime);
		this.getTaskList().clear();
		
		Task task = new Task(title, description, location, category, recurrence, importance, start, end);
		//pull all task into arrayList
		this.getTaskList().add(task);
		//push taskList into storage engine, get boolean result
		return true;
	}
	
	public ArrayList<Task> listTask(int type){
		//type: 0-->floating; 1-->deadline; 2-->periodic
		
		this.getTaskList().clear();
		//pull task of type from storage engine into arrayList
		return this.getTaskList();
	}
	

	public Task showTaskDetail(int taskID){
		//
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		return this.getTaskList().get(taskID);
	}
	
	public boolean deleteTask(int taskID){
		//
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		this.getTaskList().remove(taskID);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateTitle(int taskID, String title) throws CEOException{
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList 
		Task task = this.getTaskList().get(taskID);
		task.updateTitle(title);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateProgress(int taskID, int progress){
		//progress: 0-->incomplete; 1-->in progress; 2-->completed
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateImportance(progress);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateDescription(int taskID, String description){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateDescription(description);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateLocation(int taskID, String location){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateLocation(location);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateCategory(int taskID, String category){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateCategory(category);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateTime(int taskID, String startTime, String endTime) throws ParseException{
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		Date start = stringToDate(startTime);
		Date end = stringToDate(endTime);
		task.updateTime(start, end);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateRecurrence(int taskID, String recurrence){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateRecurrence(recurrence);
		//push arrayList into storage
		return true;
	}
	
	public boolean updateImportance(int taskID, int importance){
		this.getTaskList().clear();
		//pull all existing tasks from storage engine into arrayList
		Task task = this.getTaskList().get(taskID);
		task.updateImportance(importance);
		//push arrayList into storage
		return true;
	}
	
	public Date stringToDate(String timeString) throws ParseException{
		TimeZone tz=TimeZone.getDefault();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd/HH:mm");
		dateFormat.setTimeZone(tz);
		return dateFormat.parse(timeString);
	}
	
	public ArrayList<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(ArrayList<Task> taskList) {
		this.taskList = taskList;
	}
}
