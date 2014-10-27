package cs2103;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cs2103.exception.ErrorLogging;
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
import cs2103.util.CommonUtil;

import java.util.Collections;

public class TaskList {
	private static TaskList taskList;
	private final StorageInterface storage;
	private final File dataFile;
	private GoogleEngine google;
	private ArrayList<Task> tasks;
	private static final String SYNCING = "Syncing with Google, please wait for a while";
	private static final String SYNCING_ERROR = "Some error occurred when commit changes to Google";
	private static final String SYNC_FAIL = "Unable to sync your data with Google, Google Sync is disabled";
	
	private TaskList(Option option) throws FatalException, HandledException{
		this.dataFile = new File("CEOStore.ics");
		switch(option.getValue()){
		default:
		case SYNC:
		case DEFAULT:
			try{
				this.google = GoogleEngine.getInstance();
			} catch (HandledException e) {
				CommonUtil.print(e.getErrorMsg());
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
		this.syncWithGoogle();
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
	
	public Task addTask(Task task) throws HandledException, FatalException{
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
		Task returnTask = this.getTaskByTask(task);
		if (this.google != null){
			Task added = this.addToGoogle(task);
			this.tasks = this.storage.getTaskList();
			if (added != null){
				returnTask = this.getTaskByTask(added);
			}
		}
		return returnTask;
	}
	
	public Task updateTask(Task task) throws HandledException, FatalException{
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
		Task returnTask = this.getTaskByTask(task);
		if (this.google != null){
			Task updated = this.updateToGoogle(task);
			this.tasks = this.storage.getTaskList();
			if (updated != null){
				returnTask = this.getTaskByTask(updated);
			}
		}
		return returnTask;
	}
	
	public void deleteTask(Task task) throws HandledException, FatalException{
		this.storage.deleteTask(task);
		this.tasks = this.storage.getTaskList();
		if (this.google != null){
			this.deleteToGoogle(task);
			this.tasks = this.storage.getTaskList();
		}
	}
	
	public boolean syncWithGoogle(){
		if (this.google == null) return false;
		CommonUtil.print(SYNCING);
		try {
			ArrayList<Task> googleList = this.google.getTaskList();
			syncFromGoogle(googleList);
			syncToGoogle(googleList);
			this.tasks = this.storage.getTaskList();
			this.google.updateLastUpdated();
			return true;
		} catch (IOException | FatalException | HandledException e) {
			if (e instanceof IOException){
				ErrorLogging.getInstance().writeToLog(SYNCING_ERROR, e);
			}
			CommonUtil.print(SYNC_FAIL);
			this.google = null;
			return false;
		}
	}
	
	private void deleteToGoogle(Task task) throws HandledException{
		try {
			if (this.google.needToSync(task)){
				this.syncWithGoogle();
			} else {
				this.google.deleteTask(task);
				this.google.updateLastUpdated();
			}
		} catch (IOException e) {
			ErrorLogging.getInstance().writeToLog(SYNCING_ERROR, e);
			CommonUtil.print(SYNCING_ERROR);
		}
	}
	
	private Task updateToGoogle(Task task) throws HandledException, FatalException{
		try {
			if (this.google.needToSync(task)){
				this.syncWithGoogle();
			} else {
				if (task.isDeleted()){
					this.google.deleteTask(task);
				} else {
					Task updating = this.google.updateTask(task);
					this.google.updateLastUpdated();
					if (updating != null){
						this.updateUidInList(task, updating);
						return updating;
					}
				}
			}
		} catch (IOException e) {
			ErrorLogging.getInstance().writeToLog(SYNCING_ERROR, e);
			CommonUtil.print(SYNCING_ERROR);
		}
		return null;
	}

	private Task addToGoogle(Task task) throws HandledException {
		try {
			if (this.google.needToSync(task)){
				this.syncWithGoogle();
			} else {
				Task returnTask = this.google.addTask(task);
				this.google.updateLastUpdated();
				return returnTask;
			}
		} catch (IOException e) {
			ErrorLogging.getInstance().writeToLog(SYNCING_ERROR, e);
			CommonUtil.print(SYNCING_ERROR);
		}
		return null;
	}
	
	private void syncFromGoogle(ArrayList<Task> googleList) throws HandledException, FatalException{
		for (Task remoteTask:googleList){
			Task localTask = this.getTaskByTask(remoteTask, this.tasks);
			if (localTask == null){
				if (!remoteTask.isDeleted()){
					this.storage.updateTask(remoteTask);
				}
			} else {
				if (localTask.getLastModified().before(remoteTask.getLastModified())){
					remoteTask.updateCreated(localTask.getCreated());
					this.storage.updateTask(remoteTask);
				}
			}
		}
	}
	
	private void syncToGoogle(ArrayList<Task> googleList) throws HandledException, FatalException{
		for (Task localTask:this.tasks){
			Task remoteTask = this.getTaskByTask(localTask, googleList);
			if (remoteTask == null){
				Task adding;
				try {
					adding = this.google.addTask(localTask);
				} catch (IOException e) {
					ErrorLogging.getInstance().writeToLog(SYNCING_ERROR, e);
					adding = null;
				}
				if (adding != null){
					this.updateUidInList(localTask, adding);
				}
			} else {
				if (!remoteTask.isDeleted()){
					if (remoteTask.getLastModified().before(localTask.getLastModified())){
						Task updating;
						try {
							updating = this.google.updateTask(localTask);
						} catch (IOException e) {
							ErrorLogging.getInstance().writeToLog(SYNCING_ERROR, e);
							updating = null;
						}
						if (updating != null){
							this.updateUidInList(localTask, updating);
						}
					}
				}
			}
		}
	}
	
	private void updateUidInList(Task oldTask, Task newTask) throws HandledException, FatalException{
		if (oldTask.equals(newTask)){
			this.storage.updateTask(newTask);
		} else {
			this.storage.deleteTask(oldTask);
			this.storage.updateTask(newTask);
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
	
	private Task getTaskByTask(Task task){
		return this.getTaskByTask(task, this.tasks);
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
