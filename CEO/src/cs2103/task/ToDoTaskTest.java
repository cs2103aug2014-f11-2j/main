package cs2103.task;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Status;

import org.fusesource.jansi.*;
import org.junit.Test;

public abstract class ToDoTaskTest extends TaskTest{

	@Test
	public void testUpdateAndGetCompleted(){
		ToDoTask task = (ToDoTask) getConcrete();
		task.updateCompleted(null);
		assertEquals(null, task.getCompleted());
		DateTime testDate = new DateTime();
		task.updateCompleted(testDate);
		assertTrue(testDate.equals(task.getCompleted()));
	}
	
	@Test
	public void testUpdateAndGetStatus(){
		ToDoTask task = (ToDoTask) getConcrete();
		task.updateStatus(null);
		assertEquals(Status.VTODO_NEEDS_ACTION, task.getStatus());
		String testStatus = "Test Status";
		assertEquals(testStatus, task.getStatus());
	}
	
	@Test
	public void testDeleteAndIsDeleted(){
		ToDoTask task = (ToDoTask) getConcrete();
		task.delete();
		assertTrue(task.isDeleted());
	}
	
	@Test
	public void testRestore(){
		ToDoTask task = (ToDoTask) getConcrete();
		task.updateCompleted(null);
		task.restore();
		assertEquals(Status.VTODO_NEEDS_ACTION, task.getStatus());
		DateTime testDate = new DateTime();
		task.updateCompleted(testDate);
		task.restore();
		assertEquals(Status.VTODO_COMPLETED, task.getStatus());
		assertEquals(new DateTime(), task.getLastModified());
	}
	
	@Test
	public void testCompletedToString(){
		ToDoTask task = (ToDoTask) getConcrete();
		Ansi testAnsi = ansi().fg(RED).a("Needs Action").reset(); 
		assertEquals(testAnsi, task.completedToString(null));
		DateTime testDate = new DateTime();
		testAnsi = ansi().fg(GREEN).a("Completed").reset(); 
		assertEquals(testAnsi, task.completedToString(testDate));
	}
	
}
