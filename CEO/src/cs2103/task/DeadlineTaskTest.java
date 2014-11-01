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

	@Before
	public void setUp() throws Exception {
		dlt = new DeadlineTask(this.taskUID, this.status, this.dueTime);
		dlt.updateTitle(this.title);
		dlt.updateDescription(this.description);
		dlt.updateLocation(this.location);
		dlt.updateRecurrence(this.recurrence);
		dlt.updateLastModified(null);
	}
	
	@Test 
	public void testDeadlineTaskConstructor() throws HandledException{
		testDeadlineTaskConstructionOne();
		testDeadlineTaskConstructionTwo();
		testDeadlineTaskConstructionThree();
	}
		
	public void testDeadlineTaskConstructionOne() throws HandledException {
		exception.expect(HandledException.class);
		dlt = new DeadlineTask(taskUID, created, status, " ", null, complete);
	}
	
	public void testDeadlineTaskConstructionTwo() throws HandledException {
		dlt = new DeadlineTask(taskUID, created, status,  " ", dueTime, complete);
		assertTrue(true);
	}
	
	public void testDeadlineTaskConstructionThree() throws HandledException {
		dlt = new DeadlineTask(taskUID, created, status,  " ", dueTime, new DateTime());
		assertTrue(true);	
	}
	

	@Test
	public void testUpdateAndGetDueTime() throws HandledException{
		Date newDate = new DateTime();
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
		DeadlineTask task = (DeadlineTask) dlt.clone();
		assertTrue(TestUtil.compareTasks(task, dlt));
	}

	@Test
	public void testToSummary() {
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Jan-1970 07:30:01\n", dlt.toSummary());
	}

	@Test
	public void testToDetail() {
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Jan-1970 07:30:01\nDescription: \n", dlt.toDetail());
		dlt.updateDescription("Description");
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Jan-1970 07:30:01\nDescription: Description\n", dlt.toDetail());
	}

	@Test
	public void testCheckPeriod() {
		Date[] time = new Date[2];
		assertEquals(dlt.checkPeriod(time), true);
		time[0] = new DateTime(1001);
		assertEquals(dlt.checkPeriod(time), false);
		time[0] = null;
		time[1] = new DateTime(1002);
		assertEquals(dlt.checkPeriod(time), true);
	}

	@Test
	public void testConvert() throws HandledException {
		testConvert(dlt);
	}

	@Override
	@Test
	public void testUpdateAndGetStatus() {
		assertEquals(Status.VTODO_NEEDS_ACTION, dlt.getStatus());
		Status testStatus = new Status();
		dlt.updateStatus(testStatus);
		assertEquals(testStatus, dlt.getStatus());
	}
}