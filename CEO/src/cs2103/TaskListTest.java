package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Status;

import org.junit.Test;

import com.google.api.client.util.DateTime;

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
		
		PeriodicTask testPeriodicTask = new PeriodicTask("testPeriodic", null, testStatus, "testPeriodic", "testlocation", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 864000L), null);
		test.addTask(testPeriodicTask);
		
		ArrayList<PeriodicTask> returnTest = test.getPeriodicList();
		assertTrue(TestUtil.compareTasks(returnTest.get(1),testPeriodicTask));
	}

	@Test
	public void testGetDeadlineList() throws FatalException, HandledException {
		TaskList testDead = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testStatus");
		
		DeadlineTask testTask = new DeadlineTask("testDead", new Date(), testStatus, "testDeadline", new Date(), new Date());
		testDead.addTask(testTask);
		ArrayList<DeadlineTask> returnTest = testDead.getDeadlineList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0), testTask));
	}

	@Test
	public void testGetFloatingList() throws HandledException, FatalException {
		TaskList testFloat = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testFloat");
		
		FloatingTask testTask = new FloatingTask("testFloat", new Date(), testStatus, "testFloating", new Date());
		testFloat.addTask(testTask);
		ArrayList<FloatingTask> returnTest = testFloat.getFloatingList();
		assertTrue(TestUtil.compareTasks(returnTest.get(2), testTask));
	}
	
	@Test
	public void testGetAllList() throws HandledException, FatalException {
		TaskList testAll = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testStatus");
		Date startDate = new Date(System.currentTimeMillis());
		Date completeDate = new Date(System.currentTimeMillis() + 864000L);
		
		Task testAllTask = new FloatingTask("testAll", startDate, testStatus, "testAll", completeDate);
		testAll.addTask(testAllTask);
		
		ArrayList<Task> test = testAll.getAllList();
		assertTrue(TestUtil.compareTasks(test.get(4), testAllTask));
	}
	
	@Test
	public void testGetTaskByID() throws HandledException, FatalException {
		TaskList testID = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testID");
		Date createdDate = new Date(System.currentTimeMillis());
		Date dueDate = new Date(System.currentTimeMillis() + 865000L);
		
		
		DeadlineTask testIDTask = new DeadlineTask("testID", createdDate, testStatus, "testID", new Date(), dueDate);
		testID.addTask(testIDTask);
		
		assertTrue(TestUtil.compareTasks(testID.getTaskByID(7), testIDTask));
	}
	
	@Test
	public void testAddTask() throws HandledException, FatalException {
		TaskList testAdd = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testAdd");
		Date startDate = new Date(System.currentTimeMillis());
		Date completeDate =new Date(System.currentTimeMillis() + 863000L);
		
		Task testAddTask = new FloatingTask("testAdd", startDate, testStatus, "testAdd", completeDate);
		testAdd.addTask(testAddTask);
		ArrayList<Task> returnTest = testAdd.getAllList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0), testAddTask));
	}
	
	
	@Test
	public void testUpdateTask() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testUpdate");
		Date startDate = new Date(System.currentTimeMillis());
		Date completeDate = new Date(System.currentTimeMillis() + 861000L);
		Date startDate1 = new Date(System.currentTimeMillis());
		Date completeDate1 = new Date(System.currentTimeMillis() + 867000L);
		
		FloatingTask testTask = new FloatingTask("testUpdate", startDate, testStatus, "testUpdate", completeDate);
		test.addTask(testTask);
		ArrayList<FloatingTask> returnTest = test.getFloatingList();
		assertTrue(TestUtil.compareTasks(returnTest.get(1), testTask));
		
		FloatingTask testTask1 = new FloatingTask("testUpdate", startDate1, testStatus, "testUpdate", completeDate1);
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
		
		PeriodicTask testTask = new PeriodicTask("testDelete", startDate, testStatus, "testDelete", "testingDelete", startDate,completeDate, null);
		test.addTask(testTask);
	
		test.deleteTask(testTask);
		ArrayList<PeriodicTask> returnTest1 = test.getPeriodicList();
		assertTrue(returnTest1.isEmpty());
	}
	
	@Test
	public void testGetTrashList() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testTrash");
		Date startDate = new Date(System.currentTimeMillis());
		Date completeDate = new Date(System.currentTimeMillis() + 869000L);
		
		PeriodicTask testTask = new PeriodicTask("testTrash", startDate, testStatus, "testTrash", "testingTrash", startDate,completeDate, null);
		test.addTask(testTask);
		testTask.delete();
		ArrayList<Task> testTrash = test.getTrashList();
		assertTrue(TestUtil.compareTasks(testTrash.get(0), testTask));
	}
	
	@Test
	public void testGetDefaultList() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Status testStatus = new Status("testDefault");
		Date startDate = new Date(System.currentTimeMillis());
		Date completeDate = new Date(System.currentTimeMillis() + 866000L);
		
		PeriodicTask testingDefault = new PeriodicTask("testDefault", startDate, testStatus, "testDefault", "testingDefault", startDate,completeDate, null);
		test.addTask(testingDefault);
		
		ArrayList<Task> testDefault = test.getDefaultList();
		assertTrue(TestUtil.compareTasks(testDefault.get(0), testingDefault));
	}

}
