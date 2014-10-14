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
	public void testParseAllListResponseNoTasks() throws ParseException, HandledException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		assertEquals("The task list is empty", ResponseParser.parseListResponse(taskList));
	}
	
	@Test
	public void testParseAllListResponseWithTasks() throws ParseException, HandledException {
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
				ResponseParser.parseListResponse(taskList));
	}
	
	@Test
	public void testParseListResponseThreeTasks() throws ParseException, HandledException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(new FloatingTask(null, "Eating", false));
		assertEquals("Here shows task list for type ALL\n"
				+ "0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n"
				+ "0. Pooping\nType: Deadline\t\t\t\t\tStatus: Needs Action	Due At: Sep 28, 2014 11:59:00 PM\n\n"
				+ "0. Drinking\nType: Periodic\t\t\t\t\tFrom: Sep 28, 2014 11:59:00 PM To Oct 28, 2014 11:59:00 PM"
				, 
				ResponseParser.parseListResponse(taskList));
	}
	
	@Test 
	public void testAlertDeadline() {
		
	}
	
	@Test
	public void testAlertPeriodic(){
		
	}
	
	@Test 
	public void testParseShowDetailResponseNull() throws HandledException {
		Task task = null;
		assertEquals(ResponseParser.parseShowDetailResponse(task), 
					"Unable to show detail for task 1");
	}
	
	@Test 
	public void testParseShowDetailResponseTaskWithNoDetails() throws HandledException{
		Task task = new FloatingTask(null, "Eating", false);
		assertEquals("The details for Task 1:\n"
				+ "1. Eating\nType: Floating\t\tStatus: Needs Action\n"
				+ "Location: null\n"
				+ "Description: null",
				ResponseParser.parseShowDetailResponse(task));		
	}
	
	@Test 
	public void testParseShowDetailResponseTaskWithDetails() throws HandledException {
		Task task = new FloatingTask(null, "Eating", false);
		task.updateDescription("Eating Chicken Rice");
		assertEquals("The details for Task 1:\n"
				+ "1. Eating\nType: Floating\t\tStatus: Needs Action\n"
				+ "Location: Hawker Center\n"
				+ "Description: Eating Chicken Rice",
				ResponseParser.parseShowDetailResponse(task));		
	}
	
	private static Date stringToDate(String timeString) throws HandledException{
		if (timeString == null){
			return null;
		}
		try {
			TimeZone tz=TimeZone.getDefault();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			dateFormat.setTimeZone(tz);
			return dateFormat.parse(timeString);
		} catch (ParseException e) {
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		}
	}
}
