package cs2103;

import java.util.ArrayList;
import java.util.Collections;

import cs2103.task.Task;

public class StorageStub extends StorageEngine {
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
		for (Task existing:this.tasks){
			if (existing.equals(task)){
				this.tasks.remove(existing);
			}
		}
		sortTasks();
	}
	
	@Override
	public void updateTask(Task task){
		this.deleteTask(task);
		this.tasks.add(task);
		sortTasks();
	}
	
	@Override
	public ArrayList<Task> getTaskList(){
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
}
