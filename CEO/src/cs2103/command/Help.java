package cs2103.command;

import cs2103.exception.HandledException;
import cs2103.parameters.CommandType;
import cs2103.util.CommonUtil;

public class Help extends QueryCommand {
	public static final String HELP_DEFAULT = "CEO Usage:\n" +
            								  "  add <Quick add string>\n" +
            								  "      ([-S or --title <title>] [-D or -description <description>]\n" + 
            								  "      [-L or -location <location>] [-R or -recurring <number h/d/w/m/y>]\n" +
            								  "      [-T or -time {<blank>|<time>|<<time> to <time>>}])\n" +
            								  "  +Add a new task. Enter \"help add\" for more\n\n" +
            								  "  list (<floating|deadline|periodic|all|trash>)\n" +
            								  "  +List existing tasks. Enter \"help list\" for more\n\n" +
            								  "  show <task ID>\n" +
            								  "  +Show detail of the task with specified task ID. Enter \"help show\" for more\n\n" +
            								  "  delete <task ID> (-p)\n" +
            								  "  +Delete task with corresponding taskID. Enter \"help delete\" for more\n\n" +
            								  "  update <task ID> ([-S or -title <title>] [-C or -complete {true|false}]\n" +
            								  "                   [-D or -description <description>]\n" +
            								  "                   [-L or -location <location>]\n" +
            								  "                   [-T or -time {<blank>|<time>|<<time> to <time>>}]\n" +
            								  "                   [-R or -recurring <number h/d/w/m/y>])\n"+
            								  "  +Update task with corresponding task ID. Enter \"help update\" for more\n\n" +
            								  "  undo/redo <number of steps>\n"+
            								  "  +Undo/redo some steps. Enter \"help undo\" or \"help redo\" for more\n\n" +
            								  "  search (<keyword String>)\n" +
            								  "         {([-K or -type <floating|deadline|periodic|all|trash>]\n" +
            								  "         [-C or -complete {true|false}]\n" +
            								  "         [-T or -time {<blank>|<time>|<<time> to <time>>}]}\n" +
            								  "  +Search for tasks. Enter \"help search\" for more\n\n" +
            								  "  mark <task ID>\n" +
            								  "  Mark a task as completed. Enter \"help mark\" for more\n\n" +
            								  "  sync (-disable)\n" +
            								  "  +Manually sync your task with Google. In usual circumstances, CEO will\n" +
            								  "   automatically sync with Google, this command is useful when you disabled\n" +
            								  "   sync in initialization\n" +
            								  "   Use sync -disable to disable syncing\n\n" +
            								  "  alert\n" +
            								  "  +display approaching deadlines and start times\n\n" +
            								  "  help\n" + 
            								  "  +display this message";
	public static final String HELP_ADD = "Add usage:\n"+
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
	public static final String HELP_DELETE = "Delete usage:\n" +
										  	 "  delete <task ID> (-p)\n" +
										  	 "Options:\n" +
										  	 "  <task ID>        Elementary, specify the task you want to move to trash\n" +
										  	 "  -p               Optional, the task will be removed permanantly\n" +
			 								 "Example: delete 1\n";
	public static final String HELP_UPDATE = "Update usage:\n" +
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
	public static final String HELP_LIST = "List usage:\n" +
										   "list (<<blank>|floating|deadline|periodic|all>)\n" +
										   "  <blank>              list only incomplete tasks\n" +
										   "  floating             list tasks with no dates set\n" +
										   "  deadline             list tasks that have a deadline\n" +
										   "  periodic             list tasks that is a timed event\n" +
										   "  all                  list all tasks\n" +
										   "  trash                list all tasks in trash bin\n" +
										   "Example:\nlist deadline\n";
	public static final String HELP_SHOW = "Show has no extra options\n" +
										   "Example:\nshow 1\n";
	public static final String HELP_REDO = "Redo has no extra options\n" +
										   "Example:\nredo 1\n";
	public static final String HELP_UNDO = "Undo has no extra options\n" +
										   "Example:\nundo 1\n";
	public static final String HELP_SEARCH = "Search usage:\n" +
											 "  search (<keyword String>)\n" +
											 "         {([-K or -type <floating|deadline|periodic|all|trash>]\n" +
											 "         [-C or -complete {true|false}]\n" +
											 "         [-T or -time {<blank>|<time>|<<time> to <time>>}]}\n" +
			 								 "Options:\n" +
			 								 "  <Keyword>                   Search title, description and location\n" +
			 								 "                              for the specified keyword\n" +
			 								 "  <floating|deadline|         Specify the type of tasks to search\n" +
			 								 "  periodic|all|trash>         if not specified, only search incomplete tasks\n" +
			 								 "  -complete <true|false>      Specify the complete status you want to search\n" +
			 								 "  -time <blank>               Search only deadline and periodic tasks\n" +
			 								 "        <time>                Search deadline and periodic tasks that\n" +
			 								 "                              due time or start time is before given time\n" +
			 								 "        <<time> to <time>>    Search deadline and periodic tasks that \n" +
			 								 "                              due time or start time is within given period\n" +
			 								 "Example:\n" +
			 								 "  search example keyword -keyword all -complete true -time\n\n" +
			 								 "This will effectively return the completed Deadline tasks which title, description or location contains \"example keyword\"\n(when complete parameter is specified, it is implied that all Periodic Tasks will be excluded)\n";
	public static final String HELP_MARK = "Mark has no extra options\n" +
			 							   "Example: mark 2\n";
	
	public Help(String command){
		this.parameterList.addParameter(CommandType.parse(command));
	}
	
	@Override
	public String execute() throws HandledException{
		CommonUtil.checkNull(this.parameterList.getCommandType(), HandledException.ExceptionType.INVALID_CMD);
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
		case MARK:
			return HELP_MARK;
		case INVALID:
		default:
			return HELP_DEFAULT;
		}
	}

}
