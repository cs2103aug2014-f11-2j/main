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
		dlt=new DeadlineTask(null,null,"Testing",testDate,false);
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
	public void testDeadlineTask() throws HandledException, CloneNotSupportedException{
		testAllMethods();
	}
	
	public void testAllMethods() throws HandledException, CloneNotSupportedException{
		testUpdateAndGetComplete();
		testUpdateAndGetDueTimeOne();
		testUpdateAndGetDueTimeTwo();
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
		assertTrue(comparePeriodicTasks((PeriodicTask) task,(PeriodicTask) taskExpected));
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
				+ "Due At: 10-Nov-3914 00:00:00\n",dlt.toSummary());
	}

	@Test
	public void testToDetail() {
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 10-Nov-3914 00:00:00\nDescription: \n",dlt.toDetail());
		dlt.updateDescription("Description");
		assertEquals("0. Testing\nType: Deadline\tStatus: Needs Action\t"
				+ "Due At: 10-Nov-3914 00:00:00\nDescription: Description\n",dlt.toDetail());
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
	public void testUpdateAndGetTitle() throws HandledException {
		try {
			String newTitle="Coding";
			dlt.updateTitle(newTitle);
			assertEquals("Coding",dlt.getTitle());
		} catch (HandledException e) {
			fail("");
		}
		try {
			String newTitle="";
			dlt.updateTitle(newTitle);
			fail("");
		} catch (HandledException e) {
			assertEquals("A Non-empty title must be specified!",e.printErrorMsg());
		}
	}

	@Test
	public void testUpdateAndGetDescription() {
		String description= "New Description";
		dlt.updateDescription(description);
		assertEquals(description,dlt.getDescription());
	}

	@Test
	public void testUpdateAndGetTaskID() {
		int taskID=1;
		dlt.updateTaskID(taskID);
		assertEquals(dlt.getTaskID(),taskID);
	}

	@Test
	public void testUpdateAndGetLastModified() {
		assertEquals(null,dlt.getLastModified());
		Date newDate= new Date(1000,1,1);
		dlt.updateLastModified(newDate);
		assertEquals(newDate,dlt.getLastModified());
	}

	@Test
	public void testCompareTo() throws HandledException {
		DeadlineTask dlt2=new DeadlineTask(null,null,"Testing",testDate,false);
		assertEquals(0,dlt.compareTo(dlt2));
	}

	@Test
	public void testCheckAlert() {
		assertFalse(dlt.checkAlert());
	}
	
	private boolean compareFloatingTasks(FloatingTask dlt1, FloatingTask dlt2){
		if (dlt1.getComplete()!=dlt2.getComplete()) {
			return false;
		} 
		return compareCommonValuesInTasks(dlt1,dlt2);
	}
	
	private boolean compareDeadlineTasks(DeadlineTask dlt1, DeadlineTask dlt2){
		if (dlt1.getComplete()!=dlt2.getComplete()) {
			return false;
		} else if (dlt1.getDueTime()!=dlt2.getDueTime()){
			return false;
		} 
		return compareCommonValuesInTasks(dlt1,dlt2);
	}
	
	private boolean comparePeriodicTasks(PeriodicTask dlt1,PeriodicTask dlt2){
		if (dlt1.getStartTime()!=dlt2.getStartTime()) {
			return false;
		} else if (dlt1.getRecurrence()!=dlt2.getRecurrence()){
			return false;
		} else if (dlt1.getLastModified()!=dlt2.getLastModified()){
			return false;
 		} else if (dlt1.getEndTime()!=dlt2.getEndTime()){
 			return false;
 		} else if (dlt1.getLocation()!=dlt2.getLocation()){
 			return false;
 		}
		return compareCommonValuesInTasks(dlt1,dlt2);
	}
	
	private boolean compareCommonValuesInTasks(Task dlt1,Task dlt2){
		if (dlt1.getLastModified()!=dlt2.getLastModified()){
			return false;
 		} else if (dlt1.getCreated()!=dlt2.getCreated()) {
			return false;
		} else if (dlt1.getDescription()!=dlt2.getDescription()) {
			return false;
		} else if (dlt1.getTitle()!=dlt2.getTitle()) {
			return false;
		} else if (dlt1.getTaskID()!=dlt1.getTaskID()) {
 			return false;
 		} 
		return true;
	}
	
}
