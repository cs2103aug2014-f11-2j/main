package cs2103;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;

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
	public void testParseListResponseEmptyTaskList() {
		ArrayList<Task> taskList = new ArrayList<Task>();
		assertEquals(ResponseParser.parseListResponse(taskList, "ALL"), "The task list is empty");
	}
	
	@Test
	public void testParseListResponseOneTaskList() throws CEOException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(new FloatingTask(null, "Eating", false));
		assertEquals("Here shows task list for type ALL\n0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action", 
				ResponseParser.parseListResponse(taskList, "ALL"));
	}
	
	@Test
	public void testParseListResponseTwoTasksList() throws CEOException, ParseException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(new FloatingTask(null, "Eating", false));
		taskList.add(new DeadlineTask(null, "Pooping", CommandParser.stringToDate("2012/06/28/06:32"), false));
		assertEquals("Here shows task list for type ALL\n0. Eating\nType: Floating\t\t\t\t\tStatus: Needs Action\n\n0. Pooping"
				+ "\nType: Deadline\t\t\t\t\tStatus: Needs Action	Due At: Jun 28, 2012 6:32:00 AM", 
				ResponseParser.parseListResponse(taskList, "ALL"));
	}

}
