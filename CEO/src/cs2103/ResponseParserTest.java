package cs2103;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class ResponseParserTest {

	
	@Test
	public void testParseAllListResponseWithTasks() throws CEOException, ParseException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(new FloatingTask(null, "Eating", false));
		taskList.add(new DeadlineTask(null, "Pooping", stringToDate("2014/09/28/23:59"), false));
		taskList.add(new PeriodicTask(null, "Drinking", null, stringToDate("2014/09/28/23:59"),
				stringToDate("2014/10/28/23:59"), null));
		assertEquals(//"Here shows task list for type ALL\n"
				 "1. Eating\nType: Floating\t\tStatus: Needs Action\n"
				+ "2. Pooping\nType: Deadline\t\tStatus: Needs Action	Due At: Sep 28, 2014 11:59:00 PM\n"
				+ "3. Drinking\nType: Periodic\t\tFrom: Sep 28, 2014 11:59:00 PM To Oct 28, 2014 11:59:00 PM"
				, 
				ResponseParser.parseAllListResponse(taskList));
	}
	
	@Test
	public void testParseFloatingListResponseNoTasks() {
		ArrayList<FloatingTask> taskList = new ArrayList<FloatingTask>();
		assertEquals("The task list is empty", ResponseParser.parseFloatingListResponse(taskList));
	}
	
	@Test
	public void testParseFloatingListResponseWithTasks() throws CEOException {
		ArrayList<FloatingTask> taskList = new ArrayList<FloatingTask>();
		taskList.add(new FloatingTask(null, "Eating", false));
		taskList.add(new FloatingTask(null, "Drinking", false));
		assertEquals("0. Eating\nType: Floating\t\tStatus: Needs Action\n"
				+ "0. Drinking\nType: Floating\t\tStatus: Needs Action"
				, ResponseParser.parseFloatingListResponse(taskList));
	}
	
	@Test
	public void testParseDeadlineListResponseNoTasks() {
		ArrayList<DeadlineTask> taskList = new ArrayList<DeadlineTask>();
		assertEquals("The task list is empty", ResponseParser.parseDeadlineListResponse(taskList));
	}
	
	@Test
	public void testParseDeadlineListResponseWithTasks() throws CEOException {
		ArrayList<DeadlineTask> taskList = new ArrayList<DeadlineTask>();
		taskList.add(new DeadlineTask(null, "Pooping", stringToDate("2014/09/28/23:59"), false));
		taskList.add(new DeadlineTask(null, "Wiping", stringToDate("2014/09/29/23:59"), false));
		assertEquals("0. Pooping\nType: Deadline\t\tStatus: Needs Action	Due At: Sep 28, 2014 11:59:00 PM\n" 
				+ "0. Wiping\nType: Deadline\t\tStatus: Needs Action	Due At: Sep 29, 2014 11:59:00 PM\n"
				, ResponseParser.parseDeadlineListResponse(taskList));
	}
	
	@Test
	public void testParsePeriodicListResponseNoTasks() {
		
	}
	
	@Test
	public void testParsePeriodicListResponseWithTasks() {
		
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
				+ "0. Eating\nType: Floating\t\tStatus: Needs Action\n"
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
				+ "0. Eating\nType: Floating\t\tStatus: Needs Action\n"
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