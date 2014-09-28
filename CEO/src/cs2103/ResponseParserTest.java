package cs2103;

import static org.junit.Assert.*;

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
		ArrayList<Task> taskList = new ArrayList<Task>();
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
		taskList.add(new Task(null, "Eating"));
		assertEquals(ResponseParser.parseListResponse(taskList, "ALL"), "The task list is empty");
		
	}

}
