package cs2103;

import java.io.File;
import java.util.ArrayList;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.StorageEngine;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;

import java.util.Collections;

public class TaskList {
	private static TaskList taskList;
	private final StorageEngine storage;
	private final StorageEngine trash;
	private final File dataFile = new File("CEOStore.ics");
	private final File trashFile = new File("CEOTrash.ics");
	private boolean enableSync;
	private ArrayList<Task> tasks;
	private ArrayList<Task> trashs;
	
	private TaskList(Option option) throws FatalException, HandledException{
		this.enableSync = false;
		switch(option.getValue()){
		default:
		case DEFAULT:
			this.enableSync = true;
		case NOSYNC:
			this.storage = new StorageEngine(this.dataFile);
			this.trash = new StorageEngine(trashFile);
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
