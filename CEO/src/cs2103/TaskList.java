package cs2103;

import java.io.File;
import java.util.ArrayList;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.StorageInterface;
import cs2103.storage.StorageEngine;
import cs2103.storage.StorageStub;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;

import java.util.Collections;

public class TaskList {
	private static TaskList taskList;
	private final StorageInterface storage;
	private final StorageInterface trash;
	private final File dataFile;
	private final File trashFile;
	private boolean enableSync;
	private ArrayList<Task> tasks;
	private ArrayList<Task> trashs;
	
	private TaskList(Option option) throws FatalException, HandledException{
		this.enableSync = false;
		this.dataFile = new File("CEOStore.ics");
		this.trashFile = new File("CEOTrash.ics");
		switch(option.getValue()){
		default:
		case DEFAULT:
			this.enableSync = true;
		case NOSYNC:
			this.storage = new StorageEngine(this.dataFile);
			this.trash = new StorageEngine(this.trashFile);
			break;
		case TEST:
			this.storage = new StorageStub();
			this.trash = new StorageStub();
			break;
		}
		this.tasks = this.storage.getTaskList();
		this.trashs = this.trash.getTaskList();
	}
	
	public static TaskList getInstance(Option option) throws HandledException, FatalException{
		if (taskList == null){
			taskList = new TaskList(option);
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
		ArrayList<FloatingTask> returnList = new ArrayList<FloatingTask>();
		for (Task task:this.tasks){
			if (task instanceof FloatingTask){
				returnList.add((FloatingTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getDefaultList() throws HandledException{
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.tasks){
			if (!task.getComplete()){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getAllList() throws HandledException{
		return this.tasks;
	}
	
	public Task getTaskByID(int taskID) throws HandledException{
		if (taskID > this.tasks.size() || taskID < 1){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASKID);
		} else {
			return this.tasks.get(taskID-1);
		}
	}
	
	public Task getTaskByTask(Task task){
		for (Task existingTask:this.tasks){
			if (existingTask.equals(task)){
				return existingTask;
			}
		}
		return null;
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
