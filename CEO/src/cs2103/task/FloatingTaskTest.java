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

public class FloatingTaskTest extends TaskTest {
	static FloatingTask ft;
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
		ft=new FloatingTask(null,null,"Testing",false);
		ft.updateDescription(null);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDeadlineTaskConstructor(){
		testFloatingTaskConstructionOne();
		testFloatingTaskConstructionTwo();
	}
	
	public void testFloatingTaskConstructionOne() {
		try{
			ft=new FloatingTask(null,null,"",false);
			fail("Expected- Handled Exception");
		} catch(HandledException e){
			assertEquals(e.printErrorMsg(),"A Non-empty title must be specified!");
		}
	}
	
	public void testFloatingTaskConstructionTwo() {
		try{
			ft=new FloatingTask(null,null,"Testing",false);
			assertTrue(true);
		} catch(HandledException e){
			fail("Expected- Successful Creation");
		}
	}
	
	@Test
	public void testUpdateAndGetComplete() {
		ft.updateComplete(true);
		assertEquals(true,ft.getComplete());
		ft.updateComplete(false);
		assertEquals(false,ft.getComplete());
	}
	
	@Test
	public void testConvert() throws HandledException {
		testConvert(ft);
	}

	@Test
	public void testClone() {
		try {
			FloatingTask task=(FloatingTask) ft.clone();
			assertTrue(compareFloatingTasks(task,ft));
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
				+ "\nDescription: \n",ft.toDetail());
		ft.updateDescription("Description");
		assertEquals("0. Testing\nType: Floating\tStatus: Needs Action"
				+ "\nDescription: Description\n",ft.toDetail());
	}

	@Test
	public void testMatches() {
		String keyword=null;
		assertEquals(ft.matches(keyword),true);
		keyword="";
		assertEquals(ft.matches(keyword),true);
		keyword="Testing";
		assertEquals(ft.matches(keyword),true);
		keyword="Coding";
		assertEquals(ft.matches(keyword),false);
		ft.updateDescription("Coding");
		assertEquals(ft.matches(keyword),true);
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
		FloatingTask ft2=new FloatingTask(null,null,"Testing",false);
		assertEquals(0,ft.compareTo(ft2));
	}

}
