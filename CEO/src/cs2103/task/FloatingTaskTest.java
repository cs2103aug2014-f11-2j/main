//@author A0128478R
package cs2103.task;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.junit.Assert.*;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;

import org.fusesource.jansi.Ansi;
import org.junit.Before;
import org.junit.Test;

import cs2103.exception.HandledException;
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
		testConvertException();
		testConvertToFloating();
		testConvertToDeadline();
		testConvertToPeriodic();
	}
	
	private void testConvertException() throws HandledException {
		exception.expect(HandledException.class);
		DateTime[] time = null;
		ft.convert(time);	
	}

	private void testConvertToFloating() throws HandledException {
		DateTime[] time = new DateTime[2];
		time[0] = null;
		time[1] = null;
		Task ft2 = ft.convert(time);
		assertTrue(ft2 instanceof FloatingTask);
		
		FloatingTask taskExpected = new FloatingTask(null, ft.getStatus());
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
		
		DeadlineTask taskExpected = new DeadlineTask(null, null, time[0]);
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
		
		PeriodicTask taskExpected = new PeriodicTask(null, null, time[0], time[1]);
		taskExpected.updateTitle(ft.getTitle());
		taskExpected.updateDescription(ft.getDescription());
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
		Ansi expected = ansi().fg(YELLOW).a(ft.getTaskID()).a(". ").reset();
		expected.bold().a(ft.getTitle()).a('\n').boldOff().reset();
		expected.a("Status: ").a(ToDoTask.completedToString(ft.getCompleted())).a('\n');
		Ansi test = ft.toSummary();
		assertEquals(expected.toString(), test.toString());
		
		Ansi deletedTest = ansi().fg(MAGENTA).a("(Deleted Task)\n").reset();
		ft.delete();
		expected = ansi().fg(YELLOW).a(ft.getTaskID()).a(". ").reset();
		expected.bold().a(ft.getTitle()).a('\n').boldOff().reset().a(deletedTest);
		expected.a("Status: ").a(ToDoTask.completedToString(ft.getCompleted())).a('\n');
		test = ft.toSummary();
		assertEquals(expected.toString(), test.toString());
	}
}