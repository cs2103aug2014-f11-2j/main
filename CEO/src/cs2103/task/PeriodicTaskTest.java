package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;

import org.junit.Before;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public class PeriodicTaskTest extends EventTaskTest{
	private static final String contant_TestLocation = "place";
	private static final String contant_TestFrequency = "HOURLY";
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

	@Override
	protected PeriodicTask getConcrete() {
		return pt;
	}
	
	@Before
	public void setUp() throws Exception {
		pt = new PeriodicTask(this.taskUID, this.status, this.startTime, this.endTime);
		pt.updateTitle(this.title);
		pt.updateDescription(this.description);
		pt.updateLocation(this.location);
		pt.updateRecurrence(this.recurrence);
		pt.updateLastModified(null);
	}

	@Test 
	public void testPeriodicTaskConstructor() throws HandledException{
		testPeriodicTaskConstructionOne();
		testPeriodicTaskConstructionTwo();
		testPeriodicTaskConstructionThree();
	}
		
	public void testPeriodicTaskConstructionOne() throws HandledException {
		exception.expect(HandledException.class);
		pt = new PeriodicTask(this.taskUID, this.status, null, null);
	}
	
	public void testPeriodicTaskConstructionTwo() throws HandledException {
		exception.expect(HandledException.class);
		DateTime newStartTime = new DateTime(1);
		DateTime newEndTime = new DateTime(0);
		pt = new PeriodicTask(this.taskUID, this.status, newStartTime, newEndTime);
	}

	public void testPeriodicTaskConstructionThree() throws HandledException {
		pt = new PeriodicTask(this.taskUID, this.status, this.startTime, this.endTime);
		assertTrue(true);
	}
	
	@Test
	public void testUpdateAndGetLocation() {
		String location = contant_TestLocation;
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
	public void testClone() throws CloneNotSupportedException {
		PeriodicTask task = (PeriodicTask) pt.clone();
		assertTrue(TestUtil.compareTasks(task, pt));
		
	}

	@Test
	public void testToSummary() {
		assertEquals("0. Testing\nType: Periodic\t"
				+ "From: 01-Jan-1970 07:30:01 To 01-Jan-1970 07:30:01\n", pt.toSummary());
		pt.updateRecurrence(recurrence);
		assertEquals("0. Testing\nType: Recurring\t"
				+ "From: 01-Jan-1970 07:30:01 To 01-Jan-1970 07:30:01\n", pt.toSummary());
	}

	@Test
	public void testToDetail() {
		assertEquals("0. Testing\nType: Periodic\t"
				+ "From: 01-Jan-1970 07:30:01 To 01-Jan-1970 07:30:01\n"
				+ "Location: Location\nDescription: \n", pt.toDetail());
		pt.updateDescription("Description");
		assertEquals("0. Testing\nType: Periodic\t"
				+ "From: 01-Jan-1970 07:30:01 To 01-Jan-1970 07:30:01\n"
				+ "Location: Location\nDescription: Description\n", pt.toDetail());
		pt.updateRecurrence(recurrence);
		assertEquals("0. Testing\nType: Recurring\t"
				+ "From: 01-Jan-1970 07:30:01 To 01-Jan-1970 07:30:01\n"
				+ "Recurrence: 1 HOURLY\n"
				+ "Location: Location\nDescription: Description\n", pt.toDetail());
		
	}

	@Test
	public void testCheckPeriod() {
		Date[] time = new Date[2];
		assertEquals(pt.checkPeriod(time), true);
		time[0] = new DateTime(1001);
		assertEquals(pt.checkPeriod(time), false);
		time[0] = null;
		time[1] = new DateTime(1002);
		assertEquals(pt.checkPeriod(time), true);
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
	public void testUpdateTimeFromRecur() throws HandledException {
		PeriodicTask pt2 = pt.updateTimeFromRecur();
		assertTrue(pt2 == null);
		pt.updateRecurrence(recurrence);
		pt2 = pt.updateTimeFromRecur();
		assertTrue(TestUtil.compareTasks(pt, pt2));
		pt.updateTime(new DateTime(0), new DateTime(1));
		pt2 = pt.updateTimeFromRecur();
		DateTime now = new DateTime();
		Date startDate = (pt.getRecurrence().getNextDate(new DateTime(pt.getStartTime()), now));
		pt.updateTime((pt.getRecurrence().getNextDate(new DateTime(pt.getStartTime()), now)),
				new Date(pt.getEndTime().getTime() - pt.getStartTime().getTime() + startDate.getTime()));
		assertTrue(TestUtil.compareTasks(pt, pt2));	
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
		Date testDate = new DateTime(1);
		pt.updateCompleted(testDate);
		pt.restore();
		assertEquals(Status.VEVENT_CONFIRMED, pt.getStatus());
	}
}