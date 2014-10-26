package cs2103;

import java.io.File;
import java.util.ArrayList;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.GoogleEngine;
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
	private final File dataFile;
	private GoogleEngine google;
	private ArrayList<Task> tasks;
	
	private TaskList(Option option) throws FatalException, HandledException{
		this.dataFile = new File("CEOStore.ics");
		switch(option.getValue()){
		default:
		case DEFAULT:
			try{
				this.google = GoogleEngine.getInstance();
			} catch (HandledException e) {
				this.google = null;
			}
		case NOSYNC:
			this.storage = StorageEngine.getInstance(this.dataFile);
			break;
		case TEST:
			this.storage = StorageStub.getInstance();
			break;
		}
		this.tasks = this.storage.getTaskList();
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
		for (Task task:this.getAllList()){
			if (task instanceof PeriodicTask){
				returnList.add((PeriodicTask) task);
			}
		}
		Collections.sort(returnList, PeriodicTask.getComparator());
		return returnList;
	}
	
	public ArrayList<DeadlineTask> getDeadlineList() throws HandledException, FatalException{
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (Task task:this.getAllList()){
			if (task instanceof DeadlineTask){
				returnList.add((DeadlineTask) task);
			}
		}
		Collections.sort(returnList, DeadlineTask.getComparator());
		return returnList;
	}
	
	public ArrayList<FloatingTask> getFloatingList() throws HandledException, FatalException{
		ArrayList<FloatingTask> returnList = new ArrayList<FloatingTask>();
		for (Task task:this.getAllList()){
			if (task instanceof FloatingTask){
				returnList.add((FloatingTask) task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getTrashList(){
		return this.filterList(this.tasks, true);
	}
	
	public ArrayList<Task> getDefaultList() throws HandledException{
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.getAllList()){
			if (task.getCompleted() == null){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	public ArrayList<Task> getAllList() throws HandledException{
		return this.filterList(this.tasks, false);
	}
	
	public Task getTaskByID(int taskID) throws HandledException{
		if (taskID > this.tasks.size() || taskID < 1){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASKID);
		} else {
			return this.tasks.get(taskID-1);
		}
	}
	
	public Task getTaskByTask(Task task){
		return this.getTaskByTask(task, this.tasks);
	}
	
	public Task addTask(Task task) throws HandledException, FatalException{
		this.storage.addTask(task);
		this.tasks = this.storage.getTaskList();
		return this.getTaskByTask(task);
	}
	
	public Task updateTask(Task task) throws HandledException, FatalException{
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
		return this.getTaskByTask(task);
	}
	
	public void deleteTask(Task task) throws HandledException, FatalException{
		if (task.isDeleted()){
			this.storage.deleteTask(task);
		} else {
			task.delete();
			this.storage.updateTask(task);
		}
		this.tasks = this.storage.getTaskList();
	}
	
	public Task restoreTask(Task task) throws HandledException, FatalException{
		task.restore();
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
		return this.getTaskByTask(task);
	}
	
	public void syncWithGoogle(){
		try {
			ArrayList<Task> googleList = this.google.getTaskList();
			for (Task remoteTask:googleList){
				Task localTask = this.getTaskByTask(remoteTask, this.tasks);
				if (localTask == null){
					this.storage.addTask(remoteTask);
				} else {
					if (localTask.getLastModified().before(remoteTask.getLastModified())){
						this.storage.updateTask(remoteTask);
					}
				}
			}
			for (Task localTask:this.tasks){
				Task remoteTask = this.getTaskByTask(localTask, googleList);
				if (remoteTask == null){
					Task adding = this.google.addTask(localTask);
					if (adding != null){
						this.storage.deleteTask(localTask);
						this.storage.addTask(adding);
					}
				} else {
					if (localTask.isDeleted()){
						this.google.deleteTask(remoteTask);
					} else {
						if (remoteTask.getLastModified().before(localTask.getLastModified())){
							this.google.updateTask(localTask);
						}
					}
				}
			}
			this.tasks = this.storage.getTaskList();
		} catch (FatalException | HandledException e) {
			this.google = null;
		}
	}
	
	private ArrayList<Task> filterList(ArrayList<Task> taskList, boolean deleted){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:taskList){
			if (task.isDeleted() == deleted){
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	private Task getTaskByTask(Task task, ArrayList<Task> taskList){
		for (Task existingTask:taskList){
			if (existingTask.equals(task)){
				return existingTask;
			}
		}
		return null;
	}
}
