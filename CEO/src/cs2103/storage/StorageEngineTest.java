package cs2103.storage;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.*;
import cs2103.util.TestUtil;

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
		Task task1 = new FloatingTask(null, null);
		task1.updateTitle("I feel my body is so light");
		task1.updateDescription("I have never feel such way before");
		Task task2 = new DeadlineTask(null, null, new Date());
		task2.updateTitle("Nothing to be afraid of any more");
		task2.updateDescription("Because I am no longer alone");
		Task task3 = new PeriodicTask(null, null, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 864000L));
		task3.updateTitle("Tiro Finale");
		task3.updateDescription("If every Puella Magi will become a witch, why not we die now");
		task3.updateLocation("Mitakihara");
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
			task.updateTitle(task.getTaskUID());
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
}
