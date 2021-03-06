//@author A0128478R
package cs2103.task;

import static org.fusesource.jansi.Ansi.ansi;

import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;

import org.fusesource.jansi.Ansi;
import org.junit.Before;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public class PeriodicTaskTest extends EventTaskTest{
	static PeriodicTask pt;
	String taskUID = null;
	Date created = null; 
	String title = "Testing";
	String location = "Location";
	String description = "Description";
	Status status = null;
	Date startTime = new DateTime(1000);
	Date endTime = new DateTime(1001);
	Recur recurrence = new Recur();

	protected PeriodicTask getConcrete() {
		return pt;
	}
	
	@Before
	public void setUp() throws Exception {
		pt = new PeriodicTask(this.taskUID, this.status, this.startTime, this.endTime);
		updateNewPeriodicTask();
	}

	private void updateNewPeriodicTask() {
		pt.updateTitle(this.title);
		pt.updateDescription(this.description);
		pt.updateLocation(this.location);
		pt.updateLastModified(null);
		pt.updateRecurrence(this.recurrence);
	}

	/**
	 * Three different cases to test PeriodicTask constructor
	 */
	@Test 
	public void testPeriodicTaskConstructor() throws HandledException{
		testPeriodicTaskConstructionOne();
		testPeriodicTaskConstructionTwo();
		testPeriodicTaskConstructionThree();
	}
		
	/**
	 * Case 1: Invalid input times
	 */
	public void testPeriodicTaskConstructionOne() throws HandledException {
		exception.expect(HandledException.class);
		pt = new PeriodicTask(this.taskUID, this.status, null, null);
	}
	
	/**
	 * Case 2: Invalid input times
	 */
	public void testPeriodicTaskConstructionTwo() throws HandledException {
		exception.expect(HandledException.class);
		DateTime newStartTime = new DateTime(1);
		DateTime newEndTime = new DateTime(0);
		pt = new PeriodicTask(this.taskUID, this.status, newStartTime, newEndTime);
	}

	/**
	 * Case 3: Successful Creation
	 */
	public void testPeriodicTaskConstructionThree() throws HandledException {
		pt = new PeriodicTask(this.taskUID, this.status, this.startTime, this.endTime);
		assertTrue(true);
	}
	
	@Test
	public void testUpdateAndGetLocation() {
		String location = "Test Location";
		pt.updateLocation(location);
		assertEquals(location, pt.getLocation());
	}

	@Test
	public void testUpdateAndGetRecurrence() {
		Recur recur = new Recur();
		pt.updateRecurrence(recur);
		assertEquals(recur, pt.getRecurrence());
	}

	@Test
	public void testClone() throws CloneNotSupportedException {
		PeriodicTask task = (PeriodicTask) pt.clone();
		assertTrue(TestUtil.compareTasks(task, pt));
		
	}

	/**
	 * Check if Periodic Task should be alerted to user
	 */
	@Test
	public void testCheckPeriod() {
		Date[] time = new Date[2];
		assertEquals(pt.checkPeriod(time), true);
		
		time[0] = new DateTime(1);
		assertEquals(pt.checkPeriod(time), false);
		
		time[0] = null;
		time[1] = new DateTime(2);
		assertEquals(pt.checkPeriod(time), true);
	}
	
	/**
	 * Matches returns true if the input string is included, regardless
	 * 				of case, in the title, description, and location fields 
	 */
	@Test
	public void testMatches() {
		String keyword = null;
		assertEquals(pt.matches(keyword), true);
		
		keyword = "";
		assertEquals(pt.matches(keyword), true);
		
		keyword = "Testing";
		assertEquals(pt.matches(keyword), true);
		
		keyword = "Codin";
		assertEquals(pt.matches(keyword), false);
		
		pt.updateDescription("Coding");
		assertEquals(pt.matches(keyword), true);
		
		keyword = "New Location";
		assertEquals(pt.matches(keyword), false);
		
		pt.updateLocation("New LOcation");
		assertEquals(pt.matches(keyword), true);
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
		pt.convert(time);	
	}
	
	private void testConvertToFloating() throws HandledException {
		DateTime[] time = generateTimeForConvert("f");
		Task taskTest = pt.convert(time);
		assertTrue(taskTest instanceof FloatingTask);
		
		Task taskExpected = new FloatingTask(pt.getTaskUID(), Status.VTODO_NEEDS_ACTION);
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(taskTest, taskExpected));
	}

	private void testConvertToDeadline() throws HandledException {	
		DateTime[] time = generateTimeForConvert("d");
		Task taskTest = pt.convert(time);
		assertTrue(taskTest instanceof DeadlineTask);
		
		ToDoTask taskExpected = new DeadlineTask(pt.getTaskUID(), Status.VTODO_NEEDS_ACTION, time[0]);
		updateTaskExpected(taskExpected);
		assertTrue(TestUtil.compareTasks(taskTest, taskExpected));
	}
	
	private void testConvertToPeriodic() throws HandledException {
		DateTime[] time = generateTimeForConvert("p");
		Task taskTest = pt.convert(time);
		assertTrue(taskTest instanceof PeriodicTask);
		
		PeriodicTask taskExpected = new PeriodicTask(pt.getTaskUID(), pt.getStatus(), time[0], time[1]);
		updateTaskExpectedPeriodic(taskExpected);
		assertTrue(TestUtil.compareTasks(taskTest, taskExpected));
	}

	private void updateTaskExpectedPeriodic(PeriodicTask taskExpected) {
		updateTaskExpected(taskExpected);
		taskExpected.updateLocation(pt.getLocation());
		taskExpected.updateRecurrence(pt.getRecurrence());
	}

	@Test
	public void testToSummary() {
		testToSummaryWithRecurrence();
		testToSummaryWithoutRecurrence();
	}

	private void testToSummaryWithoutRecurrence() {
		Ansi expected = generateSummaryExpectedNullRecurrence();
		Ansi test = generateSummaryTestNullRecurrence();
		assertEquals(expected.toString(), test.toString());
	}

	private void testToSummaryWithRecurrence() {
		Ansi expected = generateSummaryExpected();
		Ansi test = generateSummaryTest();
		assertEquals(expected.toString(), test.toString());
	}

	private Ansi generateSummaryTest() {
		Ansi test = pt.toSummary();
		return test;
	}

	private Ansi generateSummaryTestNullRecurrence() {
		Ansi test;
		pt.updateRecurrence(null);
		test = pt.toSummary();
		return test;
	}

	private Ansi generateSummaryExpectedNullRecurrence() {
		Ansi expected;
		expected = ansi().fg(YELLOW).a(pt.getTaskID()).a(". ").reset();
		expected.bold().a(pt.getTitle()).a('\n').boldOff().reset();
		expected.a("From: ").a(generateDateStringExpected(pt.getStartTime())).a(" to ");
		expected.a(generateDateStringExpected(pt.getEndTime())).reset();
		expected.a('\n');
		return expected;
	}

	private Ansi generateSummaryExpected() {
		Ansi expected = ansi().fg(YELLOW).a(pt.getTaskID()).a(". ").reset();
		expected.bold().a(pt.getTitle()).a('\n').boldOff().reset();
		expected.a("From: ").a(generateDateStringExpected(pt.getStartTime())).a(" to ");
		expected.a(generateDateStringExpected(pt.getEndTime())).reset();
		expected.a('\n').a(generateRecurExpected(pt.getRecurrence())).a('\n');
		return expected;
	}
	
	private Ansi generateRecurExpected(Recur recur) {
		Ansi returnString = ansi().a("Recurrence: ");
		returnString.fg(YELLOW).a(recur.getInterval()).a(' ');
		returnString.a(recur.getFrequency()).reset();
		return returnString;
	}
	
	private Ansi generateDateStringExpected(Date date) {
		Ansi returnString = ansi().bold();
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);	
		returnString.fg(GREEN).a(format.format(date)).reset();
		return returnString;
	}

	@Test
	public void testToDetail() {
		Ansi expected = generateDetailExpected();
		Ansi test = generateDetailTest();
		assertEquals(expected.toString(), test.toString());
	}

	private Ansi generateDetailTest() {
		return pt.toDetail();
	}

	private Ansi generateDetailExpected() {
		Ansi expected = generateSummaryTest();
		expected.a("Location: ");
		expected.fg(CYAN).a(pt.getLocation()).a("\n").reset();
		expected.a("Description: ").a(pt.getDescription()).reset().a('\n');
		return expected;
	}
	
	/**
	 * Two different test cases to see if a task can correctly update based on its recurrence
	 */
	@Test
	public void testUpdateTimeFromRecur() throws HandledException {
		testUpdateTimeFromRecurOne();
		testUpdateTimeFromRecurTwo();
	}
	
	/**
	 * Recurrence is non null
	 */
	private void testUpdateTimeFromRecurOne() throws HandledException {
		PeriodicTask pt2 = pt.updateTimeFromRecur();
		assertTrue(pt2 == null);
		recurrence.setFrequency(Recur.HOURLY);
		pt.updateRecurrence(recurrence);
		pt2 = pt.updateTimeFromRecur();
		assertTrue(TestUtil.compareTasks(pt, pt2));
	}
		
	/**
	 * Recurrence is null
	 */
	private void testUpdateTimeFromRecurTwo() throws HandledException {
		pt.updateRecurrence(null);
		PeriodicTask pt2 = pt.updateTimeFromRecur();
		assertTrue(pt2 == null);
	}
}