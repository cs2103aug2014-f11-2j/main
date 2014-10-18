package cs2103.command;

import cs2103.CommonUtil;
import cs2103.HandledException;
import cs2103.parameters.CommandType;

public class Help extends ReadCommand {
	public static final String HELP_DEFAULT = "CEO Usage:\n" +
            								  "  add [-S or --title <title>] ([-D or -description <description>]\n" + 
            								  "      [-L or -location <location>] [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
            								  "      <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]\n" +
            								  "      [-R or -recurring <number h/d/w/m/y>])\n" +
            								  "  +Add a new task. Enter \"help add\" for more\n\n" +
            								  "  list <floating|deadline|periodic|all>\n" +
            								  "  +List existing tasks. Enter \"help list\" for more\n\n" +
            								  "  show <task ID>\n" +
            								  "  +Show detail of the task with specified task ID. Enter \"help show\" for more\n\n" +
            								  "  delete <task ID>\n" +
            								  "  +Delete task with corresponding taskID. Enter \"help delete\" for more\n\n" +
            								  "  update <task ID> ([-S or -title <title>] [-C or -complete {true|false}]\n" +
            								  "                   [-D or -description <description>]\n" +
            								  "                   [-L or -location <location>]\n" +
            								  "                   [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
            								  "                   <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]\n" +
            								  "                   [-R or -recurring <number h/d/w/m/y>])\n"+
            								  "  +Update task with corresponding task ID. Enter \"help update\" for more\n\n" +
            								  "  undo/redo <number of steps>\n"+
            								  "  +Undo/redo some steps. Enter \"help undo\" or \"help redo\" for more\n\n" +
            								  "  search (<floating|deadline|periodic|all>)\n" +
            								  "         {([-K or -keyword <keyword String>]\n" +
            								  "         [-C or -complete {true|false}]\n" +
            								  "         [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
            								  "         <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]}\n" +
            								  "  +Search for tasks. Enter \"help search\" for more\n\n" +
            								  "  help\n" + 
            								  "  +display this message";
	public static final String HELP_ADD = "Add usage:\n"+
										  "  add [-S or --title <title>] ([-D or -description <description>]\n" + 
										  "      [-L or -location <location>] [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
										  "      <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]\n" +
										  "      [-R or -recurring <number h/d/w/m/y>])\n" +
										  "Options:\n" +
										  "  -title <taskTitle>          Elementary, specify title of the task\n" +
										  "  -description <description>  Optional, describe task details\n" +
										  "  -location <location>        Optional, describe task location\n" +
										  "                              Only available for periodic tasks\n" +
										  "  -time <blank>                                default, no time info.\n" +
										  "                                               This task is a floating task.\n" +
										  "        <yyyy/MM/dd HH:mm>                     define a deadline for the task.\n" +
										  "                                               This task is a deadline task\n" +
										  "        <yyyy/MM/dd HH:mm to yyyy/MM/dd HH:mm> define time period for the task.\n" +
										  "                                               This is a periodic task\n" +
										  "  -recurring <Interval><Frequency>    Optional, define a recurrence period\n" +
										  "                       <Frequency> can be h/d/w/m/y, refers to:\n" +
										  "                       Every <Interval> (h)ours/(d)ays/(w)eek/(m)onth/(y)ear\n" +
										  "Example:\n" +
										  "  add -title Task Title -description Describe this task -location office\n" +
										  "      -time 2014/10/12 14:22 to 2014/10/13 14:22 -recurring 1w\n\n" + 
										  "This will effectively adding a Periodic task with title \"Task Title\", with description \"Describe this task\", with location \"office\", with a time period from 2014/10/12 14:22 to 2014/10/13 14:22, and this task will recur every 1 week\n";
	public static final String HELP_DELETE = "Delete has no extra options\n" +
			 								 "Example: delete 1\n";
	public static final String HELP_UPDATE = "Update usage:\n" +
			 								 "  update <task ID> ([-S or -title <title>] [-C or -complete {true|false}]\n" +
			 								 "                   [-D or -description <description>]\n" +
			 								 "                   [-L or -location <location>]\n" +
			 								 "                   [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
			 								 "                   <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]\n" +
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
			 								 "  -time <blank>                                  Convert to a floating task\n" +
			 								 "        <yyyy/MM/dd HH:mm>                       Convert to a deadline task\n" +
			 								 "        <yyyy/MM/dd HH:mm to yyyy/MM/dd HH:mm>   Convert to a periodic task\n" +
			 								 "  -recurring <Interval><Frequency>               Modify the recurrence period\n" +
			 								 "                       <Frequency> can be h/d/w/m/y, refers to:\n" +
			 								 "                       Every <Interval> (h)ours/(d)ays/(w)eek/(m)onth/(y)ear\n" +
			 								 "                       Use -recurring 0 to remove the recurrence" +
			 								 "Example:\n" +
			 								 "  update 1 -title Task Title -description Describe this task\n" +
			 								 "           -time 2014/10/12 14:30 -complete true\n\n" + 
			 								 "This will effectively update the task with Task ID 1 to a Deadline task with title \"Task Title\", with description \"Describe this task\", with a deadline at 2014/10/12 14:30 and indicate the task is complete\n";
	public static final String HELP_LIST = "List usage:\n" +
										   "list <floating|deadline|periodic|all>\n" +
										   "\tfloating\t\tlist tasks with no dates set\n" +
										   "\tperiodic\t\tlist tasks that are recurring\n" +
										   "\tdeadline\t\tlist tasks that have a single deadline\n\n" +
										   "\tall\t\tlist all tasks" +
										   "Example:\nlist deadline\n";
	public static final String HELP_SHOW = "Show has no extra options\n" +
										   "Example:\nshow 1\n";
	public static final String HELP_REDO = "Redo has no extra options\n" +
										   "Example:\nredo 1\n";
	public static final String HELP_UNDO = "Undo has no extra options\n" +
										   "Example:\nundo 1\n";
	public static final String HELP_SEARCH = "Search usage:\n" +
			 								 "  search (<floating|deadline|periodic|all>)\n" +
			 								 "         {([-K or -keyword <keyword String>]\n" +
			 								 "         [-C or -complete {true|false}]\n" +
			 								 "         [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
			 								 "         <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]}\n" +
			 								 "Options:\n" +
			 								 "  <floating|deadline|periodic|all>   Specify the type of tasks to search\n" +
			 								 "                                     Default to all if not specified" +
			 								 "  -keyword <Keyword>          Search title, description and location\n" +
			 								 "                              for the specified keyword\n" +
			 								 "  -complete <true|false>      Specify the complete status you want to search\n" +
			 								 "                              Periodic tasks will be excluded\n" +
			 								 "  -time <blank>                                  Exclude all floating tasks\n" +
			 								 "        <yyyy/MM/dd HH:mm>                       Search deadline and periodic\n" +
			 								 "                                                 tasks that due time or start\n" +
			 								 "                                                 time is before given time\n" +
			 								 "        <yyyy/MM/dd HH:mm to yyyy/MM/dd HH:mm>   Search deadline and periodic\n" +
			 								 "                                                 tasks that due time or start\n" +
			 								 "                                                 time is within given period\n" +
			 								 "Example:\n" +
			 								 "  search all -keyword example keyword -complete true -time\n\n" +
			 								 "This will effectively return the completed Deadline tasks which title, description or location contains \"example keyword\"\n(when complete parameter is specified, it is implied that all Periodic Tasks will be excluded)\n";
	
	public Help(String command){
		this.parameterList.addParameter(CommandType.parse(command));
	}
	
	@Override
	public String execute() throws HandledException{
		CommonUtil.checkNullParameter(this.parameterList.getCommandType(), HandledException.ExceptionType.INVALID_CMD);
		switch (parameterList.getCommandType().getValue()){
		case LIST:
			return HELP_LIST;
		case UPDATE:
			return HELP_UPDATE;
		case ADD:
			return HELP_ADD;
		case DELETE:
			return HELP_DELETE;
		case SHOW:
			return HELP_SHOW;
		case UNDO:
			return HELP_UNDO;
		case REDO:
			return HELP_REDO;
		case SEARCH:
			return HELP_SEARCH;
		case INVALID:
		default:
			return HELP_DEFAULT;
		}
	}

}