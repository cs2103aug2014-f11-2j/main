package cs2103.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import cs2103.TestUtil;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.*;

public class StorageEngineTest {
	StorageEngine storage;
	File file = new File("Test.ics");
	
	@Before
	public void before() throws HandledException, FatalException{
		file.delete();
		this.storage = StorageEngine.getInstance(file);
	}
	
	@Test
	public void testAddNewTask() throws HandledException, FatalException {
		Task task1 = new FloatingTask(null, null, null, "Test 1", null);
		Task task2 = new DeadlineTask(null, null, null, "Test 2", new Date(), null);
		Task task3 = new PeriodicTask(null, null, null, "Test 3", "Test Location", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 864000L), null);
		storage.updateTask(task1);
		storage.updateTask(task2);
		storage.updateTask(task3);
		ArrayList<Task> testList = storage.getTaskList();
		assertEquals(3, testList.size());
		for (Task task:testList){
			if (task.equals(task1)) assertTrue(TestUtil.compareTasks(task, task1));
			if (task.equals(task2)) assertTrue(TestUtil.compareTasks(task, task2));
			if (task.equals(task3)) assertTrue(TestUtil.compareTasks(task, task3));
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
					assertTrue(TestUtil.compareTasks(task, expected));
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
	
}
