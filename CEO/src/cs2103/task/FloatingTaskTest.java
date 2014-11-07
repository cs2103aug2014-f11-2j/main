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
	static ToDoTask ft;
	private final String taskUID = null;
	Status status = null;
	String title = "Testing";
	String description = "Description";
	String location = "Location";
	Recur recurrence = null;

	protected ToDoTask getConcrete(){
		return ft;
	}
	
	@Before
	public void setUp() throws Exception {
		ft = new FloatingTask(this.taskUID, this.status);
		updateNewFloatingTask();
	}

	private void updateNewFloatingTask() {
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
		DateTime[] time = generateTimeForConvert("f");
		Task ft2 = ft.convert(time);
		assertTrue(ft2 instanceof FloatingTask);
		
		ToDoTask taskExpected = new FloatingTask(ft.getTaskUID(), ft.getStatus());
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}

	private void testConvertToDeadline() throws HandledException {	
		DateTime[] time = generateTimeForConvert("d");
		Task ft2 = ft.convert(time);
		assertTrue(ft2 instanceof DeadlineTask);
		
		ToDoTask taskExpected = new DeadlineTask(ft.getTaskUID(), Status.VTODO_NEEDS_ACTION, time[0]);
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}
	
	private void testConvertToPeriodic() throws HandledException {
		DateTime[] time = generateTimeForConvert("p");
		Task ft2 = ft.convert(time);
		assertTrue(ft2 instanceof PeriodicTask);
		
		PeriodicTask taskExpected = new PeriodicTask(ft.getTaskUID(), Status.VEVENT_CONFIRMED, time[0], time[1]);
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(ft2, taskExpected));
	}

	@Test
	public void testClone() throws CloneNotSupportedException {
		ToDoTask task = (ToDoTask) ft.clone();
		assertTrue(TestUtil.compareTasks(task,ft));
	}

	@Test
	public void testToSummary() {
		testToSummaryUndeleted();
		testToSummaryDeleted();
	}

	private void testToSummaryDeleted() {
		Ansi expected = generateSummaryExpectedDeleted();
		Ansi test = generateSummaryTestDeleted();
		assertEquals(expected.toString(), test.toString());
	}

	private void testToSummaryUndeleted() {
		Ansi expected = generateSummaryExpected();
		Ansi test = generateSummaryTest();
		assertEquals(expected.toString(), test.toString());
	}

	private Ansi generateSummaryTestDeleted() {
		Ansi test;
		ft.delete();
		test = generateSummaryTest();
		return test;
	}

	private Ansi generateSummaryTest() {
		return ft.toSummary();
	}

	private Ansi generateSummaryExpectedDeleted() {
		Ansi expected;
		Ansi deletedTest = ansi().fg(MAGENTA).a("(Deleted Task)\n").reset();
		expected = ansi().fg(YELLOW).a(ft.getTaskID()).a(". ").reset();
		expected.bold().a(ft.getTitle()).a('\n').boldOff().reset().a(deletedTest);
		expected.a("Status: ").a(ToDoTask.completedToString(ft.getCompleted())).a('\n');
		return expected;
	}

	private Ansi generateSummaryExpected() {
		Ansi expected = ansi().fg(YELLOW).a(ft.getTaskID()).a(". ").reset();
		expected.bold().a(ft.getTitle()).a('\n').boldOff().reset();
		expected.a("Status: ").a(ToDoTask.completedToString(ft.getCompleted())).a('\n');
		return expected;
	}
}