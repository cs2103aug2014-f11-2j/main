package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public class PeriodicTaskTest extends TaskTest{
	static PeriodicTask pt;
	String taskUID = null;
	Date created = null; 
	String title = "Testing";
	String location = "Location";
	Status status = null;
	Date dueTime;
	boolean complete;
	static Date startTime = new Date(1000, 10, 10);
	static Date endTime = new Date(1001, 10, 10);
	Recur recurrence = null;

	@Before
	public void setUp() throws Exception {
		pt = new PeriodicTask(taskUID, created, status, title, location, startTime, endTime, recurrence);
		pt.updateDescription(null);
		recurrence = new Recur();
		recurrence.setFrequency("HOURLY");
		recurrence.setInterval(1);
	}

	@Test 
	public void testPeriodicTaskConstructor(){
		testPeriodicTaskConstructionOne();
		testPeriodicTaskConstructionTwo();
		testPeriodicTaskConstructionThree();
		testPeriodicTaskConstructionFour();
		testPeriodicTaskConstructionFive();
	}
	
	public void testPeriodicTaskConstructionOne() {
		try{
			pt = new PeriodicTask(taskUID, created, status, "", location, startTime, endTime, recurrence);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(), "A Non-empty title must be specified!");
		}
	}
	
	public void testPeriodicTaskConstructionTwo() {
		try{
			pt = new PeriodicTask(taskUID, created, status, title, location, null, null, recurrence);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(), "Your input time cannot be parsed, please check your input and try again!");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void testPeriodicTaskConstructionThree() {
		try{
			Date startDate = new Date (2000, 1, 1);
			Date endDate = new Date(1999, 1, 1);
			pt = new PeriodicTask(taskUID, created, status, title, location, startDate, endDate, recurrence);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(), "Your end time is before start time, please check your input and try again");
		}
	}

	public void testPeriodicTaskConstructionFour() {
		try{
			pt = new PeriodicTask(taskUID, created, status, title, location, startTime, endTime, null);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	public void testPeriodicTaskConstructionFive() {
		try{
			Recur recur = new Recur();
			pt = new PeriodicTask(null, null, status, title, location, startTime, endTime, recur);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	public void testPeriodicTaskConstructionSix() {
		try{
			Recur recur = new Recur();
			pt = new PeriodicTask(null, null, new Status(), title, location, startTime, endTime, recur);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	@Test
	public void testUpdateAndGetLocation() {
		String location = "place";
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
	public void testConvert() throws HandledException {
		testConvert(pt);
	}

	@Test
	public void testClone() {
		try {
			PeriodicTask task = (PeriodicTask) pt.clone();
			assertTrue(TestUtil.compareTasks(task, pt));
		} catch (CloneNotSupportedException e){
			fail("Expected- Successful Clone");
		}
	}

	@Test
	public void testToSummary() {
		assertEquals("0. Testing\nType: Periodic\t"
				+ "From: 10-Nov-2900 00:00:00 To 10-Nov-2901 00:00:00\n", pt.toSummary());
		pt.updateRecurrence(recurrence);
		assertEquals("0. Testing\nType: Recurring\t"
				+ "From: 10-Nov-2900 00:00:00 To 10-Nov-2901 00:00:00\n", pt.toSummary());
	}

	@Test
	public void testToDetail() {
		assertEquals("0. Testing\nType: Periodic\t"
				+ "From: 10-Nov-2900 00:00:00 To 10-Nov-2901 00:00:00\n"
				+ "Location: Location\nDescription: \n", pt.toDetail());
		pt.updateDescription("Description");
		assertEquals("0. Testing\nType: Periodic\t"
				+ "From: 10-Nov-2900 00:00:00 To 10-Nov-2901 00:00:00\n"
				+ "Location: Location\nDescription: Description\n", pt.toDetail());
		pt.updateRecurrence(recurrence);
		assertEquals("0. Testing\nType: Recurring\t"
				+ "From: 10-Nov-2900 00:00:00 To 10-Nov-2901 00:00:00\n"
				+ "Recurrence: 1 HOURLY\n"
				+ "Location: Location\nDescription: Description\n", pt.toDetail());
		
	}

	@Test
	public void testCheckPeriod() {
		Date[] time = new Date[2];
		assertEquals(pt.checkPeriod(time), true);
		time[0] = new Date(2011, 1, 1);
		assertEquals(pt.checkPeriod(time), true);
		time[0] = null;
		time[1] = new Date(2012, 1, 1);
		assertEquals(pt.checkPeriod(time), false);
	}

	@Test
	public void testMatches() {
		String keyword = null;
		assertEquals(pt.matches(keyword), true);
		keyword ="";
		assertEquals(pt.matches(keyword), true);
		keyword ="Testing";
		assertEquals(pt.matches(keyword), true);
		keyword ="Coding";
		assertEquals(pt.matches(keyword), false);
		pt.updateDescription("Coding");
		assertEquals(pt.matches(keyword), true);
		keyword ="New Location";
		assertEquals(pt.matches(keyword), false);
		pt.updateLocation("New Location");
		assertEquals(pt.matches(keyword), true);
	}
	
	@Test
	public void testUpdateAndGetStartAndEndTime() {
		Date date1 = null;
		Date date2 = null;
		try{
			pt.updateTime(date1, date2);
			fail("Expected- Handled Exception");
		} catch (HandledException e){
			assertEquals("Your input time cannot be parsed, please check your input and try again!", e.getErrorMsg());
		}
		date1 = new Date(2, 1, 1);
		date2 = new Date(1, 1, 1);
		try{
			pt.updateTime(date1, date2);
			fail("Expected- Handled Exception");
		} catch (HandledException e){
			assertEquals("Your end time is before start time, please check your input and try again", e.getErrorMsg());
		}
		date1 = new Date(1, 1, 1);
		date2 = new Date(2, 1, 1);
		try{
			pt.updateTime(date1, date2);
			assertTrue(true);
		} catch (HandledException e){
			fail("Expected- Successful Update");
		}
		assertEquals(pt.getStartTime(), date1);
		assertEquals(pt.getEndTime(), date2);
	}

	@Test
	public void testUpdateTimeFromRecur() throws HandledException {
		PeriodicTask pt2 = pt.updateTimeFromRecur();
		assertTrue(pt2 == null);
		pt.updateRecurrence(recurrence);
		pt2 = pt.updateTimeFromRecur();
		assertTrue(pt2 == null);
		pt.updateTime(new Date(0, 0, 0), new Date(0, 0, 1));
		pt2 = pt.updateTimeFromRecur();
		DateTime now = new DateTime();
		Date startDate = (pt.getRecurrence().getNextDate(new DateTime(pt.getStartTime()), now));
		pt.updateTime((pt.getRecurrence().getNextDate(new DateTime(pt.getStartTime()), now)),
				new Date(pt.getEndTime().getTime() - pt.getStartTime().getTime() + startDate.getTime()));
		assertTrue(TestUtil.compareTasks(pt, pt2));	
	}

	@Test
	public void testUpdateAndGetTaskID() {
		testUpdateAndGetTaskID(pt);
	}

	@Test
	public void testUpdateAndGetTitle() throws HandledException {
		testUpdateAndGetTitle(pt);
	}

	@Test
	public void testUpdateAndGetDescription() {
		testUpdateAndGetDescription(pt);
	}

	@Test
	public void testUpdateAndGetLastModified() {
		testUpdateAndGetLastModified(pt);
	}

	@Test
	public void testCompareTo() throws HandledException {
		PeriodicTask pt2 = new PeriodicTask(taskUID, created, status, title, location, startTime, endTime, null);
		assertEquals(0, pt.compareTo(pt2));
		pt.updateRecurrence(recurrence);
		pt2 = new PeriodicTask(taskUID, created, status, title, location, startTime, endTime, recurrence);
		assertEquals(0, pt.compareTo(pt2));
	}

	@Test
	public void testCheckAlert() {
		testCheckAlert(pt);
	}
	
	@Test public void testEquals() throws CloneNotSupportedException, HandledException{
		testEquals(pt);
	}

	@Override
	@Test
	public void testUpdateAndGetStatus() {
		assertEquals(Status.VEVENT_CONFIRMED, pt.getStatus());
		Status testStatus = new Status();
		pt.updateStatus(testStatus);
		assertEquals(testStatus, pt.getStatus());
	}

	@Test
	public void testDeleteAndIsDelete() {
		testDeleteAndIsDelete(pt);
	}

	@Override
	@Test
	public void testRestore() {
		pt.restore();
		assertEquals(Status.VEVENT_CONFIRMED, pt.getStatus());
		Date testDate = new Date(1,1,1);
		pt.updateCompleted(testDate);
		pt.restore();
		assertEquals(Status.VEVENT_CONFIRMED, pt.getStatus());
	}
}