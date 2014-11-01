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

public abstract class EventTaskTest extends TaskTest{

	@Test
	public void testGetCompleted(){
		EventTask task = (EventTask) getConcrete();
		DateTime testStartDate = new DateTime(0);
		DateTime testEndDate = new DateTime(0);
		assertEquals(testEndDate, task.getCompleted());
		
		testEndDate = new DateTime(1000);
		assertEquals(null, task.getCompleted());
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
	public void testDeleteAndIsDeleted() {
		EventTask task = (EventTask) getConcrete();
		task.delete();
		assertTrue(task.isDeleted());
	}

	@Test
	public void testRestore() {
		EventTask task = (EventTask) getConcrete();
		task.restore();
		assertEquals(Status.VEVENT_CONFIRMED, task.getStatus());
		assertEquals(null, task.getLastModified());	
	}

	@Test
	public void testDateToString(){
		EventTask task = (EventTask) getConcrete();
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
		DateTime testDate = new DateTime(0);
		Ansi testAnsi = ansi().fg(RED).a(format.format(testDate)).reset();
		assertEquals(testAnsi, task.dateToString(testDate));
		
		testDate = new DateTime(100);
		testAnsi = ansi().fg(GREEN).a(format.format(testDate)).reset();
		assertEquals(testAnsi, task.dateToString(testDate));
	}

	@Test
	public void testUpdateAndGetStartAndEndTime() throws HandledException {
		testUpdateAndGetStartAndEndTimeOne();
		testUpdateAndGetStartAndEndTimeTwo();
		testUpdateAndGetStartAndEndTimeThree();
	}
	
	private void testUpdateAndGetStartAndEndTimeOne() throws HandledException {
		EventTask task = (EventTask) getConcrete();
		exception.expect(HandledException.class);
		DateTime date1 = null;
		DateTime date2 = null;
		task.updateTime(date1, date2);
	}

	private void testUpdateAndGetStartAndEndTimeTwo() throws HandledException {
		EventTask task = (EventTask) getConcrete();
		exception.expect(HandledException.class);
		Date date1 = new DateTime(2);
		Date date2 = new DateTime(1);
		task.updateTime(date1, date2);
	}
	
	private void testUpdateAndGetStartAndEndTimeThree() throws HandledException {
		EventTask task = (EventTask) getConcrete();
		DateTime date1 = new DateTime(1);
		DateTime date2 = new DateTime(2);
		task.updateTime(date1, date2);
		assertEquals(task.getStartTime(), date1);
		assertEquals(task.getEndTime(), date2);
	}
}