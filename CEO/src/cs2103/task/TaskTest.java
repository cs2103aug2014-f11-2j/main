//@author A0128478R
package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cs2103.exception.HandledException;

/**
 * Containing tests for the inherited methods in 
 * 				concrete Task classes: DeadlineTask, FloatingTask, and PeriodicTask
 */

public abstract class TaskTest {
	private static final String testDescription = "New Description";
	private static final String testTitle = "New Title";
	private static final int testTaskID = 1;
	
	/**
	 * Called in the beginning of every JUnit test that is inherited from 
	 * 							an Abstract JUnit Test class
	 * Obtains a concrete object to run JUnit test
	 */
	protected abstract Task getConcrete();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testCheckAlert(){
		Task task = getConcrete();
		assertFalse(task.checkAlert());
	}
		
	@Test
	public void testUpdateAndGetLastModified() {
		Task task = getConcrete();
		assertTrue(task.getLastModified().equals(new DateTime(new Date())));
		
		Date testDate= new DateTime(1000);
		task.updateLastModified(testDate);
		assertTrue(task.getLastModified().equals(testDate));
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
		testUpdateAndGetCreatedNull(task);
		testUpdateAndGetCreatedNonNull(task);
	}

	private void testUpdateAndGetCreatedNonNull(Task task) {
		DateTime testDate = new DateTime(1);
		task.updateCreated(testDate);
		assertTrue(testDate.equals(task.getCreated()));
	}

	private void testUpdateAndGetCreatedNull(Task task) {
		task.updateCreated(null);
		assertTrue(new DateTime().equals(task.getCreated()));
	}

	@Test
	public void testEquals() throws CloneNotSupportedException, HandledException{
		Task task = getConcrete();
		testEqualsNull(task);
		testEqualsNonTask(task);
		testEqualsTasks(task);
	}
	
	/**
	 * Variable dummyTask is a task (never used in other test cases) used as 
	 * 				comparison to test method Equals
	 */
	private void testEqualsTasks(Task task) throws CloneNotSupportedException {
		Task dummyTask = generateDummyTask();
		assertFalse(task.equals(dummyTask));
		assertTrue(task.equals(task.clone()));
	}

	private void testEqualsNonTask(Task task) {
		Object o;
		o = (String) "Testing";
		assertEquals(false, task.equals(o));
	}

	private void testEqualsNull(Task task) {
		Object o = null;
		assertEquals(false, task.equals(o));
	}

	private Task generateDummyTask() {
		Task dummyTask = new FloatingTask(null, null);
		return dummyTask;
	}
	
	@Test
	public void testDeleteAndIsDelete() {
		Task task = getConcrete();
		assertEquals(false, task.isDeleted());
		task.delete();
		assertEquals(true, task.isDeleted());
	}
	
	@Test
	public void testCompareTo() throws CloneNotSupportedException{
		Task task = getConcrete();
		assertEquals(0, compareToTask(task));
	}

	private int compareToTask(Task task) throws CloneNotSupportedException {
		return task.compareTo((Task) task.clone());
	}
	
	@Test
	public void testDeleteAndIsDeleted(){
		Task task = (Task) getConcrete();
		task.delete();
		assertTrue(task.isDeleted());
	}
	
	protected DateTime[] generateTimeForConvert(String type) {
		DateTime[] time = new DateTime[2];
		if (type.equals("f")) {
			generateFloatingTaskTime(time);
		} else if (type.equals("d")) {
			generateDeadlineTaskTime(time);
		} else if (type.equals("p")) {
			generatePeriodicTaskTime(time);
		}
		return time;
	}

	private void generatePeriodicTaskTime(DateTime[] time) {
		time[0] = new DateTime(1);
		time[1]= new DateTime(2);
	}

	private void generateDeadlineTaskTime(DateTime[] time) {
		time[0] = new DateTime(1);
		time[1] = null;
	}

	private void generateFloatingTaskTime(DateTime[] time) {
		time[0] = null;
		time[1] = null;
	}
	
	protected void updateTaskExpected(Task taskExpected) {
		taskExpected.updateTitle(taskExpected.getTitle());
		taskExpected.updateDescription(taskExpected.getDescription());
		taskExpected.updateLastModified(taskExpected.getLastModified());
		taskExpected.updateCompleted(taskExpected.getCompleted());
	}
}