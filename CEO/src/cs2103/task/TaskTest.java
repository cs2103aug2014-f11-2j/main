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
		task.updateCreated(null);
		assertTrue(new DateTime().equals(task.getCreated()));
		
		DateTime testDate = new DateTime(1);
		task.updateCreated(testDate);
		assertTrue(testDate.equals(task.getCreated()));
	}
	
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
	
	@Test
	public void testCompareTo() throws CloneNotSupportedException{
		Task task = getConcrete();
		assertEquals(0, task.compareTo((Task) task.clone()));
	}
}