//@author A0116713M
package cs2103.storage;

import java.util.ArrayList;
import java.util.Collections;

import cs2103.task.Task;

/**
 * @author Yuri
 * This class is used for testing purpose, it will not write the task list into file system
 */
public class StorageStub implements StorageInterface {
	private ArrayList<Task> tasks;
	
	public StorageStub() {
		this.tasks = new ArrayList<Task>();
	}
	
	/**
	 * @see cs2103.storage.StorageInterface#deleteTask(cs2103.task.Task)
	 */
	@Override
	public void deleteTask(Task task) {
		Task existing = this.getModifyingTask(task);
		if (existing != null) {
			this.tasks.remove(existing);
		}
	}
	
	/**
	 * @see cs2103.storage.StorageInterface#updateTask(cs2103.task.Task)
	 */
	@Override
	public void updateTask(Task task) {
		this.deleteTask(task);
		this.tasks.add(task);
	}
	
	/**
	 * @see cs2103.storage.StorageInterface#getTaskList()
	 */
	@Override
	public ArrayList<Task> getTaskList() {
		sortTasks();
		return this.tasks;
	}
	
	private void sortTasks() {
		Collections.sort(this.tasks);
		int count=0;
		for (Task task:this.tasks) {
			count++;
			task.updateTaskID(count);
		}
	}
	
	private Task getModifyingTask(Task task){
		for (Task existing:this.tasks){
			if (existing.equals(task)){
				return existing;
			}
		}
		return null;
	}
}
