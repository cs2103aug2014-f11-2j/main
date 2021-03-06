//@author A0116713M
package cs2103.storage;

import java.util.ArrayList;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.Task;

public interface StorageInterface {
	/**
	 * Delete the task from storage
	 * @param task
	 * @throws HandledException
	 * @throws FatalException
	 */
	public void deleteTask(Task task) throws HandledException, FatalException;
	/**
	 * Add the Task object into file if a Task with the same UID does not exist, update if it does
	 * @param task
	 * @throws HandledException
	 * @throws FatalException
	 */
	public void updateTask(Task task) throws HandledException, FatalException;
	/**
	 * @return The most up-to-date Task list
	 * @throws FatalException
	 * @throws HandledException
	 */
	public ArrayList<Task> getTaskList() throws FatalException, HandledException;
}
