package cs2103.task;

import static org.junit.Assert.*;

import java.util.Date;

import net.fortuna.ical4j.model.property.Uid;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.HandledException;

public class DeadlineTaskTest {
	DeadlineTask dlt;
	Uid taskUID;
	Date created; 
	String title;
	Date dueTime;
	boolean complete;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	@Test 
	public void testDeadlineTaskConstruction(){
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
			dlt=new DeadlineTask(null,null," ",new Date(2014,10,10),false);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Succesful Creation");
		}
	}
	
	@Test
	public void testDeadlineTaskOne() throws HandledException{
		dlt=new DeadlineTask(null,null,null,null,false);
		testAllMethods(dlt);
	}
	
	public void testAllMethods(DeadlineTask dltToTest){
		
	}
	
	@Test
	public void testUpdateComplete() {
		fail("Not yet implemented");
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
	public void testConvert() {
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
	public void testGetComplete() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDueTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDueTime() {
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

}
