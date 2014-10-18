package cs2103.task;

import java.util.ArrayList;

import cs2103.StorageEngine;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import java.util.Collections;

public class TaskList {
	private static TaskList taskList;
	private final StorageEngine storage;
	private ArrayList<Task> tasks;
	
	private TaskList(String dataFile) throws HandledException, FatalException{
		this.storage = StorageEngine.getInstance(dataFile);
		this.tasks = this.storage.getTaskList();
	}
	
	public static TaskList getInstance(String dataFile) throws HandledException, FatalException{
		if (taskList == null){
			taskList = new TaskList(dataFile);
		}
		return taskList;
	}
	
	public static TaskList getInstance() throws FatalException{
		if (taskList == null){
			throw new FatalException(FatalException.ExceptionType.NOT_INITIALIZED);
		} else {
			return taskList;
		}
	}
	
	public boolean checkInitialized(){
		return this.tasks != null;
	}
	
	public ArrayList<PeriodicTask> getPeriodicList() throws HandledException, FatalException{
		ArrayList<PeriodicTask> returnList = new ArrayList<PeriodicTask>();
		for (Task task:this.tasks){
			if (task instanceof PeriodicTask){
				returnList.add((PeriodicTask) task);
			}
		}
		Collections.sort(returnList, PeriodicTask.getComparator());
		return returnList;
	}
	
	public ArrayList<DeadlineTask> getDeadlineList() throws HandledException, FatalException{
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (Task task:this.tasks){
			if (task instanceof DeadlineTask){
				returnList.add((DeadlineTask) task);
			}
		}
		Collections.sort(returnList, DeadlineTask.getComparator());
		return returnList;
	}
	
	public ArrayList<FloatingTask> getFloatingList() throws HandledException, FatalException{
		ArrayList<Task> allList = getAllList();
		ArrayList<FloatingTask> returnList = new ArrayList<FloatingTask>();
		for (Task task:allList){
			if (task instanceof FloatingTask){
				returnList.add((FloatingTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getAllList() throws HandledException, FatalException{
		return this.tasks;
	}
	
	public Task getTaskByID(int taskID) throws HandledException, FatalException{
		if (taskID > this.tasks.size() || taskID < 1){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASKID);
		} else {
			return this.tasks.get(taskID-1);
		}
	}
	
	public void addTask(Task task) throws HandledException, FatalException{
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
	}
	
	public void updateTask(Task task) throws HandledException, FatalException{
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
	}
	
	public void deleteTask(Task task) throws HandledException, FatalException{
		this.storage.deleteTask(task);
		this.tasks = this.storage.getTaskList();
	}
}
