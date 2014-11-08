//@author A0128478R
package cs2103.task;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;

import org.junit.Before;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public class DeadlineTaskTest extends ToDoTaskTest{
	static DeadlineTask dlt;
	String taskUID = null;
	Date created = null; 
	Status status = null;
	String title = "Testing";
	String description = "Description";
	String location = "Location";
	Recur recurrence = null;
	Date complete = null;
	DateTime dueTime = new DateTime(1);
	
	protected ToDoTask getConcrete(){
		return dlt;
	}

	@Before
	public void setUp() throws Exception {
		dlt = new DeadlineTask(this.taskUID, this.status, this.dueTime);
		dlt.updateTitle(this.title);
		dlt.updateDescription(this.description);
		dlt.updateLocation(this.location);
		dlt.updateRecurrence(this.recurrence);
		dlt.updateLastModified(null);
	}
	
	/**
	 * Three different cases to test DeadlineTask constructor
	 */
	@Test 
	public void testDeadlineTaskConstructor() throws HandledException{
		testDeadlineTaskConstructionOne();
		testDeadlineTaskConstructionTwo();
	}
		
	/**
	 * Case 1: Invalid date
	 */
	public void testDeadlineTaskConstructionOne() throws HandledException {
		exception.expect(HandledException.class);
		dlt = new DeadlineTask(this.taskUID, this.status, null);
	}
	
	/**
	 * Case 2: Successful Construction 
	 */
	public void testDeadlineTaskConstructionTwo() throws HandledException {
		dlt = new DeadlineTask(this.taskUID, this.status, null);
		assertTrue(true);
	}

	@Test
	public void testUpdateAndGetDueTime() throws HandledException{
		DateTime newDate = new DateTime(1);
		assertTrue(dlt.getDueTime().compareTo(newDate) == 0);
	}

	/**
	 * Check if DeadlineTask should be alerted to user
	 */
	@Test
	public void testCheckPeriod() {
		DateTime[] time = new DateTime[2];
		assertEquals(dlt.checkPeriod(time), true);
		
		time[0] = new DateTime(1);
		assertEquals(dlt.checkPeriod(time), false);
		
		time[0] = null;
		time[1] = new DateTime(2);
		assertEquals(dlt.checkPeriod(time), true);
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
		dlt.convert(time);	
	}
	
	private void testConvertToFloating() throws HandledException {
		DateTime[] time = generateTimeForConvert("f");
		Task taskTest = dlt.convert(time);
		assertTrue(taskTest instanceof FloatingTask);
		
		Task taskExpected = new FloatingTask(dlt.getTaskUID(), Status.VTODO_NEEDS_ACTION);
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(taskTest, taskExpected));
	}

	private void testConvertToDeadline() throws HandledException {	
		DateTime[] time = generateTimeForConvert("d");
		Task taskTest = dlt.convert(time);
		assertTrue(taskTest instanceof DeadlineTask);
		
		ToDoTask taskExpected = new DeadlineTask(dlt.getTaskUID(), dlt.getStatus(), time[0]);
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(taskTest, taskExpected));
	}
	
	private void testConvertToPeriodic() throws HandledException {
		DateTime[] time = generateTimeForConvert("p");
		Task taskTest = dlt.convert(time);
		assertTrue(taskTest instanceof PeriodicTask);
		
		PeriodicTask taskExpected = new PeriodicTask(dlt.getTaskUID(), Status.VEVENT_CONFIRMED, time[0], time[1]);
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(taskTest, taskExpected));
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
		ToDoTask task = (ToDoTask) dlt.clone();
		assertTrue(TestUtil.compareTasks(task, dlt));
	}
	
	@Test
	public void testToSummary() {
		Ansi expected = generateSummaryExpected();
		Ansi test = generateSummaryTest();
		assertEquals(expected.toString(), test.toString());
	}

	private Ansi generateSummaryTest() {
		return dlt.toSummary();
	}

	private Ansi generateSummaryExpected() {
		Ansi expected = ansi().fg(YELLOW).a(dlt.getTaskID()).a(". ").reset();
		expected.bold().a(dlt.getTitle()).a('\n').boldOff().reset();
		expected.a("Status: ").a(generateStatusExpected(dlt.getCompleted()));
		expected.a("\tDue At: ").a(generateDateStringExpected(dlt.getDueTime())).a('\n');
		return expected;
	}
	
	private Ansi generateDateStringExpected(Date date) {
		Ansi returnString = ansi().bold();
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);	
		returnString.fg(GREEN).a(format.format(date)).reset();
		return returnString;
	}
	
	private Ansi generateStatusExpected(DateTime completed) {
		Ansi returnString = ansi();
		returnString.bold().fg(RED).a("Needs Action").reset();
		return returnString;
	}
}