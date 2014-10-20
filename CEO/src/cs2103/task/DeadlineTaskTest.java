package cs2103.task;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.property.Uid;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.HandledException;

public class DeadlineTaskTest {
	static DeadlineTask dlt;
	Uid taskUID;
	Date created; 
	String title;
	Date dueTime;
	boolean complete;
	static Date testDate=new Date(2014,10,10);

	@Before
	public void setUp() throws Exception {
		dlt=new DeadlineTask(null,null," ",testDate,false);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test 
	public void testDeadlineTaskConstructor(){
		testDeadlineTaskConstructionOne();
		testDeadlineTaskConstructionTwo();
		testDeadlineTaskConstructionThree();
	}
	
	public void testDeadlineTaskConstructionOne() {
		try{
			dlt=new DeadlineTask(null,null,null,null,false);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.printErrorMsg(),"A Non-empty title must be specified!");
		}
	}
	
	public void testDeadlineTaskConstructionTwo() {
		try{
			dlt=new DeadlineTask(null,null," ",null,false);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.printErrorMsg(),"Your input time cannot be parsed, please check your input and try again!");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void testDeadlineTaskConstructionThree() {
		try{
			dlt=new DeadlineTask(null,null," ",testDate,false);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testDeadlineTaskOne() throws HandledException{
		testAllMethods();
	}
	
	public void testAllMethods() throws HandledException{
		testUpdateAndGetComplete();
		testUpdateAndGetDueTimeOne();
		testUpdateAndGetDueTimeTwo();
		testConvert();
	}
	
	@Test
	public void testUpdateAndGetComplete() {
		dlt.updateComplete(true);
		assertEquals(true,dlt.getComplete());
		dlt.updateComplete(false);
		assertEquals(false,dlt.getComplete());
	}

	@Test
	public void testUpdateAndGetDueTimeOne() throws HandledException{
		@SuppressWarnings("deprecation")
		Date newDate=new Date(2014,10,11);
		try {
			dlt.updateDueTime(newDate);
		} catch (HandledException e){
			fail("Expected- Successful Update");
		}
		assertTrue(dlt.getDueTime().compareTo(newDate)==0);
	}

	@Test
	public void testUpdateAndGetDueTimeTwo() throws HandledException{
		try {
			dlt.updateDueTime(null);
			fail("Expected- Handled Exception");
		} catch (HandledException e){
		}
		assertTrue(dlt.getDueTime().compareTo(testDate)==0);
	}
	
	@Test
	public void testConvert() throws HandledException {
		Date[] time=null;
		try{
			dlt.convert(time);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.printErrorMsg(),"Your input time cannot be parsed, please check your input and try again!");
		}
		time= new Date[2];
		time[0]=null;
		time[1]=null;
		Task task = dlt.convert(time);
		assertTrue(task instanceof FloatingTask);
		task= dlt.convert(time);
		Task taskExpected = new FloatingTask((dlt).getTaskUID(),dlt.getCreated(), 
				dlt.getTitle(), false);
		taskExpected.updateDescription(dlt.getDescription());
		assertTrue(compareFloatingTasks((FloatingTask) task,(FloatingTask) taskExpected));
		
		time[0]= new Date(2014,1,1);
		task =dlt.convert(time);
		assertTrue(task instanceof DeadlineTask);
		taskExpected = new DeadlineTask(dlt.getTaskUID(), dlt.getCreated(), 
				dlt.getTitle(), time[0], dlt.getComplete());
		taskExpected.updateDescription(dlt.getDescription());
		assertTrue(compareDeadlineTasks((DeadlineTask) task,(DeadlineTask) taskExpected));
		
		time[1]= new Date(2014,2,1);
		task=dlt.convert(time);
		assertTrue(task instanceof PeriodicTask);
		taskExpected= new PeriodicTask(dlt.getTaskUID(), dlt.getCreated(), 
				dlt.getTitle(), null, time[0], time[1], null);
		taskExpected.updateDescription(dlt.getDescription());
		
	}

	@Test
	public void testUpdateLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateRecurrence() {
		fail("Not yet implemented");
	}

	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	@Test
	public void testToSummary() {
		fail("Not yet implemented");
	}

	@Test
	public void testToDetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testToComponent() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckPeriod() {
		fail("Not yet implemented");
	}

	@Test
	public void testMatches() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeadlineTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetComparator() {
		fail("Not yet implemented");
	}

	@Test
	public void testTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaskID() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTaskUID() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTitle() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDescription() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCreated() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLastModified() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateTaskID() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateTitle() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDescription() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateLastModified() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddCommonProperty() {
		fail("Not yet implemented");
	}

	@Test
	public void testDateToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckAlert() {
		fail("Not yet implemented");
	}
	
	private boolean compareFloatingTasks(FloatingTask dlt1, FloatingTask dlt2){
		if (dlt1.getComplete()!=dlt2.getComplete()) {
			return false;
		} else if (dlt1.getCreated()!=dlt2.getCreated()) {
			return false;
		} else if (dlt1.getDescription()!=dlt2.getDescription()) {
			return false;
		} else if (dlt1.getTitle()!=dlt2.getTitle()) {
			return false;
		} else if (dlt1.getLastModified()!=dlt2.getLastModified()){
			return false;
 		} else if (dlt1.getTaskID()!=dlt1.getTaskID()) {
 			return false;
 		} 
		return true;
	}
	
	private boolean compareDeadlineTasks(DeadlineTask dlt1, DeadlineTask dlt2){
		if (dlt1.getComplete()!=dlt2.getComplete()) {
			return false;
		} else if (dlt1.getCreated()!=dlt2.getCreated()) {
			return false;
		} else if (dlt1.getDescription()!=dlt2.getDescription()) {
			return false;
		} else if (dlt1.getTitle()!=dlt2.getTitle()) {
			return false;
		} else if (dlt1.getDueTime()!=dlt2.getDueTime()){
			return false;
		} else if (dlt1.getLastModified()!=dlt2.getLastModified()){
			return false;
 		} else if (dlt1.getTaskID()!=dlt1.getTaskID()) {
 			return false;
 		} 
		return true;
	}
	
	private boolean comparePeriodicTasks(PeriodicTask dlt1,PeriodicTask dlt2){

		if (dlt1.getComplete()!=dlt2.getComplete()) {
			return false;
		} else if (dlt1.getCreated()!=dlt2.getCreated()) {
			return false;
		} else if (dlt1.getDescription()!=dlt2.getDescription()) {
			return false;
		} else if (dlt1.getTitle()!=dlt2.getTitle()) {
			return false;
		} else if (dlt1.getDueTime()!=dlt2.getDueTime()){
			return false;
		} else if (dlt1.getLastModified()!=dlt2.getLastModified()){
			return false;
 		} else if (dlt1.getTaskID()!=dlt1.getTaskID()) {
 			return false;
 		} 
		return true
	}
	
}
