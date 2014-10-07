package cs2103;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResponseParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		//ArrayList<Task> taskList = new ArrayList<Task>();
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testParseAllListResponseThreeTasks() throws CEOException, ParseException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(new FloatingTask(null, "Eating", false));
		taskList.add(new DeadlineTask(null, "Pooping", stringToDate("2014/09/28/23:59"), false));
		taskList.add(new PeriodicTask(null, "Drinking", null, stringToDate("2014/09/28/23:59"),
				stringToDate("2014/10/28/23:59"), null));
		assertEquals("Here shows task list for type ALL\n"
				+ "0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n"
				+ "0. Pooping\nType: Deadline\t\t\t\t\tStatus: Needs Action	Due At: Sep 28, 2014 11:59:00 PM\n\n"
				+ "0. Drinking\nType: Periodic\t\t\t\t\tFrom: Sep 28, 2014 11:59:00 PM To Oct 28, 2014 11:59:00 PM"
				, 
				ResponseParser.parseAllListResponse(taskList));
	}
	@Test
	public void testParseFloatingListResponse() {
		
	}
	
	@Test
	public void testParseDeadlineListResponse() {
		
	}
	
	@Test
	public void testParsePeriodicListResponse() {
		
	}
	
	@Test 
	public void testAlertDeadline() {
		
	}
	
	@Test
	public void testAlertPeriodic(){
		
	}
	
	@Test 
	public void testParseShowDetailResponseNull() {
		Task task = null;
		int taskID = 1;
		try {
			assertEquals(ResponseParser.parseShowDetailResponse(task, taskID), 
					"Unable to show detail for task 1");
		} catch (CEOException e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testParseShowDetailResponseTaskWithNoDetails() throws CEOException {
		Task task = new FloatingTask(null, "Eating", false);
		int taskID = 1;
		assertEquals("The details for Task 1:\n"
				+ "0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n"
				+ "Location: null\n"
				+ "Description: null",
				ResponseParser.parseShowDetailResponse(task, taskID));		
	}
	
	@Test 
	public void testParseShowDetailResponseTaskWithDetails() throws CEOException {
		Task task = new FloatingTask(null, "Eating", false);
		task.updateDescription("Eating Chicken Rice");
		int taskID = 1;
		assertEquals("The details for Task 1:\n"
				+ "0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n"
				+ "Location: Hawker Center\n"
				+ "Description: Eating Chicken Rice",
				ResponseParser.parseShowDetailResponse(task, taskID));		
	}
	
	// Used to create tasks for testing
	private static Date stringToDate(String timeString) throws CEOException{
		if (timeString == null){
			return null;
		}
		try {
			TimeZone tz=TimeZone.getDefault();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd/HH:mm");
			dateFormat.setTimeZone(tz);
			return dateFormat.parse(timeString);
		} catch (ParseException e) {
			throw new CEOException(CEOException.INVALID_TIME);
		}
	}
}