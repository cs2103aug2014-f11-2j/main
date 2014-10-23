package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import cs2103.exception.HandledException;

public abstract class TaskTest {
	private static final String testDescription = "New Description";
	private static final String testTitle = "New Title";
	private static final int testTaskID = 1;
	
	public abstract void testCompareTo() throws HandledException;
	public abstract void testMatches();
	public abstract void testToSummary();
	public abstract void testToDetail();
	public abstract void testClone() throws CloneNotSupportedException;
	
	public void testCheckAlert(Task task){
		assertFalse(task.checkAlert());
	}
		
	public void testUpdateAndGetLastModified(Task task) {
		assertEquals(null,task.getLastModified());
		Date newDate= new Date(1000,1,2);
		task.updateLastModified(newDate);
		assertEquals(newDate,task.getLastModified());
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
		Task taskExpected = new FloatingTask((task).getTaskUID(),task.getCreated(), 
				task.getTitle(), false);
		taskExpected.updateDescription(task.getDescription());
		assertTrue(compareFloatingTasks((FloatingTask) dummyTask,(FloatingTask) taskExpected));
		
		time[0]= new Date(2014,1,1);
		dummyTask =task.convert(time);
		assertTrue(dummyTask instanceof DeadlineTask);
		if (task instanceof DeadlineTask) {
			taskExpected = new DeadlineTask(task.getTaskUID(), task.getCreated(), 
					task.getTitle(), time[0], ((DeadlineTask) task).getComplete());
		} else if (task instanceof FloatingTask) {
			taskExpected = new DeadlineTask(task.getTaskUID(), task.getCreated(), 
					task.getTitle(), time[0], ((FloatingTask) task).getComplete());
		} else {
			taskExpected = new DeadlineTask(task.getTaskUID(), task.getCreated(), 
					task.getTitle(), time[0],false);	
		}
		taskExpected.updateDescription(task.getDescription());
		assertTrue(compareDeadlineTasks((DeadlineTask) dummyTask,(DeadlineTask) taskExpected));
		
		time[1]= new Date(2014,2,1);
		dummyTask=task.convert(time);
		assertTrue(dummyTask instanceof PeriodicTask);
		if (task instanceof PeriodicTask) {
			taskExpected= new PeriodicTask(task.getTaskUID(), task.getCreated(), 
					task.getTitle(), ((PeriodicTask) task).getLocation(), time[0], time[1], null);
		} else {
			taskExpected= new PeriodicTask(task.getTaskUID(), task.getCreated(), 
					task.getTitle(), null, time[0], time[1], null);	
		}
		taskExpected.updateDescription(task.getDescription());
		assertTrue(comparePeriodicTasks((PeriodicTask) dummyTask,(PeriodicTask) taskExpected));
	}
	
	static boolean compareFloatingTasks(FloatingTask dlt1, FloatingTask dlt2){
		if (dlt1.getComplete()!=dlt2.getComplete()) {
			return false;
		} 
		return compareCommonValuesInTasks(dlt1,dlt2);
	}
	
	static boolean compareDeadlineTasks(DeadlineTask t1, DeadlineTask t2){
		if (t1.getComplete()!=t2.getComplete()) {
			return false;
		} else if (t1.getDueTime()!=t2.getDueTime()){
			return false;
		} 
		return compareCommonValuesInTasks(t1,t2);
	}
	
	static boolean comparePeriodicTasks(PeriodicTask t1,PeriodicTask t2){
		if (t1.getStartTime()!=t2.getStartTime()) {
			return false;
		} else if (t1.getRecurrence()!=t2.getRecurrence()){
			return false;
		} else if (t1.getLastModified()!=t2.getLastModified()){
			return false;
 		} else if (t1.getEndTime()!=t2.getEndTime()){
 			return false;
 		} else if (t1.getLocation()!=t2.getLocation()){
 			return false;
 		}
		return compareCommonValuesInTasks(t1,t2);
	}
	
	static boolean compareCommonValuesInTasks(Task t1,Task t2){
		if (t1.getLastModified()!=t2.getLastModified()){
			return false;
 		} else if (t1.getCreated()!=t2.getCreated()) {
			return false;
		} else if (t1.getDescription()!=t2.getDescription()) {
			return false;
		} else if (t1.getTitle()!=t2.getTitle()) {
			return false;
		} else if (t1.getTaskID()!=t1.getTaskID()) {
 			return false;
 		} 
		return true;
	} 
}