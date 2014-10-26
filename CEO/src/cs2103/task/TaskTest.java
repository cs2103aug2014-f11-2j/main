package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public abstract class TaskTest {
	private static final String testDescription = "New Description";
	private static final String testTitle = "New Title";
	private static final int testTaskID = 1;
	
	public abstract void testCompareTo() throws HandledException;
	public abstract void testMatches();
	public abstract void testToSummary();
	public abstract void testToDetail();
	public abstract void testClone() throws CloneNotSupportedException;
	public abstract void testUpdateAndGetStatus();;
	public abstract void testRestore();
	
	public void testCheckAlert(Task task){
		assertFalse(task.checkAlert());
	}
		
	public void testUpdateAndGetLastModified(Task task) {
		assertTrue(task.getLastModified().equals(new DateTime(new Date())));
		Date newDate= new Date(1000,1,2);
		task.updateLastModified(newDate);
		assertTrue(task.getLastModified().equals(newDate));
	}
	
	public void testUpdateAndGetTaskID(Task task) {
		task.updateTaskID(testTaskID);
		assertEquals(task.getTaskID(),testTaskID);
	}
	
	public void testUpdateAndGetDescription(Task task) {
		task.updateDescription(testDescription);
		assertEquals(testDescription,task.getDescription());
	}
	
	public void testUpdateAndGetTitle(Task task) throws HandledException {
		try {
			String newTitle = testTitle;
			task.updateTitle(newTitle);
			assertEquals(testTitle,task.getTitle());
		} catch (HandledException e) {
			fail("Expected- Successful Update");
		}
		try {
			String newTitle="";
			task.updateTitle(newTitle);
			fail("Expected- Handled Exception");
		} catch (HandledException e) {
			assertEquals("A Non-empty title must be specified!", e.getErrorMsg());
		}
	}
	
	public void testConvert(Task task) throws HandledException {
		Date[] time=null;
		try{
			task.convert(time);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals("Your input time cannot be parsed, please check your input and try again!", e.getErrorMsg());
		}
		time= new Date[2];
		time[0]=null;
		time[1]=null;
		Task dummyTask = task.convert(time);
		assertTrue(dummyTask instanceof FloatingTask);
		dummyTask= task.convert(time);
		Task taskExpected = new FloatingTask((task).getTaskUID(),task.getCreated(), task.getStatus(),
				task.getTitle(), task.getCompleted());
		taskExpected.updateDescription(task.getDescription());
		assertTrue(TestUtil.compareTasks(dummyTask, taskExpected));
		
		time[0]= new Date(2014,1,1);
		dummyTask =task.convert(time);
		assertTrue(dummyTask instanceof DeadlineTask);
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
		
		time[1]= new Date(2014,2,1);
		dummyTask=task.convert(time);
		assertTrue(dummyTask instanceof PeriodicTask);
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
	
	public void testEquals(Task task) throws CloneNotSupportedException, HandledException{
		Object o = null;
		assertEquals(false, task.equals(o));
		o = (String) "Testing";
		assertEquals(false, task.equals(o));
		FloatingTask dummyTask = new FloatingTask(null, null, null, testTitle, null);
		assertFalse(task.equals(dummyTask));
		assertTrue(task.equals(task.clone()));
	}
	
	public void testDeleteAndIsDelete(Task task) {
		assertEquals(false, task.isDeleted());
		task.delete();
		assertEquals(true, task.isDeleted());
	}
	

}