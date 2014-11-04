package cs2103.storage;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import net.fortuna.ical4j.model.property.Status;

import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;
import cs2103.util.TestUtil;


public class TaskListTest {
	
	@Test
	public void testGetPeriodicList() throws FatalException, HandledException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status  testStatus = new Status("testPeriodic");
		
		PeriodicTask testPeriodicTask = new PeriodicTask("testPeriodic", testStatus, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 864000L));
		test.addTask(testPeriodicTask);
		
		ArrayList<PeriodicTask> returnTest = test.getPeriodicList();
		assertEquals(returnTest.get(0).getStatus(), testStatus);
	}

	@Test
	public void testGetDeadlineList() throws FatalException, HandledException {
		TaskList testDead = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testStatus");
		
		DeadlineTask testTask = new DeadlineTask("testDeadline", testStatus, new Date());
		testDead.addTask(testTask);
		ArrayList<DeadlineTask> returnTest = testDead.getDeadlineList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0), testTask));
	}

	@Test
	public void testGetFloatingList() throws HandledException, FatalException {
		TaskList testFloat = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testFloat");
		
		FloatingTask testTask = new FloatingTask("testFloat", testStatus);
		testFloat.addTask(testTask);
		ArrayList<FloatingTask> returnTest = testFloat.getFloatingList();
		
		assertTrue(TestUtil.compareTasks(returnTest.get(3), testTask));
	}
	
	@Test
	public void testGetAllList() throws HandledException, FatalException {
		TaskList testAll = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testStatus");
	
		Task testAllTask = new FloatingTask("testAll", testStatus);
		testAll.addTask(testAllTask);
		
		ArrayList<Task> test = testAll.getAllList();
		assertTrue(TestUtil.compareTasks(test.get(5), testAllTask));
	}
	
	@Test
	public void testGetTaskByID() throws HandledException, FatalException {
		TaskList testID = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testID");
		Date dueDate = new Date(System.currentTimeMillis() + 865000L);
		
		
		DeadlineTask testIDTask = new DeadlineTask("testID", testStatus, dueDate);
		testID.addTask(testIDTask);
		assertTrue(TestUtil.compareTasks(testID.getTaskByID(8), testIDTask));
	}
	
	@Test
	public void testAddTask() throws HandledException, FatalException {
		TaskList testAdd = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testAdd");
		
		Task testAddTask = new FloatingTask("testAdd", testStatus);
		testAdd.addTask(testAddTask);
		ArrayList<Task> returnTest = testAdd.getAllList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0), testAddTask));
	}
	
	
	@Test
	public void testUpdateTask() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testUpdate");
		
		FloatingTask testTask = new FloatingTask("testUpdate", testStatus);
		test.addTask(testTask);
		ArrayList<FloatingTask> returnTest = test.getFloatingList();
		assertTrue(TestUtil.compareTasks(returnTest.get(1), testTask));
		
		FloatingTask testTask1 = new FloatingTask("testUpdate2", testStatus);
		test.updateTask(testTask1);
		ArrayList<FloatingTask> returnTest1 = test.getFloatingList();
		assertTrue(TestUtil.compareTasks(returnTest1.get(1), testTask1));
	}

	@Test
	public void testDeleteTask() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testDelete");
		Date startDate = new Date(System.currentTimeMillis());
		Date completeDate = new Date(System.currentTimeMillis() + 861000L);
		
		PeriodicTask testTask = new PeriodicTask("testDelete", testStatus, startDate,completeDate);
		test.addTask(testTask);
	
		test.deleteTask(testTask);
		ArrayList<PeriodicTask> returnTest1 = test.getPeriodicList();
		assertTrue(returnTest1.isEmpty());
	}
	
	@Test
	public void testGetTrashList() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testTrash");
		
		FloatingTask testTask = new FloatingTask("testTrash", testStatus);
		test.addTask(testTask);
		testTask.delete();
		ArrayList<Task> testTrash = test.getTrashList();
		assertEquals(testTrash.get(0).getStatus(), testTask.getStatus());
	}
	
	@Test
	public void testGetDefaultList() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testDefault");
		
		FloatingTask testingDefault = new FloatingTask("testFloating", testStatus);
		test.addTask(testingDefault);
		
		ArrayList<Task> testDefault = test.getDefaultList();
		assertTrue(TestUtil.compareTasks(testDefault.get(7), testingDefault));
	}

}

