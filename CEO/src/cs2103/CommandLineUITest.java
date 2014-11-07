//@author A0110906R
package cs2103;

import static org.junit.Assert.*;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

import java.lang.reflect.Array;

import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommandLineUITest {
	
	private static final String MESSAGE_DELETE = "You have moved task with ID %1$d to trash\n";
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again\n";
	private static final String MESSAGE_ADD = "You have successfully added a new task.\n";							
	private static final String MESSAGE_ADD_SUCCESS_5 = "Description: ";
	private static final String MESSAGE_UPDATE = "You have updated task with ID %1$d\n";
	private static final String MESSAGE_UPDATE_UNSUCCESSFUL = "You need to specify at least one parameter";
	protected static final Ansi DELETED = ansi().fg(MAGENTA).a("(Deleted Task)\n").reset();
	private static final String MESSAGE_INVALID_ID = "Your input task ID is not valid, please check your input and try again!\n";
	private static final String MESSAGE_NULL_ERROR = "Your input command contains error, please check your input and try again!";
	private static final String MESSAGE_MARK = "Successfully marked task %1$d as completed\n";
	private static final String MESSAGE_MARK_SUCCESS = "Successfully marked task 4 as completed";
	public static final Ansi HELP_DEFAULT = ansi().fg(YELLOW).a("CEO Usage:\n").reset()
			  								.bold().a("  add <Quick add string>\n" +
			  								"      ([-S or --title <title>] [-D or -description <description>]\n" + 
			  								"      [-L or -location <location>] [-R or -recurring <number h/d/w/m/y>]\n" +
			  										"      [-T or -time {<blank>|<time>|<<time> to <time>>}])\n").reset()
			  										.bg(BLUE).a("  +Add a new task. Enter \"help add\" for more\n\n").reset()
			  										.bold().a("  list (<floating|deadline|periodic|all|trash>)\n").reset()
			  										.bg(GREEN).a("  +List existing tasks. Enter \"help list\" for more\n\n").reset()
			  										.bold().a("  show <task ID>\n").reset()
			  										.bg(GREEN).a("  +Show detail of the task with specified task ID. Enter \"help show\" for more\n\n").reset()
			  										.bold().a("  delete <task ID> (-p)\n").reset()
			  										.bg(RED).a("  +Delete task with corresponding taskID. Enter \"help delete\" for more\n\n").reset()
			  										.bold().a("  update <task ID> ([-S or -title <title>] [-C or -complete {true|false}]\n" +
			  												"                   [-D or -description <description>]\n" +
			  												"                   [-L or -location <location>]\n" +
			  												"                   [-T or -time {<blank>|<time>|<<time> to <time>>}]\n" +
			  												"                   [-R or -recurring <number h/d/w/m/y>])\n").reset()
			  										.bg(BLUE).a("  +Update task with corresponding task ID. Enter \"help update\" for more\n\n").reset()
			  										.bold().a("  undo/redo <number of steps>\n").reset()
			  										.bg(BLUE).a("  +Undo/redo some steps. Enter \"help undo\" or \"help redo\" for more\n\n").reset()
			  										.bold().a("  search (<keyword String>)\n" +
			  												"         {([-K or -type <floating|deadline|periodic|all|trash>]\n" +
			  												"         [-C or -complete {true|false}]\n" +
			  												"         [-T or -time {<blank>|<time>|<<time> to <time>>}]}\n").reset()
			  										.bg(GREEN).a("  +Search for tasks. Enter \"help search\" for more\n\n").reset()
			  										.bold().a("  mark <task ID>\n").reset()
			  										.bg(BLUE).a("  Mark a task as completed. Enter \"help mark\" for more\n\n").reset()
			  										.bold().a("  sync (-disable)\n").reset()
			  										.bg(BLUE).a("  +Manually sync your task with Google. In usual circumstances, CEO will\n" +
			  										"   automatically sync with Google, this command is useful when you disabled\n" +
			  										"   sync in initialization\n" +
			  										"   Use sync -disable to disable syncing\n\n").reset()
			  										.bold().a("  alert\n").reset()
			  										.bg(BLUE).a("  +display approaching deadlines and start times\n\n").reset()
			  										.bold().a("  help\n").reset()
			  										.bg(BLUE).a("  +display this message\n").reset();
	public static final Ansi HELP_ADD = ansi().a("Add usage:\n")
										.bold().a("  add <Quick add string>\n" +
												"      ([-S or --title <title>] [-D or -description <description>]\n" + 
												"      [-L or -location <location>] [-R or -recurring <number h/d/w/m/y>]\n" +
												"      [-T or -time {<blank>|<time>|<<time> to <time>>}])\n").reset()
												.a("Options:\n")
												.bg(CYAN).a("  <Quick add string>          Text before \"from\"/\"by\"/\"on\"/\"in\"/\"at\" is title.\n" +
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
														"  -recurring <Interval><Frequency> Optional, define a recurrence period\n" +
														"                       <Frequency> can be h/d/w/m/y, refers to:\n" +
														"                       Every <Interval> (h)ours/(d)ays/(w)eek/(m)onth/(y)ear\n\n").reset()
														.a("Example:\n")
														.bg(YELLOW).a("  add Submit homework by tomorrow\n").reset()
														.bg(BLUE).a("This will effectively adding a Deadline Task with title \"Submit homework\" with due time by tomorrow\n\n").reset()
														.bg(YELLOW).a("  add -title Task Title -description Describe this task -location office\n" +
																"      -time 2014/10/12 14:22 to 2014/10/13 14:22 -recurring 1w\n").reset()
																.bg(BLUE).a("This will effectively adding a Periodic task with title \"Task Title\", with description \"Describe this task\", with location \"office\", with a time period from 2014/10/12 14:22 to 2014/10/13 14:22, and this task will recur every 1 week\n").reset();
	public static final Ansi HELP_DELETE = ansi().a("Delete usage:\n")
										   .bold().a("  delete <task ID> (-p)\n").reset()
										   .a("Options:\n")
										   .bg(CYAN).a("  <task ID>        Elementary, specify the task you want to move to trash\n" +
												   "  -p               Optional, the task will be removed permanantly\n").reset()
												   .bg(YELLOW).a("Example: delete 1\n").reset();
											public static final Ansi HELP_UPDATE = ansi().a("Update usage:\n")
											.bold().a("  update <task ID> ([-S or -title <title>] [-C or -complete {true|false}]\n" +
											"                   [-D or -description <description>]\n" +
											"                   [-L or -location <location>]\n" +
											"                   [-T or -time {<blank>|<time>|<<time> to <time>>}]\n" +
													"                   [-R or -recurring <number h/d/w/m/y>])\n").reset()
													.a("Options:\n")
													.bg(CYAN).a("  <Task ID>                   Elementary, specify the task ID of which\n" +
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
															"  -recurring <Interval><Frequency> Modify the recurrence period\n" +
															"                       <Frequency> can be h/d/w/m/y, refers to:\n" +
															"                       Every <Interval> (h)ours/(d)ays/(w)eek/(m)onth/(y)ear\n" +
															"                       Use -recurring 0 to remove the recurrence\n\n").reset()
															.a("Example:\n")
															.bg(YELLOW).a("  update 1 -title Task Title -description Describe this task\n" +
																	"           -time 2014/10/12 -complete true\n").reset()
																	.bg(BLUE).a("This will effectively update the task with Task ID 1 to a Deadline task with title \"Task Title\", with description \"Describe this task\", with a deadline at 2014/10/12 and indicate the task is complete\n").reset();
	public static final Ansi HELP_LIST = ansi().a("List usage:\n")
										.bold().a("list (<<blank>|floating|deadline|periodic|all>)\n" +
												"  <blank>              list only incomplete tasks\n" +
												"  floating             list tasks with no dates set\n" +
												"  deadline             list tasks that have a deadline\n" +
												"  periodic             list tasks that is a timed event\n" +
												"  all                  list all tasks\n" +
												"  trash                list all tasks in trash bin\n").reset()
												.bg(YELLOW).a("Example:\nlist deadline\n").reset();
	public static final Ansi HELP_SHOW = ansi().bold().a("Show has no extra options\n" +
										"Example:\nshow 1\n").reset();
	public static final Ansi HELP_REDO = ansi().bold().a("Redo has no extra options\n" +
										"Example:\nredo 1\n").reset();
	public static final Ansi HELP_UNDO = ansi().bold().a("Undo has no extra options\n" +
										"Example:\nundo 1\n").reset();
	public static final Ansi HELP_SEARCH = ansi().a("Search usage:\n")
											.bold().a("  search (<keyword String>)\n" +
													"         {([-K or -type <floating|deadline|periodic|all|trash>]\n" +
													"         [-C or -complete {true|false}]\n" +
													"         [-T or -time {<blank>|<time>|<<time> to <time>>}]}\n").reset()
													.a("Options:\n")
													.bg(CYAN).a("  <Keyword>                   Search title, description and location\n" +
															"                              for the specified keyword\n" +
															"  <floating|deadline|         Specify the type of tasks to search\n" +
															"  periodic|all|trash>         if not specified, search all tasks\n" +
															"  -complete <true|false>      Specify the complete status you want to search\n" +
															"  -time <blank>               Search only deadline and periodic tasks\n" +
															"        <time>                Search deadline and periodic tasks that\n" +
															"                              due time or start time is before given time\n" +
															"        <<time> to <time>>    Search deadline and periodic tasks that \n" +
															"                              due time or start time is within given period\n\n").reset()
															.a("Example:\n")
															.bg(YELLOW).a("  search example keyword -keyword all -complete true -time\n").reset()
															.bg(BLUE).a("This will effectively return the completed Deadline tasks which title, description or location contains \"example keyword\". (when complete parameter is specified, it is implied that all Periodic Tasks will be excluded)\n").reset();
	public static final Ansi HELP_MARK = ansi().bold().a("Mark has no extra options\n" +
										"Example: mark 2\n").reset();

	private static final String MESSAGE_REDO_SUCCESS = "Successfully redo 0 operations\n";
	private static final String MESSAGE_UNDO_SUCCESS = "Successfully undo 1 operations\n";
	private static final String MESSAGE_SHOW_SUCCESS = "The details for Task 1:\n";
	private static final String MESSAGE_SHOW_SUCCESS_1 = ansi().a(MESSAGE_SHOW_SUCCESS).fg(YELLOW).a("1. ").reset().bold().a("Play basketball \n").boldOff().reset().a("Status: ").bold().fg(RED).a("Needs Action").reset().a('\t').a("Due At: ").bold().fg(GREEN).a("2014/12/15 07:00 PM").reset().a("\nDescription: \n").reset().toString();
	
	@Test
	public void test6_InvalidCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String invalidTest = test.testCommand("‐title submit ‐description IVLE ‐location NUS ‐time 2014/09/09 23:59 2014/09/08 00:00 to 2014/09/09 23:59");
		assertEquals(invalidTest, ansi().bg(RED).a(MESSAGE_COMMAND_ERROR).reset().toString());
	}	
	
	@Test(expected= NullPointerException.class)
	public void test4_ExitCommand() throws NullPointerException, HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		assertNull(test.testCommand("exit"));
	}
		
	@Test
	public void test1_AddCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String addTest = test.testCommand("add Submit homework on 21 December 2014 at 6pm");
		String[] separatedTest = addTest.split("\n");
		Ansi returnString = ansi().fg(GREEN).a(MESSAGE_ADD);
		Ansi returnString1 = ansi().fg(YELLOW).a("2").a(". ").reset();
		returnString1.bold().a("Submit homework ").a('\n').boldOff().reset();
		returnString.a(returnString1);
		String[] separated = returnString.toString().split("\n");
		assertEquals(Array.get(separatedTest, 0), Array.get(separated, 0));
		assertEquals(Array.get(separatedTest, 1), Array.get(separated, 1));
		assertEquals(Array.get(separatedTest, 2), ansi().boldOff().reset().a("Status: ").bold().fg(RED).a("Needs Action").reset().a("\t").a("Due At: ").bold().fg(GREEN).a("2014/12/21 06:00 PM").reset().toString());
		assertEquals(Array.get(separatedTest, 3), MESSAGE_ADD_SUCCESS_5);
		assertEquals(Array.get(separatedTest, 4), ansi().reset().toString());
	}
		
	@Test
	public void test14_UpdateCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		test.testCommand("add Submit test cases on 27 December 2014 at 7pm");
		String updateTest = test.testCommand("Update 1 -title Submit nothing");
	
