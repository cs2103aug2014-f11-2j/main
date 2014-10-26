package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.fortuna.ical4j.model.property.Uid;
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
		Uid testUid = new Uid("test");
		Status  testStatus = new Status("testPeriodic");
		
		PeriodicTask testPeriodicTask = new PeriodicTask(testUid, null, testStatus, "testPeriodic", "testlocation", new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis() + 864000L), null);
		test.addTask(testPeriodicTask);
		
		ArrayList<PeriodicTask> returnTest = test.getPeriodicList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0),testPeriodicTask));
	}

	@Test
	public void testGetDeadlineList() throws FatalException, HandledException {
		TaskList testDead = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testDead");
		Status testStatus = new Status("testStatus");
		
		DeadlineTask testTask = new DeadlineTask(testUid, new Date(), testStatus, "testDeadline", new Date(), new Date());
		testDead.addTask(testTask);
		ArrayList<DeadlineTask> returnTest = testDead.getDeadlineList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0), testTask));
	}

	@Test
	public void testGetFloatingList() throws HandledException, FatalException {
		TaskList testFloat = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testFloat");
		Status testStatus = new Status("testFloat");
		
		FloatingTask testTask = new FloatingTask(testUid, new Date(), testStatus, "testFloating", new Date());
		testFloat.addTask(testTask);
		ArrayList<FloatingTask> returnTest = testFloat.getFloatingList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0), testTask));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetAllList() throws HandledException, FatalException {
		TaskList testAll = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testAll");
		Status testStatus = new Status("testStatus");
		Date startDate = new Date(2014, 10, 25, 15, 45, 15);
		Date completeDate = new Date(2014, 10, 27, 16, 14, 25);
		
		Task testAllTask = new FloatingTask(testUid, startDate, testStatus, "testAll", completeDate);
		testAll.addTask(testAllTask);
		
		ArrayList<Task> test = testAll.getAllList();
		assertTrue(TestUtil.compareTasks(test.get(2), testAllTask));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTaskByID() throws HandledException, FatalException {
		TaskList testID = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testID");
		Status testStatus = new Status("testID");
		Date createdDate = new Date(2014, 10, 25, 15, 45, 15);
		Date dueDate = new Date(2014, 10, 27, 16, 14, 23);
		
		
		DeadlineTask testIDTask = new DeadlineTask(testUid, createdDate, testStatus, "testID", dueDate, new Date());
		testID.addTask(testIDTask);
		assertTrue(TestUtil.compareTasks(testID.getTaskByID(4), testIDTask));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testAddTask() throws HandledException, FatalException {
		TaskList testAdd = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid test = new Uid("test");
		Status testStatus = new Status("testAdd");
		Date startDate = new Date(2014, 11, 21, 15, 45, 15);
		Date completeDate = new Date(2014, 11, 27, 16, 14, 43);
		
		Task testAddTask = new FloatingTask(test, startDate, testStatus, "testAdd", completeDate);
		testAdd.addTask(testAddTask);
		ArrayList<Task> returnTest = testAdd.getAllList();
		assertTrue(TestUtil.compareTasks(returnTest.get(0), testAddTask));
	}
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testUpdateTask() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testUpdate");
		Status testStatus = new Status("testUpdate");
		Date startDate = new Date(2014, 11, 21, 15, 45, 15);
		Date completeDate = new Date(2014, 12, 27, 16, 14, 43);
		Date startDate1 = new Date(2014,12, 21, 15, 45, 15);
		Date completeDate1 = new Date(2014, 12, 25, 16, 14, 43);
		
		FloatingTask testTask = new FloatingTask(testUid, startDate, testStatus, "testUpdate", completeDate);
		test.addTask(testTask);
		ArrayList<FloatingTask> returnTest = test.getFloatingList();
		assertTrue(TestUtil.compareTasks(returnTest.get(1), testTask));
		
		FloatingTask testTask1 = new FloatingTask(testUid, startDate1, testStatus, "testUpdate", completeDate1);
		test.updateTask(testTask1);
		ArrayList<FloatingTask> returnTest1 = test.getFloatingList();
		assertTrue(TestUtil.compareTasks(returnTest1.get(1), testTask1));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDeleteTask() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testDelete");
		Status testStatus = new Status("testDelete");
		Date startDate = new Date(2014, 10, 30, 15, 45, 15);
		Date completeDate = new Date(2014, 11, 27, 16, 14, 43);
		
		PeriodicTask testTask = new PeriodicTask(testUid, startDate, testStatus, "testDelete", "testingDelete", startDate,completeDate, null);
		test.addTask(testTask);
	
		test.deleteTask(testTask);
		ArrayList<PeriodicTask> returnTest1 = test.getPeriodicList();
		assertTrue(returnTest1.isEmpty());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTrashList() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testDelete");
		Status testStatus = new Status("testDelete");
		Date startDate = new Date(2014, 10, 30, 15, 45, 15);
		Date completeDate = new Date(2014, 11, 27, 16, 14, 43);
		
		PeriodicTask testTask = new PeriodicTask(testUid, startDate, testStatus, "testDelete", "testingDelete", startDate,completeDate, null);
		test.addTask(testTask);
		testTask.delete();
		ArrayList<Task> testTrash = test.getTrashList();
		assertTrue(TestUtil.compareTasks(testTrash.get(0), testTask));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetDefaultList() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(new Option(Option.Value.TEST));
		Uid testUid = new Uid("testDefault");
		Status testStatus = new Status("testDefault");
		Date startDate = new Date(2014, 10, 23, 15, 45, 15);
		Date completeDate = new Date(2014, 10, 25, 16, 14, 43);
		
		PeriodicTask testingDefault = new PeriodicTask(testUid, startDate, testStatus, "testDefault", "testingDefault", startDate,completeDate, null);
		test.addTask(testingDefault);
		
		ArrayList<Task> testDefault = test.getDefaultList();
		assertTrue(TestUtil.compareTasks(testDefault.get(0), testingDefault));
	}

}
