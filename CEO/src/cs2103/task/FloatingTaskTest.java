package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.util.TestUtil;

public class FloatingTaskTest extends TaskTest {
	static FloatingTask ft;
	Uid taskUID = null;
	Date created = null; 
	Status status = null;
	String title = "Testing";
	Date complete = null;

	@Before
	public void setUp() throws Exception {
		ft = new FloatingTask(taskUID, created, status, title, complete);
		ft.updateDescription(null);
	}

	@Test
	public void testDeadlineTaskConstructor(){
		testFloatingTaskConstructionOne();
		testFloatingTaskConstructionTwo();
	}
	
	public void testFloatingTaskConstructionOne() {
		try{
			ft = new FloatingTask(taskUID, created, status, "", complete);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(), "A Non-empty title must be specified!");
		}
	}
	
	public void testFloatingTaskConstructionTwo() {
		try{
			ft = new FloatingTask(taskUID, created, status, title, complete);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	@Test
	public void testUpdateAndGetCompleted() {
		ft.updateCompleted(null);
		assertEquals(null, ft.getCompleted());
		Date testDate = new Date(1,1,1);
		ft.updateCompleted(testDate);
		DateTime testDate2 = new DateTime(testDate);
		assertTrue(ft.getCompleted().equals(testDate2));
	}
	
	@Test
	public void testConvert() throws HandledException {
		testConvert(ft);
	}
	
	@Test
	public void testClone() {
		try {
			FloatingTask task = (FloatingTask) ft.clone();
			assertTrue(TestUtil.compareTasks(task,ft));
		} catch (CloneNotSupportedException e){
			fail("Expected- Successful Clone");
		}
	}

	@Test
	public void testToSummary() {
		assertEquals("0. Testing\nType: Floating\tStatus: Needs Action\n", 
				ft.toSummary());
	}

	@Test
	public void testToDetail() {
		assertEquals("0. Testing\nType: Floating\tStatus: Needs Action"
				+ "\nDescription: \n", ft.toDetail());
		ft.updateDescription("Description");
		assertEquals("0. Testing\nType: Floating\tStatus: Needs Action"
				+ "\nDescription: Description\n", ft.toDetail());
	}

	@Test
	public void testMatches() {
		String keyword=null;
		assertEquals(ft.matches(keyword), true);
		keyword="";
		assertEquals(ft.matches(keyword), true);
		keyword="Testing";
		assertEquals(ft.matches(keyword), true);
		keyword="Coding";
		assertEquals(ft.matches(keyword), false);
		ft.updateDescription("Coding");
		assertEquals(ft.matches(keyword), true);
	}

	@Test
	public void testUpdateAndGetTaskID() {
		testUpdateAndGetTaskID(ft);
	}

	@Test
	public void testUpdateAndGetTitle() throws HandledException {
		testUpdateAndGetTitle(ft);
	}

	@Test
	public void testUpdateAndGetDescription() {
		testUpdateAndGetDescription(ft);
	}

	@Test
	public void testUpdateAndGetLastModified() {
		testUpdateAndGetLastModified(ft);
	}

	@Test
	public void testCheckAlert() {
		testCheckAlert(ft);
	}

	@Override
	public void testCompareTo() throws HandledException {
		FloatingTask ft2=new FloatingTask(taskUID, created, status, title, complete);
		assertEquals(0, ft.compareTo(ft2));
	}
	
	@Test public void testEquals() throws CloneNotSupportedException, HandledException{
		testEquals(ft);
	}
	
	@Test
	public void testUpdateAndGetStatus(){
		assertEquals(Status.VTODO_NEEDS_ACTION, ft.getStatus());
		Status testStatus = new Status();
		ft.updateStatus(testStatus);
		assertEquals(testStatus, ft.getStatus());
	}

	@Override
	@Test
	public void testDeleteAndIsDelete() {
		assertEquals(false, ft.isDeleted());
		ft.delete();
		assertEquals(true, ft.isDeleted());
	}

	@Override
	@Test
	public void testRestore() {
		ft.restore();
		assertEquals(Status.VTODO_NEEDS_ACTION, ft.getStatus());
		Date testDate = new Date(1,1,1);
		ft.updateCompleted(testDate);
		ft.restore();
		assertEquals(Status.VTODO_COMPLETED, ft.getStatus());
	}
}