;		Ansi returnString = ansi();
		returnString.fg(GREEN).a(String.format(MESSAGE_UPDATE, 1)).reset();
		Ansi returnString1 = ansi().fg(YELLOW).a("1").a(". ").reset();
		returnString1.bold().a("Submit nothing").a('\n').boldOff().reset();
		
		returnString.a(returnString1);
		returnString.a(ansi().a("Status: ").bold().fg(RED).a("Needs Action").reset().a("\t").a("Due At: ").bold().fg(GREEN).a("2014/12/27 07:00 PM").reset().toString());
		returnString.a("\nDescription: \n").reset();
		assertEquals(updateTest, returnString.toString());
		
		String invalidUpdateTest = test.testCommand("Update 23 invalid");
		assertEquals(invalidUpdateTest, ansi().bg(RED).a(MESSAGE_UPDATE_UNSUCCESSFUL).a('\n').reset().toString());
	}
	
	@Test
	public void test11_SearchCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String searchTest = test.testCommand("search periodic");
		assertEquals(ansi().bold().fg(RED).a("The task list is empty\n").reset().toString(), searchTest);
	}
	
	@Test
	public void test2_DeleteCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		test.testCommand("add -title write test cases -description write test cases for CS2103 -location eclipse -time 2014/08/08/00:00 to 2014/09/09/23:59 -recurring 2d2");
		String deleteTest = test.testCommand("delete 1");
		assertEquals(deleteTest, ansi().fg(GREEN).a(String.format(MESSAGE_DELETE, 1)).reset().toString());
		
		String invalidDelete = test.testCommand("delete 23");
		assertEquals(invalidDelete, ansi().bg(RED).a(MESSAGE_INVALID_ID).reset().toString());
	}
	
	@Test
	public void test3_EmptyCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String emptyTest = test.testCommand("");
		assertEquals(emptyTest, ansi().bg(RED).a(MESSAGE_COMMAND_ERROR).reset().toString());
	}
	
	@Test
	public void test9_NullCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String nullTest = test.testCommand(null);
		assertEquals(ansi().bg(RED).a(MESSAGE_NULL_ERROR).a('\n').reset().toString(), nullTest);
	}
	
	@Test
	public void test8_MarkCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		test.testCommand("add get rich on 5 June 2015 at 6pm");
		String markTest = test.testCommand("mark 4");


		String[] separatedTest = markTest.split("\n");
		
		Ansi returnString1 = ansi().fg(GREEN).a(String.format(MESSAGE_MARK, 4)).reset();
		Ansi returnString= ansi().fg(YELLOW).a("4").a(". ").reset();
		returnString1.a(returnString.bold().a("get rich "));
		String[] result1 = returnString1.toString().split("\n");
		assertEquals(Array.get(separatedTest, 0), ansi().fg(GREEN).a(MESSAGE_MARK_SUCCESS).toString());
		assertEquals(Array.get(separatedTest, 1), Array.get(result1, 1));
		assertEquals(Array.get(separatedTest, 2), ansi().boldOff().reset().a("Status: ").bold().fg(GREEN).a("Completed").reset().a("\t").a("Due At: ").bold().fg(GREEN).a("2015/06/05 06:00 PM").reset().toString());
		assertEquals(Array.get(separatedTest, 3), ansi().a("Description: ").toString());
		
		String invalidMark = test.testCommand("mark 23");
		assertEquals(invalidMark, ansi().bg(RED).a(MESSAGE_INVALID_ID).reset().toString());
	}
	
	@Test 
	public void test10_RedoCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String redoTest = test.testCommand("redo 1");
		assertEquals(redoTest, ansi().fg(GREEN).a(MESSAGE_REDO_SUCCESS).reset().toString());
		
	}
	
	@Test
	public void test13_UndoCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String undoTest = test.testCommand("undo 1");
		assertEquals(undoTest, ansi().fg(GREEN).a(MESSAGE_UNDO_SUCCESS).reset().toString());
	}
	
	@Test
	public void test7_ListCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String listTest = test.testCommand("list");
		String[] separatedTest = listTest.split("\n");
		Ansi returnString1 = ansi().fg(YELLOW).a("1").a(". ").reset();
		returnString1.bold().a("Submit homework ").a('\n').boldOff().reset();
		returnString1.a(ansi().a("Status: ").bold().fg(RED).a("Needs Action").reset().a("\t").a("Due At: ").bold().fg(GREEN).a("2014/12/21 06:00 PM").reset().toString());
		String[] separated = returnString1.toString().split("\n");
		assertEquals(Array.get(separatedTest, 0), Array.get(separated, 0));
		assertEquals(Array.get(separatedTest, 1), Array.get(separated, 1));
	}
	
	@Test
	public void test12_ShowCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String showTest1 = test.testCommand("show 10");
		assertEquals(showTest1, ansi().bg(RED).a(MESSAGE_INVALID_ID).reset().toString());
		
		test.testCommand("add Play basketball on 15 December 2014 at 7pm");
		String showTest2 = test.testCommand("show 1");
		assertEquals(showTest2, MESSAGE_SHOW_SUCCESS_1);
	}
	
	@Test
	public void test5_HelpCommand() throws HandledException, FatalException {
		CommandLineUI test = CommandLineUI.getInstance(new Option(Option.Value.TEST));
		String helpTest = test.testCommand("help");
		assertEquals(helpTest, HELP_DEFAULT.toString());
		
		String helpAddTest = test.testCommand("help add");
		assertEquals(helpAddTest, HELP_ADD.toString());
		
		String helpDeleteTest = test.testCommand("help delete");
		assertEquals(helpDeleteTest, HELP_DELETE.toString());
		
		String helpUpdateTest = test.testCommand("help update");
		assertEquals(helpUpdateTest, HELP_UPDATE.toString());
		
		String helpListTest = test.testCommand("help list");
		assertEquals(helpListTest, HELP_LIST.toString());
		
		String helpShowTest = test.testCommand("help show");
		assertEquals(helpShowTest, HELP_SHOW.toString());
		
		String helpRedoTest = test.testCommand("help redo");
		assertEquals(helpRedoTest, HELP_REDO.toString());
		
		String helpUndoTest = test.testCommand("help undo");
		assertEquals(helpUndoTest, HELP_UNDO.toString());
		
		String helpSearchTest = test.testCommand("help search");
		assertEquals(helpSearchTest, HELP_SEARCH.toString());
		
		String helpMarkTest = test.testCommand("help mark");
		assertEquals(helpMarkTest, HELP_MARK.toString());
	}
}

