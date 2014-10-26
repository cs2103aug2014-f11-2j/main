package cs2103.storage;

import java.util.ArrayList;
import java.util.Collections;

import cs2103.task.Task;

public class StorageStub implements StorageInterface {
	private static StorageStub storage;
	private ArrayList<Task> tasks;
	
	private StorageStub(){
		this.tasks = new ArrayList<Task>();
	}
	
	public static StorageStub getInstance(){
		if (storage == null){
			storage = new StorageStub();
		}
		return storage;
	}
	
	@Override
	public void deleteTask(Task task){
		Task existing = this.getModifyingTask(task);
		if (existing != null){
			this.tasks.remove(existing);
		}
	}
	
	@Override
	public void updateTask(Task task){
		this.deleteTask(task);
		this.tasks.add(task);
	}
	
	@Override
	public ArrayList<Task> getTaskList(){
		sortTasks();
		return this.tasks;
	}
	
	private void sortTasks(){
		Collections.sort(this.tasks);
		int count=0;
		for (Task task:this.tasks){
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
