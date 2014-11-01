package cs2103.storage;

import static org.junit.Assert.*;

import java.io.IOException;

import net.fortuna.ical4j.model.DateTime;

import org.junit.Before;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.task.*;

public class GoogleEngineTest {
	private GoogleEngine google;
	private static final long DAY_IN_MILLIS = 86400000L;
	
	@Before
	public void setUp() throws Exception {
		this.google = GoogleEngine.getInstance();
	}

	@Test
	public void testFloatingTask() throws HandledException, IOException {
		ToDoTask testTask = new FloatingTask(null, null);
		testTask.updateTitle("Tiro Finale");
		testTask.updateDescription("Nanimo kowakunai");
		testTask = (ToDoTask) this.google.addTask(testTask);
		assertEquals("Tiro Finale", testTask.getTitle());
		assertEquals("Nanimo kowakunai", testTask.getDescription());
		DateTime completed = new DateTime();
		testTask.updateCompleted(completed);
		testTask = (ToDoTask) this.google.updateTask(testTask);
		assertEquals(completed.getTime(), testTask.getCompleted().getTime());
		boolean success = false;
		for (Task task:this.google.getTaskList()){
			if (!task.isDeleted() && task.equals(testTask)) success = true;
		}
		assertTrue(success);
		this.google.deleteTask(testTask);
		for (Task task:this.google.getTaskList()){
			if (!task.isDeleted() && task.equals(testTask)) fail();
		}
	}
	
	@Test
	public void testDeadlineTask() throws HandledException, IOException {
		ToDoTask testTask = new DeadlineTask(null, null, new DateTime());
		testTask.updateTitle("TwinTails");
		testTask.updateDescription("Tail On!");
		testTask = (ToDoTask) this.google.addTask(testTask);
		assertEquals("TwinTails", testTask.getTitle());
		assertEquals("Tail On!", testTask.getDescription());
		DateTime completed = new DateTime();
		testTask.updateCompleted(completed);
		testTask = (ToDoTask) this.google.updateTask(testTask);
		assertEquals(completed.getTime(), testTask.getCompleted().getTime());
		boolean success = false;
		for (Task task:this.google.getTaskList()){
			if (!task.isDeleted() && task.equals(testTask)) success = true;
		}
		assertTrue(success);
		this.google.deleteTask(testTask);
		for (Task task:this.google.getTaskList()){
			if (!task.isDeleted() && task.equals(testTask)) fail();
		}
	}
	
	@Test
	public void testPeriodicTask() throws HandledException, IOException {
		DateTime startTime = new DateTime();
		DateTime endTime = new DateTime(startTime.getTime() + DAY_IN_MILLIS);
		EventTask testTask = new PeriodicTask(null, null, startTime, endTime);
		testTask.updateTitle("Anata no tame nara");
		testTask.updateDescription("Watashi ha eien no meiron ni tochikomeraredemo, kamawanai");
		testTask.updateLocation("Mitakihara");
		testTask = (EventTask) this.google.addTask(testTask);
		assertEquals("Anata no tame nara", testTask.getTitle());
		assertEquals("Watashi ha eien no meiron ni tochikomeraredemo, kamawanai", testTask.getDescription());
		assertEquals("Mitakihara", ((PeriodicTask) testTask).getLocation());
		assertEquals(startTime.getTime(), testTask.getStartTime().getTime());
		assertEquals(endTime.getTime(), testTask.getEndTime().getTime());
		boolean success = false;
		for (Task task:this.google.getTaskList()){
			if (!task.isDeleted() && task.equals(testTask)) success = true;
		}
		assertTrue(success);
		this.google.deleteTask(testTask);
		for (Task task:this.google.getTaskList()){
			if (!task.isDeleted() && task.equals(testTask)) fail();
		}
	}
	
	//Test for invalid input
	@Test(expected = HandledException.class)
	public void testNullAdd() throws HandledException, IOException{
		this.google.addTask(null);
	}
	
	//Test for invalid input
	@Test(expected = HandledException.class)
	public void testNullUpdate() throws HandledException, IOException{
		this.google.updateTask(null);
	}
	
	//Test for invalid input
	@Test(expected = HandledException.class)
	public void testNullDelete() throws HandledException, IOException{
		this.google.deleteTask(null);
	}
}
