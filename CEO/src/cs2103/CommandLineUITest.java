package cs2103;

import static org.junit.Assert.*;

import java.lang.reflect.Array;

import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;

public class CommandLineUITest {
	
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again";
	private static final String MESSAGE_ADD_SUCCESS = "You have successfully added a new task." ;
	private static final String MESSAGE_ADD_SUCCESS_1 = "4. Submit homework "; 
	private static final String MESSAGE_ADD_SUCCESS_2 ="Type: Deadline	Status: Needs Action	Due At: 27-Dec-2014 18:00:00";
	private static final String MESSAGE_ADD_SUCCESS_3 = "Description: ";
	private static final String MESSAGE_UPDATE_SUCCESS = "You have updated task with ID 2";
	private static final String MESSAGE_UPDATE_SUCCESS_1 = "3. Submit nothing";
	private static final String MESSAGE_UPDATE_SUCCESS_2 = "Type: Floating	Status: Completed";
	private static final String MESSAGE_UPDATE_SUCCESS_3 = "Description: ";
	private static final String MESSAGE_DELETE_SUCCESS = "You have deleted task with ID 1";
	private static final String MESSAGE_INVALID_ID = "Your input task ID is not valid, please check your input and try again!";
	private static final String MESSAGE_EMPTY_SEARCH = "The task list is empty";
	private static final String MESSAGE_NULL_ERROR = "Your input command contains error, please check your input and try again!";
	private static final String MESSAGE_MARK_SUCCESS = "Successfully marked task 2 as completed";
	private static final String MESSAGE_MARK_SUCCESS_1 = "2. get rich in 6 months";
	private static final String MESSAGE_MARK_SUCCESS_2 = "Type: Floating	Status: Completed";
	private static final String MESSAGE_MARK_SUCCESS_3 = "Description: ";
	private static final String HELP_DEFAULT = "CEO Usage:\n" +
			  								   "  add <Quick add string>\n" +
			  								   "      ([-S or --title <title>] [-D or -description <description>]\n" + 
			  								   "      [-L or -location <location>] [-R or -recurring <number h/d/w/m/y>]\n" +
			  								   "      [-T or -time {<blank>|<time>|<<time> to <time>>}])\n" +
			  								   "  +Add a new task. Enter \"help add\" for more\n\n" +
			  								   "  list (<floating|deadline|periodic|all>)\n" +
			  								   "  +List existing tasks. Enter \"help list\" for more\n\n" +
			  								   "  show <task ID>\n" +
			  								   "  +Show detail of the task with specified task ID. Enter \"help show\" for more\n\n" +
			  								   "  delete <task ID>\n" +
			  								   "  +Delete task with corresponding taskID. Enter \"help delete\" for more\n\n" +
			  								   "  update <task ID> ([-S or -title <title>] [-C or -complete {true|false}]\n" +
			  								   "                   [-D or -description <description>]\n" +
			  								   "                   [-L or -location <location>]\n" +
			  								   "                   [-T or -time {<blank>|<time>|<<time> to <time>>}]\n" +
			  								   "                   [-R or -recurring <number h/d/w/m/y>])\n"+
			  								   "  +Update task with corresponding task ID. Enter \"help update\" for more\n\n" +
			  								   "  undo/redo <number of steps>\n"+
			  								   "  +Undo/redo some steps. Enter \"help undo\" or \"help redo\" for more\n\n" +
			  								   "  search (<floating|deadline|periodic|all>)\n" +
			  								   "         {([-K or -keyword <keyword String>]\n" +
			  								   "         [-C or -complete {true|false}]\n" +
			  								   "         [-T or -time {<blank>|<time>|<<time> to <time>>}]}\n" +
			  								   "  +Search for tasks. Enter \"help search\" for more\n\n" +
			  								   "  mark <task ID>\n" +
			  								   "  Mark a task as completed. Enter \"help mark\" for more\n\n" +
			  								   "  alert\n" +
			  								   "  +display approaching deadlines and start times\n\n" +
			  								   "  help\n" + 
			  								   "  +display this message";
	private static final String HELP_ADD = "Add usage:\n"+
			  							   "  add <Quick add string>\n" +
			  							   "      ([-S or --title <title>] [-D or -description <description>]\n" + 
			  							   "      [-L or -location <location>] [-R or -recurring <number h/d/w/m/y>]\n" +
			  							   "      [-T or -time {<blank>|<time>|<<time> to <time>>}])\n" +
			  							   "Options:\n" +
			  							   "  <Quick add string>          Text before \"from\"/\"by\"/\"on\"/\"in\"/\"at\" is title.\n" +
			  							   "                              You can specify time after these keywords.\n" +
			  							   "                              Specify recurrence after keyword \"every\"\n" +
			  							   "  -title <taskTitle>          Optional if using quick add, title of the task\n" +
			  							   "  -description <description>  Optional, describe task details\n" +
			  							   "  -location <location>        Optional, describe task location\n" +
			  							   "                              Only available for periodic tasks\n" +
			  							   "  -time <blank>               default, no time info.\n" +
			  							   "                              This task is a floating task.\n" +
			  							   "        <time>                define a deadline for the task.\n" +
			  							   "                              This task is a deadline task\n" +
			  							   "        <<time> to <time>>    define time period for the task.\n" +
			  							   "                              This is a periodic task\n" +
			  							   "  -recurring <Interval><Frequency>    Optional, define a recurrence period\n" +
			  							   "                       <Frequency> can be h/d/w/m/y, refers to:\n" +
			  							   "                       Every <Interval> (h)ours/(d)ays/(w)eek/(m)onth/(y)ear\n" +
			  							   "Example:\n" +
			  							   "  add Submit homework by 6pm tomorrow\n" +
			  							   "This will effectively adding a Deadline Task with title \"Submit homework\" with due time at 6pm tomorrow\n\n" +
			  							   "  add -title Task Title -description Describe this task -location office\n" +
			  							   "      -time 2014/10/12 14:22 to 2014/10/13 14:22 -recurring 1w\n\n" + 
			  							   "This will effectively adding a Periodic task with title \"Task Title\", with description \"Describe this task\", with location \"office\", with a time period from 2014/10/12 14:22 to 2014/10/13 14:22, and this task will recur every 1 week\n";
	private static final String HELP_DELETE = "Delete has no extra options\n" +
			 								 "Example: delete 1\n";
	private static final String HELP_UPDATE = "Update usage:\n" +
			 								  "  update <task ID> ([-S or -title <title>] [-C or -complete {true|false}]\n" +
			 								  "                   [-D or -description <description>]\n" +
			 								  "                   [-L or -location <location>]\n" +
			 								  "                   [-T or -time {<blank>|<time>|<<time> to <time>>}]\n" +
			 								  "                   [-R or -recurring <number h/d/w/m/y>])\n"+
			 								  "Options:\n" +
			 								  "  <Task ID>                   Elementary, specify the task ID of which\n" +
			 								  "                              the task require modification\n" +
			 								  "  -title <taskTitle>          Modify title of the task\n" +
			 								  "  -complete <true|false>      Indicate the task is complete or not\n" +
			 								  "                              Unavailable for periodic tasks\n" +
			 								  "  -description <description>  Modify task details\n" +
			 								  "  -location <location>        Modify task location\n" +
			 								  "                              only available for periodic tasks\n" +
			 								  "  -time <blank>               Convert this task to a floating task\n" +
			 								  "        <time>                Convert this task to a deadline task\n" +
			 								  "        <<time> to <time>>    Convert this task to a periodic task\n" +
			 								  "  -recurring <Interval><Frequency>               Modify the recurrence period\n" +
			 								  "                       <Frequency> can be h/d/w/m/y, refers to:\n" +
			 								  "                       Every <Interval> (h)ours/(d)ays/(w)eek/(m)onth/(y)ear\n" +
			 								  "                       Use -recurring 0 to remove the recurrence" +
			 								  "Example:\n" +
			 								  "  update 1 -title Task Title -description Describe this task\n" +
			 								  "           -time 2014/10/12 14:30 -complete true\n\n" + 
			 								  "This will effectively update the task with Task ID 1 to a Deadline task with title \"Task Title\", with description \"Describe this task\", with a deadline at 2014/10/12 14:30 and indicate the task is complete\n";
	private static final String HELP_LIST = "List usage:\n" +
			   							   "list (<floating|deadline|periodic|all>)\n" +
			   							   "  list only incomplete tasks if no type is specified\n" +
			   							   "  floating\t\tlist tasks with no dates set\n" +
			   							   "  periodic\t\tlist tasks that are recurring\n" +
			   							   "  deadline\t\tlist tasks that have a single deadline\n\n" +
			   							   "  all\t\tlist all tasks" +
			   							   "Example:\nlist deadline\n";
	private static final String HELP_SHOW = "Show has no extra options\n" +
			   							   "Example:\nshow 1\n";
	private static final String HELP_REDO = "Redo has no extra options\n" +
			   							   "Example:\nredo 1\n";
	private static final String HELP_UNDO = "Undo has no extra options\n" +
			   							   "Example:\nundo 1\n";
	private static final String HELP_SEARCH = "Search usage:\n" +
			 								 "  search (<floating|deadline|periodic|all>)\n" +
			 								 "         {([-K or -keyword <keyword String>]\n" +
			 								 "         [-C or -complete {true|false}]\n" +
			 								 "         [-T or -time {<blank>|<time>|<<time> to <time>>}]}\n" +
			 								 "Options:\n" +
			 								 "  <floating|deadline|periodic|all>   Specify the type of tasks to search\n" +
			 								 "                                     Default to all if not specified\n" +
			 								 "  -keyword <Keyword>          Search title, description and location\n" +
			 								 "                              for the specified keyword\n" +
			 								 "  -complete <true|false>      Specify the complete status you want to search\n" +
			 								 "  -time <blank>               Search only deadline and periodic tasks\n" +
			 								 "        <time>                Search deadline and periodic tasks that\n" +
			 								 "                              due time or start time is before given time\n" +
			 								 "        <<time> to <time>>    Search deadline and periodic tasks that \n" +
			 								 "                              due time or start time is within given period\n" +
			 								 "Example:\n" +
			 								 "  search all -keyword example keyword -complete true -time\n\n" +
			 								 "This will effectively return the completed Deadline tasks which title, description or location contains \"example keyword\"\n(when complete parameter is specified, it is implied that all Periodic Tasks will be excluded)\n";
	private static final String HELP_MARK = "Mark has no extra options\n" +
			   							   "Example: mark 2\n";
	private static final String MESSAGE_REDO_SUCCESS = "Successfully redo 0 tasks";
	private static final String MESSAGE_UNDO_SUCCESS = "Successfully undo 1 tasks";
	private static final String MESSAGE_LIST_SUCCESS = "The task list is empty";
	private static final String MESSAGE_SHOW_SUCCESS = "The details for Task 2:";
	private static final String MESSAGE_SHOW_SUCCESS_1 = "2. get rich in 6 months";
	private static final String MESSAGE_SHOW_SUCCESS_2 = "Type: Floating	Status: Completed";
	private static final String MESSAGE_SHOW_SUCCESS_3 = "Description: ";
	
