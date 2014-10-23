package cs2103;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.*;

public class StorageEngineTest {
	StorageEngine storage;
	File file = new File("Test.ics");
	
	@Before
	public void before() throws HandledException, FatalException{
		file.delete();
		this.storage = new StorageEngine(file.getName());
	}
	
	@Test
	public void testAddNewTask() throws HandledException, FatalException {
		Task task1 = new FloatingTask(null, null, "Test 1", false);
		Task task2 = new DeadlineTask(null, null, "Test 2", new Date(), false);
		Task task3 = new PeriodicTask(null, null, "Test 3", "Test Location", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 864000L), null);
		storage.updateTask(task1);
		storage.updateTask(task2);
		storage.updateTask(task3);
		ArrayList<Task> testList = storage.getTaskList();
		assertEquals(3, testList.size());
		for (Task task:testList){
			if (task.equals(task1)) assertTrue(compareTasks(task, task1));
			if (task.equals(task2)) assertTrue(compareTasks(task, task2));
			if (task.equals(task3)) assertTrue(compareTasks(task, task3));
		}
	}
	
	@Test
	public void testUpdateTask() throws HandledException, FatalException{
		ArrayList<Task> testList = storage.getTaskList();
		ArrayList<Task> expectedList = new ArrayList<Task>();
		for (Task task:testList){
			expectedList.add(task);
		}
		for (Task task:expectedList){
			task.updateTitle(task.getTaskUID().getValue());
			storage.updateTask(task);
		}
		testList = storage.getTaskList();
		for (Task task:testList){
			for (Task expected:expectedList){
				if (expected.equals(task)){
					assertTrue(compareTasks(task, expected));
				}
			}
		}
	}
	
	@Test
	public void testDeleteTask() throws FatalException, HandledException{
		ArrayList<Task> testList = storage.getTaskList();
		for (Task task:testList){
			storage.deleteTask(task);
		}
		testList = storage.getTaskList();
		assertEquals(0, testList.size());
	}
	
	@Test(expected = HandledException.class)
	public void testNullUpdate() throws HandledException, FatalException{
		storage.updateTask(null);
	}
	
	@Test(expected = HandledException.class)
	public void testNullDelete() throws HandledException, FatalException{
		storage.deleteTask(null);
	}
	
	private boolean compareTasks(Task task1, Task task2){
		if (task1 == null || task2 == null) return false;
		if (!task1.getTitle().equals(task2.getTitle())) return false;
		if (!task1.getDescription().equals(task2.getDescription())) return false;
		//if (task1.getCreated().getTime() != task2.getCreated().getTime()) return false;
		//if (task1.getLastModified().getTime() != task2.getLastModified().getTime()) return false;
		if (task1 instanceof FloatingTask && task2 instanceof FloatingTask){
			return compareFloating((FloatingTask) task1, (FloatingTask) task2);
		} else if (task1 instanceof DeadlineTask && task2 instanceof DeadlineTask){
			return compareDeadline((DeadlineTask) task1, (DeadlineTask) task2);
		} else if (task1 instanceof PeriodicTask && task2 instanceof PeriodicTask){
			return comparePeriodic((PeriodicTask) task1, (PeriodicTask) task2);
		} else {
			return false;
		}
	}
	
	private boolean compareFloating(FloatingTask task1, FloatingTask task2){
		if (task1 == null || task2 == null) return false;
		if (task1.getComplete() != task2.getComplete()) return false;
		return true;
	}
	
	private boolean compareDeadline(DeadlineTask task1, DeadlineTask task2){
		if (task1 == null || task2 == null) return false;
		if (task1.getComplete() != task2.getComplete()) return false;
		//if (task1.getDueTime().getTime() != task2.getDueTime().getTime()) return false;
		return true;
	}
	
	private boolean comparePeriodic(PeriodicTask task1, PeriodicTask task2){
		if (task1 == null || task2 == null) return false;
		if (!task1.getLocation().equals(task2.getLocation())) return false;
		//if (task1.getStartTime().getTime() != task2.getStartTime().getTime()) return false;
		//if (task1.getEndTime().getTime() != task2.getEndTime().getTime()) return false;
		if (task1.getRecurrence() == null && task2.getRecurrence() == null){
			return true;
		} else if (task1.getRecurrence() == null || task2.getRecurrence() == null){
			return false;
		} else {
			return task1.getRecurrence().toString().equals(task2.getRecurrence().toString());
		}
	}
}
