//@author A0128478R
package cs2103.task;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Status;

import org.fusesource.jansi.Ansi;
import org.junit.Test;

import cs2103.exception.HandledException;

/**
 * Containing tests for the inherited methods in concrete Task class PeriodicTask
 */
public abstract class EventTaskTest extends TaskTest{

	@Test
	public void testGetCompleted() throws HandledException{
		EventTask task = (EventTask) getConcrete();
		DateTime testStartDate = new DateTime(0);
		DateTime testEndDate = new DateTime(1);
		task.updateTime(testStartDate, testEndDate);
		assertEquals(testEndDate, task.getCompleted());
	}
	
	@Test
	public void testUpdateAndGetStatus() {
		EventTask task = (EventTask) getConcrete();
		task.updateStatus(null);
		assertEquals(Status.VEVENT_CONFIRMED, task.getStatus());
		
		Status testStatus = Status.VTODO_COMPLETED;
		task.updateStatus(testStatus);
		assertEquals(testStatus, task.getStatus());
	}
	
	@Test
	public void testRestore() {
		EventTask task = (EventTask) getConcrete();
		task.restore();
		assertEquals(Status.VEVENT_CONFIRMED, task.getStatus());
		assertTrue(new DateTime().equals(task.getLastModified()));	
	}

	@Test
	public void testDateToString(){
		EventTask task = (EventTask) getConcrete();
		DateTime testDate = new DateTime(0);
		Ansi test = task.dateToString(testDate);
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
		Ansi expected = ansi().bold().fg(GREEN).a(format.format(testDate)).reset();
		assertEquals(expected.toString(), test.toString());
	}

	/**
	 * Three different cases to test UpdateTime, getStartTime, and getEndTime methods
	 */
	@Test
	public void testUpdateAndGetStartAndEndTime() throws HandledException {
		testUpdateAndGetStartAndEndTimeOne();
		testUpdateAndGetStartAndEndTimeTwo();
		testUpdateAndGetStartAndEndTimeThree();
	}
	
	/**
	 * Case 1: Invalid input, throwing error
	 */
	private void testUpdateAndGetStartAndEndTimeOne() throws HandledException {
		EventTask task = (EventTask) getConcrete();
		exception.expect(HandledException.class);
		DateTime date1 = null;
		DateTime date2 = null;
		task.updateTime(date1, date2);
	}

	/**
	 * Case 2: Start time after end time, throwing error
	 */
	private void testUpdateAndGetStartAndEndTimeTwo() throws HandledException {
		EventTask task = (EventTask) getConcrete();
		exception.expect(HandledException.class);
		Date date1 = new DateTime(2);
		Date date2 = new DateTime(1);
		task.updateTime(date1, date2);
	}
	
	/**
	 * Case 3: Successful Update 
	 */
	private void testUpdateAndGetStartAndEndTimeThree() throws HandledException {
		EventTask task = (EventTask) getConcrete();
		DateTime date1 = new DateTime(1);
		DateTime date2 = new DateTime(2);
		task.updateTime(date1, date2);
		assertEquals(task.getStartTime(), date1);
		assertEquals(task.getEndTime(), date2);
	}
}