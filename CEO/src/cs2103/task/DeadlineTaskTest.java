package cs2103.task;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public class DeadlineTaskTest extends TaskTest{
	static DeadlineTask dlt;
	String taskUID = null;
	Date created = null; 
	Status status = null;
	String title = "Testing";
	Date complete = null;
	static Date dueTime = new Date(1000, 1, 1);

	@Before
	public void setUp() throws Exception {
		dlt = new DeadlineTask(taskUID, created, status, title, dueTime, complete);
		dlt.updateDescription(null);
	}
	
	@Test 
	public void testDeadlineTaskConstructor(){
		testDeadlineTaskConstructionOne();
		testDeadlineTaskConstructionTwo();
		testDeadlineTaskConstructionThree();
		testDeadlineTaskConstructionFour();
	}
	
	public void testDeadlineTaskConstructionOne() {
		try{
			dlt = new DeadlineTask(taskUID, created, status, null, dueTime, complete);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(), "A Non-empty title must be specified!");
		}
	}
	
	public void testDeadlineTaskConstructionTwo() {
		try{
			dlt = new DeadlineTask(taskUID, created, status, " ", null, complete);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(), "Your input time cannot be parsed, please check your input and try again!");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void testDeadlineTaskConstructionThree() {
		try{
			dlt = new DeadlineTask(taskUID, created, status,  " ", dueTime, complete);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	public void testDeadlineTaskConstructionFour() {
		try{
			dlt = new DeadlineTask(taskUID, created, status,  " ", dueTime, new DateTime());
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	@Test
	public void testUpdateAndGetComplete() {
		dlt.updateCompleted(null);
		assertEquals(null, dlt.getCompleted());
		Date testDate = new Date(1,1,1);
		dlt.updateCompleted(testDate);
		DateTime testDate2 = new DateTime(testDate);
		assertTrue(dlt.getCompleted().equals(testDate2));
	}

	@Test
	public void testUpdateAndGetDueTime() throws HandledException{
		@SuppressWarnings("deprecation")
		Date newDate = new Date(1000, 1, 2);
		try {
			dlt.updateDueTime(newDate);
		} catch (HandledException e){
			fail("Expected- Successful Update");
		}
		assertTrue(dlt.getDueTime().compareTo(newDate) == 0);
		try {
			dlt.updateDueTime(null);
			fail("Expected- Handled Exception");
		} catch (HandledException e){
		}
		assertTrue(dlt.getDueTime().compareTo(newDate) == 0);
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
		try {
			DeadlineTask task = (DeadlineTask) dlt.clone();
			assertTrue(TestUtil.compareTasks(task, dlt));
		} catch (CloneNotSupportedException e){
			fail("Expected- Successful Clone");
		}
	}

	@Test
	public void testToSummary() {
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Feb-2900 00:00:00\n", dlt.toSummary());
	}

	@Test
	public void testToDetail() {
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Feb-2900 00:00:00\nDescription: \n", dlt.toDetail());
		dlt.updateDescription("Description");
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Feb-2900 00:00:00\nDescription: Description\n", dlt.toDetail());
	}

	@Test
	public void testCheckPeriod() {
		Date[] time = new Date[2];
		assertEquals(dlt.checkPeriod(time), true);
		time[0] = new Date(1000, 1, 2);
		assertEquals(dlt.checkPeriod(time), true);
		time[0] = null;
		time[1] = new Date(2011, 1, 1);
		assertEquals(dlt.checkPeriod(time), false);
	}

	@Test
	public void testMatches() {
		String keyword = null;
		assertEquals(dlt.matches(keyword), true);
		keyword="";
		assertEquals(dlt.matches(keyword), true);
		keyword="Testing";
		assertEquals(dlt.matches(keyword), true);
		keyword="Coding";
		assertEquals(dlt.matches(keyword), false);
		dlt.updateDescription("Coding");
		assertEquals(dlt.matches(keyword), true);
	}
	
	@Test
	public void testCompareTo() throws HandledException {
		DeadlineTask dlt2 = new DeadlineTask(taskUID, created, status, title, dueTime, complete);
		dlt2.updateDescription(null);
		assertEquals(0, dlt.compareTo(dlt2));
	}
	
	@Test
	public void testConvert() throws HandledException {
		testConvert(dlt);
	}

	@Test
	public void testUpdateAndGetTitle() throws HandledException {
		testUpdateAndGetTitle(dlt);
	}

	@Test
	public void testUpdateAndGetDescription() {
		testUpdateAndGetDescription(dlt);
	}

	@Test
	public void testUpdateAndGetTaskID() {
		testUpdateAndGetTaskID(dlt);
	}

	@Test
	public void testUpdateAndGetLastModified() {
		testUpdateAndGetLastModified(dlt);
	}

	@Test
	public void testCheckAlert() {
		testCheckAlert(dlt);
	}
	
	@Test
	public void testEquals() throws CloneNotSupportedException, HandledException{
		testEquals(dlt);
	}

	@Override
	@Test
	public void testUpdateAndGetStatus() {
		assertEquals(Status.VTODO_NEEDS_ACTION, dlt.getStatus());
		Status testStatus = new Status();
		dlt.updateStatus(testStatus);
		assertEquals(testStatus, dlt.getStatus());
	}

	@Test
	public void testDeleteAndIsDelete() {
		testDeleteAndIsDelete(dlt);
	}

	@Override
	@Test
	public void testRestore() {
		dlt.restore();
		assertEquals(Status.VTODO_NEEDS_ACTION, dlt.getStatus());
		Date testDate = new Date(1,1,1);
		dlt.updateCompleted(testDate);
		dlt.restore();
		assertEquals(Status.VTODO_COMPLETED, dlt.getStatus());
		
	}
}