	@Test
	public void testInvalidCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		//test invalid command
		String invalidTest = test.testCommand("‐title submit ‐description IVLE ‐location NUS ‐time 2014/09/09 23:59 2014/09/08 00:00 to 2014/09/09 23:59");
		assertEquals(invalidTest, MESSAGE_COMMAND_ERROR);
	}	
	
	@Test
	public void testExitCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		//test exit command
		String exitTest = test.testCommand("exit");
		assertEquals(exitTest, "EXIT");
	}
		
	@Test
	public void testAddCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		//test add 
		String addTest = test.testCommand("add Submit homework by 6pm in two months time");
		String[] separatedTest = addTest.split("\n");
		
		assertEquals(Array.get(separatedTest, 0), MESSAGE_ADD_SUCCESS);
		assertEquals(Array.get(separatedTest, 1), MESSAGE_ADD_SUCCESS_1);
		assertEquals(Array.get(separatedTest, 2), MESSAGE_ADD_SUCCESS_2);
		assertEquals(Array.get(separatedTest, 3), MESSAGE_ADD_SUCCESS_3);
	}
		
	@Test
	public void testUpdateCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		
		test.testCommand("add Submit test cases by 6 pm in 3 months time");
		String updateTest = test.testCommand("Update 2 -title Submit nothing");
		String[] separatedTest = updateTest.split("\n");
		assertEquals(Array.get(separatedTest, 0), MESSAGE_UPDATE_SUCCESS);
		assertEquals(Array.get(separatedTest, 1), MESSAGE_UPDATE_SUCCESS_1);
		assertEquals(Array.get(separatedTest, 2), MESSAGE_UPDATE_SUCCESS_2);
		assertEquals(Array.get(separatedTest, 3), MESSAGE_UPDATE_SUCCESS_3);
		
		String invalidUpdateTest = test.testCommand("show 4");
		assertEquals(invalidUpdateTest, MESSAGE_INVALID_ID);
	}
	
	@Test
	public void testSearchCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		//test search command
		String searchTest = test.testCommand("search periodic");
		assertEquals(searchTest, MESSAGE_EMPTY_SEARCH);
	}
	
	@Test
	public void testDeleteCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		test.testCommand("add -title write test cases -description write test cases for CS2103 -location eclipse -time 2014/08/08/00:00 to 2014/09/09/23:59 -recurring 2d2");
		//test delete command
		String deleteTest = test.testCommand("delete 1");
		assertEquals(deleteTest, MESSAGE_DELETE_SUCCESS);
	}
	
	@Test
	public void testEmptyCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String emptyTest = test.testCommand("");
		assertEquals(emptyTest, MESSAGE_COMMAND_ERROR);
	}
	
	@Test
	public void testNullCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String nullTest = test.testCommand(null);
		assertEquals(MESSAGE_NULL_ERROR, nullTest);
	}
	
	@Test
	public void testMarkCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		test.testCommand("add get rich in 6 months");
		String markTest = test.testCommand("mark 2");
		String[] separatedTest = markTest.split("\n");
		assertEquals(Array.get(separatedTest, 0), MESSAGE_MARK_SUCCESS);
		assertEquals(Array.get(separatedTest, 1), MESSAGE_MARK_SUCCESS_1);
		assertEquals(Array.get(separatedTest, 2), MESSAGE_MARK_SUCCESS_2);
		assertEquals(Array.get(separatedTest, 3), MESSAGE_MARK_SUCCESS_3);
	}
	
	@Test 
	public void testRedoCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String redoTest = test.testCommand("redo 1");
		assertEquals(redoTest, MESSAGE_REDO_SUCCESS);
	}
	
	@Test
	public void testUndoCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String undoTest = test.testCommand("undo 1");
		assertEquals(undoTest, MESSAGE_UNDO_SUCCESS);
	}
	
	@Test
	public void testListCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String listTest = test.testCommand("list");
		assertEquals(listTest, MESSAGE_LIST_SUCCESS);
	}
	
	@Test
	public void testShowCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String showTest1 = test.testCommand("show 3");
		assertEquals(showTest1, MESSAGE_INVALID_ID);
		
		String showTest2 = test.testCommand("show 2");
		String[] separatedTest = showTest2.split("\n");
		assertEquals(Array.get(separatedTest, 0), MESSAGE_SHOW_SUCCESS);
		assertEquals(Array.get(separatedTest, 1), MESSAGE_SHOW_SUCCESS_1);
		assertEquals(Array.get(separatedTest, 2), MESSAGE_SHOW_SUCCESS_2);
		assertEquals(Array.get(separatedTest, 3), MESSAGE_SHOW_SUCCESS_3);
	}
	
	@Test
	public void testHelpCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String helpTest = test.testCommand("help");
		assertEquals(helpTest, HELP_DEFAULT);
		
		String helpAddTest = test.testCommand("help add");
		assertEquals(helpAddTest, HELP_ADD);
		
		String helpDeleteTest = test.testCommand("help delete");
		assertEquals(helpDeleteTest, HELP_DELETE);
		
		String helpUpdateTest = test.testCommand("help update");
		assertEquals(helpUpdateTest, HELP_UPDATE);
		
		String helpListTest = test.testCommand("help list");
		assertEquals(helpListTest, HELP_LIST);
		
		String helpShowTest = test.testCommand("help show");
		assertEquals(helpShowTest, HELP_SHOW);
		
		String helpRedoTest = test.testCommand("help redo");
		assertEquals(helpRedoTest, HELP_REDO);
		
		String helpUndoTest = test.testCommand("help undo");
		assertEquals(helpUndoTest, HELP_UNDO);
		
		String helpSearchTest = test.testCommand("help search");
		assertEquals(helpSearchTest, HELP_SEARCH);
		
		String helpMarkTest = test.testCommand("help mark");
		assertEquals(helpMarkTest, HELP_MARK);
	}
}
