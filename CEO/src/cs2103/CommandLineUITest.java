package cs2103;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class CommandLineUITest {
	
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again";
	private static final String MESSAGE_ADD_SUCCESS = "You have successfully added a new task.";
	private static final String MESSAGE_UPDATE_SUCCESS = "You have updated task with ID 1";
	private static final String MESSAGE_DELETE_SUCCESS = "You have deleted task with ID 1";
	private static final String MESSAGE_INVALID_ID = "Your input task ID is not valid, please check your input and try again!";
	private static final String MESSAGE_EMPTY_SEARCH = "The task list is empty";
	
	@Test
	public void testInvalidCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(null, false);
		
		//test invalid command
		String invalidTest = test.testCommand("‐title submit ‐description IVLE ‐location NUS ‐time 2014/09/09 23:59 2014/09/08 00:00 to 2014/09/09 23:59");
		assertEquals(invalidTest, MESSAGE_COMMAND_ERROR);
	}	
	
	@Test
	public void testExitCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(null, false);
		//test exit command
		String exitTest = test.testCommand("exit");
		assertEquals(exitTest, "EXIT");
	}
		
	@Test
	public void testAddCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(null, false);
		//test add 
		String addTest = test.testCommand("add -title write test cases -description write test cases for CS2103 -location eclipse -time 2014/08/08/00:00 to 2014/09/09/23:59 -recurring 2d2");
		assertEquals(addTest, MESSAGE_ADD_SUCCESS);
	}
		
	@Test
	public void testUpdateCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(null, false);
		test.testCommand("add -title write test cases -description write test cases for CS2103 -location eclipse -time 2014/08/08/00:00 to 2014/09/09/23:59 -recurring 2d2");
		
		//test update command
		String updateTest = test.testCommand("update 1 -title write parser -complete false -description write parser for 2103 -location eclipse -time 2014/08/08/00:00 to 2014/09/09/23:59 -recurring 2d2 ");
		assertEquals(updateTest, MESSAGE_UPDATE_SUCCESS);
		String invalidUpdateTest = test.testCommand("show 2");
		assertEquals(invalidUpdateTest, MESSAGE_INVALID_ID);
	}
	
	@Test
	public void testSearchCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(null, false);
		//test search command
		String searchTest = test.testCommand("search floating");
		assertEquals(searchTest, MESSAGE_EMPTY_SEARCH);
	}
	
	@Test
	public void testDeleteCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(null, false);
		test.testCommand("add -title write test cases -description write test cases for CS2103 -location eclipse -time 2014/08/08/00:00 to 2014/09/09/23:59 -recurring 2d2");
		//test delete command
		String deleteTest = test.testCommand("delete 1");
		assertEquals(deleteTest, MESSAGE_DELETE_SUCCESS);
		
	}

}
