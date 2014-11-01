package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public abstract class TaskTest {
	private static final String testDescription = "New Description";
	private static final String testTitle = "New Title";
	private static final int testTaskID = 1;
	
	protected abstract Task getConcrete();
	
	public abstract void testCompareTo() throws HandledException;
	public abstract void testMatches();
	public abstract void testToSummary();
	public abstract void testToDetail();
	public abstract void testClone() throws CloneNotSupportedException;
	public abstract void testUpdateAndGetStatus();;
	public abstract void testRestore();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public void testCheckAlert(Task task){
		assertFalse(task.checkAlert());
	}
		
	public void testUpdateAndGetLastModified() {
		Task task = getConcrete();
		assertTrue(task.getLastModified().equals(new DateTime(new Date())));
		Date newDate= new DateTime(1000);
		task.updateLastModified(newDate);
		assertTrue(task.getLastModified().equals(newDate));
	}
	
	@Test
	public void testUpdateAndGetTaskID() {
		Task task = getConcrete();
		task.updateTaskID(testTaskID);
		assertEquals(task.getTaskID(),testTaskID);
	}
	
	@Test
	public void testUpdateAndGetDescription() {
		Task task = getConcrete();
		task.updateDescription(testDescription);
		assertEquals(testDescription,task.getDescription());
	}
	
	@Test
	public void testUpdateAndGetTitle() throws HandledException {
			Task task = getConcrete();
			String newTitle = testTitle;
			task.updateTitle(newTitle);
			assertEquals(testTitle,task.getTitle());
	}
	
	@Test
	public void testUpdateAndGetCreated() {
		Task task = getConcrete();
		
	}
	
	@Test
	public void testUpdateAndGetTaskUID(){
		Task task = getConcrete();
	}
	
	public void testConvert(Task task) throws HandledException {
		testConvertOne(task);
		//testConvertToFloating(task);
		//testConvertToDeadline(task);
		//testConvertToPeriodic(task);
	}
	
	public void testConvertOne(Task task) throws HandledException{
		exception.expect(HandledException.class);
		Date[] time=null;
		task.convert(time);	
	}
	
	/*
	public void testConvertToFloating(Task task) throws HandledException {
		Date[] time = new Date[2];
		time[0] = null;
		time[1] = null;
		Task dummyTask = task.convert(time);
		assertTrue(dummyTask instanceof FloatingTask);
		Task taskExpected;
		if (task instanceof PeriodicTask) {
			taskExpected = new FloatingTask((task).getTaskUID(),task.getCreated(), task.getStatus(),
					task.getTitle(), null);
		} else {
			taskExpected = new FloatingTask((task).getTaskUID(),task.getCreated(), task.getStatus(),
					task.getTitle(), task.getCompleted());	
		}
		taskExpected.updateDescription(task.getDescription());
		assertTrue(TestUtil.compareTasks(dummyTask, taskExpected));
	}
	
	public void testConvertToDeadline(Task task) throws HandledException {
		Date[] time = new Date[2];
		time[0] = new DateTime(2014);
		time[1] = null;
		Task dummyTask =task.convert(time);
		assertTrue(dummyTask instanceof DeadlineTask);
		Task taskExpected;
		if (task instanceof DeadlineTask) {
			taskExpected = new DeadlineTask(task.getTaskUID(), task.getCreated(), task.getStatus(),
					task.getTitle(), time[0], ((DeadlineTask) task).getCompleted());
		} else if (task instanceof FloatingTask) {
			taskExpected = new DeadlineTask(task.getTaskUID(), task.getCreated(), task.getStatus(),
					task.getTitle(), time[0], ((FloatingTask) task).getCompleted());
		} else {
			taskExpected = new DeadlineTask(task.getTaskUID(), task.getCreated(), task.getStatus(),
					task.getTitle(), time[0], null);	
		}
		taskExpected.updateDescription(task.getDescription());
		assertTrue(TestUtil.compareTasks(dummyTask, taskExpected));
	}
	
	public void testConvertToPeriodic(Task task) throws HandledException {
		Date[] time = new Date[2];
		time[0] = new DateTime(2014);
		time[1]= new DateTime(2015);
		Task dummyTask=task.convert(time);
		assertTrue(dummyTask instanceof PeriodicTask);
		Task taskExpected;
		if (task instanceof PeriodicTask) {
			taskExpected= new PeriodicTask(task.getTaskUID(), task.getCreated(), task.getStatus(),
					task.getTitle(), ((PeriodicTask) task).getLocation(), time[0], time[1], null);
		} else {
			taskExpected= new PeriodicTask(task.getTaskUID(), task.getCreated(), task.getStatus(), 
					task.getTitle(), null, time[0], time[1], null);	
		}
		taskExpected.updateDescription(task.getDescription());
		assertTrue(TestUtil.compareTasks(dummyTask, taskExpected));
	}
	*/
	
	@Test
	public void testEquals() throws CloneNotSupportedException, HandledException{
		Task task = getConcrete();
		Object o = null;
		assertEquals(false, task.equals(o));
		o = (String) "Testing";
		assertEquals(false, task.equals(o));
		FloatingTask dummyTask = new FloatingTask(null, null);
		assertFalse(task.equals(dummyTask));
		assertTrue(task.equals(task.clone()));
	}
	
	@Test
	public void testDeleteAndIsDelete() {
		Task task = getConcrete();
		assertEquals(false, task.isDeleted());
		task.delete();
		assertEquals(true, task.isDeleted());
	}
}