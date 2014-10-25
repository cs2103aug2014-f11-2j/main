package cs2103.storage;

import java.util.ArrayList;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.Task;

public interface StorageInterface {
	public void deleteTask(Task task) throws HandledException, FatalException;
	public void updateTask(Task task) throws HandledException, FatalException;
	public void addTask(Task task) throws HandledException, FatalException;
	public ArrayList<Task> getTaskList() throws FatalException, HandledException;
}
