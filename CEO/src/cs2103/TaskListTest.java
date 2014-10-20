package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.fortuna.ical4j.model.property.Uid;

import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;

public class TaskListTest {
	
	@Test
	public void testGetPeriodicList() throws FatalException, HandledException {
		TaskList test = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		Calendar cal1 =Calendar.getInstance();
		cal1.setTimeInMillis(0);
		cal1.set(2014, 10, 21, 18, 59, 23);
		Date testEnd = cal.getTime();
		
		PeriodicTask testTask = new PeriodicTask(testUid, testStart, "testPeriodic", "testLocation", testStart, testEnd, null);
		test.addTask(testTask);
		ArrayList<PeriodicTask> returnTest = test.getPeriodicList();
		assertEquals(returnTest.get(0).getTitle(), "testPeriodic");
	}

	@Test
	public void testGetDeadlineList() throws FatalException, HandledException {
		TaskList test = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		Calendar cal1 =Calendar.getInstance();
		cal1.setTimeInMillis(0);
		cal1.set(2014, 10, 21, 18, 59, 23);
		Date testEnd = cal.getTime();
		
		DeadlineTask testTask = new DeadlineTask(testUid, testStart, "testDeadline", testEnd, false);
		test.addTask(testTask);
		ArrayList<DeadlineTask> returnTest = test.getDeadlineList();
		assertEquals(returnTest.get(0).getTitle(), "testDeadline");
	}

	@Test
	public void testGetFloatingList() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		
		FloatingTask testTask = new FloatingTask(testUid, testStart, "testFloating", false);
		test.addTask(testTask);
		ArrayList<FloatingTask> returnTest = test.getFloatingList();
		assertEquals(returnTest.get(0).getTitle(), "testFloating");
	}
	
	
	@Test
	public void testGetAllList() throws HandledException, FatalException {
		TaskList testAll = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		Calendar cal1 =Calendar.getInstance();
		cal1.setTimeInMillis(0);
		cal1.set(2014, 10, 21, 18, 59, 23);
		Date testEnd = cal.getTime();
		
		FloatingTask testFloat = new FloatingTask(testUid, testStart, "testFloating", false);
		DeadlineTask testDeadline = new DeadlineTask(testUid, testStart, "testDeadline", testEnd, false);
		PeriodicTask testPeriodic = new PeriodicTask(testUid, testStart, "testPeriodic", "testLocation", testStart, testEnd, null);
		
		testAll.addTask(testFloat);
		testAll.addTask(testDeadline);
		testAll.addTask(testPeriodic);
		
		ArrayList<Task> test = testAll.getAllList();
		assertEquals(test.get(0).getTitle(), "testFloating");
		assertEquals(test.get(1).getTitle(), "testDeadline");
		assertEquals(test.get(2).getTitle(), "testPeriodic");
	}
	
	
	@Test
	public void testGetTaskByID() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		Calendar cal1 =Calendar.getInstance();
		cal1.setTimeInMillis(0);
		cal1.set(2014, 10, 21, 18, 59, 23);
		Date testEnd = cal.getTime();
		
		DeadlineTask testTask = new DeadlineTask(testUid, testStart, "testDeadline", testEnd, false);
		test.addTask(testTask);
		Task testingTask = test.getTaskByID(1);
		assertEquals(testingTask.getTitle(), "testDeadline");
	}
	
	
	@Test
	public void testAddTask() throws HandledException, FatalException {
		TaskList testAdd = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		Calendar cal1 =Calendar.getInstance();
		cal1.setTimeInMillis(0);
		cal1.set(2014, 10, 21, 18, 59, 23);
		Date testEnd = cal.getTime();
		
		PeriodicTask testTask = new PeriodicTask(testUid, testStart, "testPeriodic", "testLocation", testStart, testEnd, null);
		testAdd.addTask(testTask);
		ArrayList<PeriodicTask> returnTest = testAdd.getPeriodicList();
		assertEquals(returnTest.get(0).getTitle(), "testPeriodic");
	}
	
	@Test
	public void testUpdateTask() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		Calendar cal1 =Calendar.getInstance();
		cal1.setTimeInMillis(0);
		cal1.set(2014, 10, 21, 18, 59, 23);
		Date testEnd = cal.getTime();
		
		PeriodicTask testTask = new PeriodicTask(testUid, testStart, "testPeriodic", "testLocation", testStart, testEnd, null);
		test.addTask(testTask);
		ArrayList<PeriodicTask> returnTest = test.getPeriodicList();
		assertEquals(returnTest.get(0).getTitle(), "testPeriodic");
		
		DeadlineTask testTask1 = new DeadlineTask(testUid, testStart, "testDeadline", testEnd, false);
		test.updateTask(testTask1);
		ArrayList<DeadlineTask> returnTest1 = test.getDeadlineList();
		assertEquals(returnTest1.get(0).getTitle(), "testDeadline");
	}

	@Test
	public void testDeleteTask() throws HandledException, FatalException {
		TaskList test = TaskList.getInstance(null, false);
		Uid testUid = new Uid("test");
		
		Calendar cal =Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(2014, 10, 20, 18, 59, 23);
		Date testStart = cal.getTime();
		Calendar cal1 =Calendar.getInstance();
		cal1.setTimeInMillis(0);
		cal1.set(2014, 10, 21, 18, 59, 23);
		Date testEnd = cal.getTime();
		
		PeriodicTask testTask = new PeriodicTask(testUid, testStart, "testPeriodic", "testLocation", testStart, testEnd, null);
		test.addTask(testTask);
		ArrayList<PeriodicTask> returnTest = test.getPeriodicList();
		assertEquals(returnTest.get(0).getTitle(), "testPeriodic");
		
		test.deleteTask(testTask);
		ArrayList<PeriodicTask> returnTest1 = test.getPeriodicList();
		assertEquals(returnTest1.isEmpty(), true);
	}

}
