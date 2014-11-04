package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.junit.Assert.*;

import org.fusesource.jansi.Ansi;
import org.junit.Test;

import cs2103.exception.HandledException;

public class HelpTest {
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


	@Test
	public void testDefaultHelp() throws HandledException {
		Help defaultHelp = new Help("help");
		assertEquals(HELP_DEFAULT.toString(),defaultHelp.execute().toString());
	}
	
	@Test
	public void testAddHelp() throws HandledException{
		Help addHelp = new Help("add");
		assertEquals(HELP_ADD.toString(),addHelp.execute().toString());
	}
	
	@Test
	public void testDeleteHelp() throws HandledException{
		Help deleteHelp = new Help("delete");
		assertEquals(HELP_DELETE.toString(),deleteHelp.execute().toString());
	}
	
	@Test
	public void testUpdateHelp() throws HandledException{
		Help updateHelp = new Help("update");
		assertEquals(HELP_UPDATE.toString(),updateHelp.execute().toString());
	}

	@Test
	public void testListHelp() throws HandledException{
		Help listHelp = new Help("list");
		assertEquals(HELP_LIST.toString(),listHelp.execute().toString());
	}
	
	@Test
	public void testShowHelp() throws HandledException{
		Help showHelp = new Help("show");
		assertEquals(HELP_SHOW.toString(),showHelp.execute().toString());
	}
	
	@Test
	public void testRedoHelp() throws HandledException{
		Help redoHelp = new Help("redo");
		assertEquals(HELP_REDO.toString(),redoHelp.execute().toString());
	}
	
	@Test
	public void testUndoHelp() throws HandledException{
		Help undoHelp = new Help("undo");
		assertEquals(HELP_UNDO.toString(), undoHelp.execute().toString());
	}
	
	@Test
	public void testSearchHelp() throws HandledException{
		Help searchHelp = new Help("search");
		assertEquals(HELP_SEARCH.toString(), searchHelp.execute().toString());
	}
	
	@Test
	public void testMarkHelp() throws HandledException{
		Help markHelp = new Help("mark");
		assertEquals(HELP_MARK.toString(), markHelp.execute().toString());
	}
}