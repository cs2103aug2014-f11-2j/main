package cs2103;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;

public class ResponseParserTest {
	
	@Test
	public void testParseListResponseThreeTasks() throws ParseException, HandledException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(new FloatingTask(null, "Eating", false));
		//taskList.add(new DeadlineTask(null, "Pooping", CommandParser.stringToDate("2014/09/28/23:59"), false));
		//taskList.add(new PeriodicTask(null, "Drinking", CommandParser.stringToDate("2014/09/28/23:59"),
		//		CommandParser.stringToDate("2014/10/28/23:59"),null, null));
		assertEquals("Here shows task list for type ALL\n"
				+ "0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n"
				+ "0. Pooping\nType: Deadline\t\t\t\t\tStatus: Needs Action	Due At: Sep 28, 2014 11:59:00 PM\n\n"
				+ "0. Drinking\nType: Periodic\t\t\t\t\tFrom: Sep 28, 2014 11:59:00 PM To Oct 28, 2014 11:59:00 PM"
				, 
				ResponseParser.parseAllListResponse(taskList));
	}

	
	@Test 
	public void testParseShowDetailResponseNull() throws HandledException {
		Task task = null;
		int taskID = 1;
		assertEquals(ResponseParser.parseShowDetailResponse(task, taskID), 
					"Unable to show detail for task 1");
	}
	
	@Test 
	public void testParseShowDetailResponseTaskWithNoDetails() throws HandledException{
		Task task = new FloatingTask(null, "Eating", false);
		int taskID = 1;
		assertEquals("The details for Task 1:\n"
				+ "0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n"
				+ "Location: null\n"
				+ "Description: null",
				ResponseParser.parseShowDetailResponse(task, taskID));		
	}
	
	@Test 
	public void testParseShowDetailResponseTaskWithDetails() throws HandledException {
		Task task = new FloatingTask(null, "Eating", false);
		task.updateDescription("Eating Chicken Rice");
		int taskID = 1;
		assertEquals("The details for Task 1:\n"
				+ "0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n"
				+ "Location: Hawker Center\n"
				+ "Description: Eating Chicken Rice",
				ResponseParser.parseShowDetailResponse(task, taskID));		
	}
}