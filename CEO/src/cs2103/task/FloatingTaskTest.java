package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;

import org.junit.Before;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.storage.TaskList;
import cs2103.util.TestUtil;

public class FloatingTaskTest extends ToDoTaskTest {
	private static FloatingTask ft;
	private final String taskUID = null;
	Status status = null;
	String title = "Testing";
	String description = "Description";
	String location = "Location";
	Recur recurrence = null;

	protected FloatingTask getConcrete(){
		return ft;
	}
	
	@Before
	public void setUp() throws Exception {
		ft = new FloatingTask(this.taskUID, this.status);
		ft.updateTitle(this.title);
		ft.updateDescription(this.description);
		ft.updateLocation(this.location);
		ft.updateRecurrence(this.recurrence);
		ft.updateLastModified(null);
	}
	
	@Test
	public void testDeadlineTaskConstructor(){
		ft = new FloatingTask(null, null);
		assertTrue(true);
	}
	
	@Test
	public void testConvert() throws HandledException {
		testConvertOne();
		testConvertToFloating();
		testConvertToDeadline();
		testConvertToPeriodic();
	}
	
	private void testConvertOne() throws HandledException {
		exception.expect(HandledException.class);
		Date[] time=null;
		ft.convert(time);	
	}

	private void testConvertToFloating() throws HandledException {
		DateTime[] time = new DateTime[2];
		time[0] = null;
		time[1] = null;
		Task ft2 = ft.convert(time);
		assertTrue(ft2 instanceof FloatingTask);
		
		Task taskExpected = new FloatingTask(null, null);
		taskExpected.updateTitle(ft.getTitle());
		taskExpected.updateDescription(ft.getDescription());
		taskExpected.updateLastModified(ft.getLastModified());
		taskExpected.updateCompleted(ft.getCompleted());
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}

	private void testConvertToDeadline() throws HandledException {	
		DateTime[] time = new DateTime[2];
		time[0] = new DateTime(1);
		time[1] = null;
		Task ft2 = ft.convert(time);
		assertTrue(ft2 instanceof DeadlineTask);
		
		Task taskExpected = new DeadlineTask(null, null, time[0]);
		taskExpected.updateTitle(ft.getTitle());
		taskExpected.updateDescription(ft.getDescription());
		taskExpected.updateLastModified(ft.getLastModified());
		taskExpected.updateCompleted(ft.getCompleted());
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}
	
	private void testConvertToPeriodic() throws HandledException {
		DateTime[] time = new DateTime[2];
		time[0] = new DateTime(1);
		time[1]= new DateTime(2);
		Task ft2 = ft.convert(time);
		assertTrue(ft2 instanceof PeriodicTask);
		
		Task taskExpected = new PeriodicTask(null, null, time[0], time[1]);
		taskExpected.updateTitle(ft.getTitle());
		taskExpected.updateDescription(ft.getDescription());
		ft.updateLocation(this.location);
		ft.updateRecurrence(this.recurrence);
		taskExpected.updateLastModified(ft.getLastModified());
		taskExpected.updateCompleted(ft.getCompleted());
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
			FloatingTask task = (FloatingTask) ft.clone();
			assertTrue(TestUtil.compareTasks(task,ft));
	}

	@Test
	public void testToSummary() {
		fail();
	}

	@Test
	public void testToDetail() {
		fail();
	}	
}