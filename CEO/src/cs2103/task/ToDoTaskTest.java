package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Status;

import org.junit.Test;

public abstract class ToDoTaskTest extends TaskTest{

	@Test
	public void testUpdateAndGetCompleted(){
		Task task = getConcrete();
		task.updateCompleted(null);
		assertEquals(null, task.getCompleted());
		Date testDate = new DateTime();
		task.updateCompleted(testDate);
		assertTrue(testDate.equals(task.getCompleted()));
	}
	
	@Test
	public void testUpdateAndGetStatus(){
		Task task = getConcrete();
		task.updateStatus(null);
		assertEquals(Status.VTODO_NEEDS_ACTION, task.getStatus());
		String testStatus = "Test Status";
		assertEquals(testStatus, task.getStatus());
	}
	
	@Test
	public void testDeleteAndIsDeleted(){
		Task task = getConcrete();
		task.delete();
		assertTrue(task.isDeleted());
	}
	
	@Test
	public void testRestore(){
		Task task = getConcrete();
		task.updateCompleted(null);
		task.restore();
		assertEquals(Status.VTODO_NEEDS_ACTION, task.getStatus());
		Date testDate = new DateTime();
		task.updateCompleted(testDate);
		task.restore();
		assertEquals(Status.VTODO_COMPLETED, task.getStatus());
		assertEquals(new DateTime(), task.getLastModified());
	}
	
	@Test
	
}
