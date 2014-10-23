package cs2103.task;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.property.Uid;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.HandledException;

public class DeadlineTaskTest extends TaskTest{
	static DeadlineTask dlt;
	Uid taskUID = null;
	Date created = null; 
	String title = "Testing";
	boolean complete = false;
	static Date dueTime=new Date(1000,1,1);

	@Before
	public void setUp() throws Exception {
		dlt=new DeadlineTask(taskUID,created,title,dueTime,complete);
		dlt.updateDescription(null);
	}
	
	@Test 
	public void testDeadlineTaskConstructor(){
		testDeadlineTaskConstructionOne();
		testDeadlineTaskConstructionTwo();
		testDeadlineTaskConstructionThree();
	}
	
	public void testDeadlineTaskConstructionOne() {
		try{
			dlt=new DeadlineTask(taskUID,created,null,dueTime,complete);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(),"A Non-empty title must be specified!");
		}
	}
	
	public void testDeadlineTaskConstructionTwo() {
		try{
			dlt=new DeadlineTask(taskUID,created," ",null,complete);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.getErrorMsg(),"Your input time cannot be parsed, please check your input and try again!");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void testDeadlineTaskConstructionThree() {
		try{
			dlt=new DeadlineTask(taskUID,created," ",dueTime,complete);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testDeadlineTask() throws HandledException, CloneNotSupportedException{
		testAllMethods();
	}
	
	public void testAllMethods() throws HandledException, CloneNotSupportedException{
		testUpdateAndGetComplete();
		testUpdateAndGetDueTime();
		testConvert();
		testClone();
		testToSummary();
		testToDetail();
		testCheckPeriod();
		testMatches();
	}
	
	@Test
	public void testUpdateAndGetComplete() {
		dlt.updateComplete(true);
		assertEquals(true,dlt.getComplete());
		dlt.updateComplete(false);
		assertEquals(false,dlt.getComplete());
	}

	@Test
	public void testUpdateAndGetDueTime() throws HandledException{
		@SuppressWarnings("deprecation")
		Date newDate=new Date(1000,1,2);
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
	public void testConvert() throws HandledException {
		testConvert(dlt);
	}

	@Test
	public void testClone() throws CloneNotSupportedException {
		try {
			DeadlineTask task=(DeadlineTask) dlt.clone();
			assertTrue(compareDeadlineTasks(task,dlt));
		} catch (CloneNotSupportedException e){
			fail("Expected- Successful Clone");
		}
	}

	@Test
	public void testToSummary() {
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Feb-2900 00:00:00\n",dlt.toSummary());
	}

	@Test
	public void testToDetail() {
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Feb-2900 00:00:00\nDescription: \n",dlt.toDetail());
		dlt.updateDescription("Description");
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 01-Feb-2900 00:00:00\nDescription: Description\n",dlt.toDetail());
	}

	@Test
	public void testCheckPeriod() {
		Date[] time= new Date[2];
		assertEquals(dlt.checkPeriod(time),true);
		time[0]=new Date(2011,1,1);
		assertEquals(dlt.checkPeriod(time),false);
		time[0]=null;
		time[1]=new Date(2011,1,1);
		assertEquals(dlt.checkPeriod(time),false);
	}

	@Test
	public void testMatches() {
		String keyword=null;
		assertEquals(dlt.matches(keyword),true);
		keyword="";
		assertEquals(dlt.matches(keyword),true);
		keyword="Testing";
		assertEquals(dlt.matches(keyword),true);
		keyword="Coding";
		assertEquals(dlt.matches(keyword),false);
		dlt.updateDescription("Coding");
		assertEquals(dlt.matches(keyword),true);
	}
	
	@Test
	public void testCompareTo() throws HandledException {
		DeadlineTask dlt2=new DeadlineTask(null,null,"Testing",dueTime,false);
		assertEquals(0,dlt.compareTo(dlt2));
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

}