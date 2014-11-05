//@author A0128478R
package cs2103.task;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.junit.Assert.*;

import java.util.Date;

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
		pt.updateTitle(this.title);
		pt.updateDescription(this.description);
		pt.updateLocation(this.location);
		pt.updateLastModified(null);
		pt.updateRecurrence(this.recurrence);
		System.out.println(recurrence.getFrequency());
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
	public void testToSummary() {
		Ansi expected = ansi().fg(YELLOW).a(pt.getTaskID()).a(". ").reset();
		expected.bold().a(pt.getTitle()).a('\n').boldOff().reset();
		expected.a("From: ").a(pt.dateToString(pt.getStartTime())).a(" to ");
		expected.a(pt.dateToString(pt.getEndTime())).reset();
		expected.a('\n').a(PeriodicTask.recurToString(pt.getRecurrence())).a('\n');
		Ansi test = pt.toSummary();
		assertEquals(expected.toString(), test.toString());
		
		pt.updateRecurrence(null);
		expected = ansi().fg(YELLOW).a(pt.getTaskID()).a(". ").reset();
		expected.bold().a(pt.getTitle()).a('\n').boldOff().reset();
		expected.a("From: ").a(pt.dateToString(pt.getStartTime())).a(" to ");
		expected.a(pt.dateToString(pt.getEndTime())).reset();
		expected.a('\n');
		test = pt.toSummary();
		assertEquals(expected.toString(), test.toString());
	}

	@Test
	public void testToDetail() {
		Ansi expected = pt.toSummary();
		expected.a("Location: ");
		expected.fg(CYAN).a(pt.getLocation()).a("\n").reset();
		expected.a("Description: ").a(pt.getDescription()).reset().a('\n');
		Ansi test = pt.toDetail();
		assertEquals(expected.toString(), test.toString());
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