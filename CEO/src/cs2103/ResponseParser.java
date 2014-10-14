package cs2103;

import java.util.ArrayList;

public class ResponseParser {
	public static final String HELP_DEFAULT = "CEO Usage:\n" +
			                                  "  add [-N or --title <title>] ([-D or -description <description>]\n" + 
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
			                                  "  update <task ID> ([-N or -title <title>] [-C or -complete {true|false}]\n" +
			                                  "                   [-D or -description <description>]\n" +
			                                  "                   [-L or -location <location>]\n" +
			                                  "                   [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
			                                  "                   <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]\n" +
			                                  "                   [-R or -recurring <number h/d/w/m/y>])\n"+
			                                  "  +Update task with corresponding task ID. Enter \"help update\" for more\n\n" +
			                                  "  undo/redo <number of steps>\n"+
			                                  "  +Undo/redo some steps. Enter \"help undo\" or \"help redo\" for more\n\n" +
			                                  "  help\n" + 
			                                  "  +display this message";
	public static final String HELP_ADD = "Add usage:\n"+
										  "  add [-N or --title <title>] ([-D or -description <description>]\n" + 
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
											 "  update <task ID> ([-N or -title <title>] [-C or -complete {true|false}]\n" +
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
			 								 "  search ([-N or -title <title>] [-C or -complete {true|false}]\n" +
			 								 "         [-D or -description <description>]\n" +
			 								 "         [-L or -location <location>]\n" +
			 								 "         [-T or -time {<blank>|<YYYY/MM/DD hh:mm>|\n" +
			 								 "         <<YYYY/MM/DD hh:mm> to <YYYY/MM/DD hh:mm>>}]\n" +
			 								 "Options:\n" +
			 								 "  -title <titleKeyword>       Specify title keyword for search\n" +
			 								 "  -complete <true|false>      Specify the complete status you want to search\n" +
			 								 "                              Periodic tasks will be excluded\n" +
			 								 "  -description <description>  Specify description keyword for search\n" +
			 								 "  -location <location>        Specify location keyword for search\n" +
			 								 "                              only periodic tasks will be included\n" +
			 								 "  -time <blank>                                  Exclude all floating tasks\n" +
			 								 "        <yyyy/MM/dd HH:mm>                       Search deadline and periodic tasks\n" +
			 								 "                                                 that due time or start time\n" +
			 								 "                                                 is before given time point\n" +
			 								 "        <yyyy/MM/dd HH:mm to yyyy/MM/dd HH:mm>   Search deadline and periodic tasks\n" +
			 								 "                                                 that due time or start time\n" +
			 								 "                                                 is within the given period\n" +
			 								 "Example:\n" +
			 								 "  search -title title keyword -description description keyword -complete true\n\n" +
			 								 "This will effectively return the tasks which title contains \"task keyword\", description contains \"description keyword\" and is complete\n";
	
	private static final String MESSAGE_ADD = "You have successfully added a new task.";
	private static final String MESSAGE_ADD_ERROR = "Failed to add new task";
	private static final String MESSAGE_INVALID_TASKTYPE_FORMAT = "Your input Task Type %1$s is invalid, corrected to All";
	private static final String MESSAGE_EMPTY_LIST = "The task list is empty";
	private static final String MESSAGE_SHOW_FORMAT = "The details for Task %1$d:\n";
	private static final String MESSAGE_SHOW_ERROR_FORMAT = "Failed to show task with ID %1$s";
	private static final String MESSAGE_TASKS_DUE = "Tasks due within one day:\n";
	private static final String MESSAGE_TASKS_STARTING = "Tasks start within one day:\n";
	private static final String MESSAGE_DELETE_FORMAT = "You have deleted task with ID %1$s";
	private static final String MESSAGE_DELETE_ERROR_FORMAT = "Failed to delete task with ID %1$s";
	private static final String MESSAGE_UPDATE_FORMAT = "You have updated task with ID %1$s";
	private static final String MESSAGE_UPDATE_ERROR_FORMAT = "Failed to update task with ID %1$s";
	private static final String MESSAGE_UNDO_FORMAT = "Successfully undo %1$d tasks";
	private static final String MESSAGE_REDO_FORMAT = "Successfully redo %1$d tasks";
	private static final String MESSAGE_UPDATE_RECUR_TIME_FORMAT = "Successfully updated %1$d recurring tasks";
	
	public static <T extends Task> String parseListResponse(ArrayList<T> taskList){
		if (taskList == null || taskList.size() == 0){
			return MESSAGE_EMPTY_LIST;
		} else {
			StringBuffer sb = new StringBuffer();
			for (Task task:taskList){
				sb.append(task.toSummary());
			}
			return deleteLastChar(sb);
		}
	}
	
	public static String parseListErrorResponse(String taskType){
		return String.format(MESSAGE_INVALID_TASKTYPE_FORMAT, taskType);
	}
	
	public static String alertDeadline(ArrayList<DeadlineTask> alertList){
		if (alertList == null || alertList.size() == 0){
			return null;
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(MESSAGE_TASKS_DUE);
			sb.append(parseListResponse(alertList));
			return deleteLastChar(sb);
		}
	}
	
	public static String alertPeriodic(ArrayList<PeriodicTask> alertList){
		if (alertList == null || alertList.size() == 0){
			return null;
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(MESSAGE_TASKS_STARTING);
			sb.append(parseListResponse(alertList));
			return deleteLastChar(sb);
		}
	}
	
	public static String parseShowDetailResponse(Task task) throws HandledException{
		if (task == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASK_OBJ);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format(MESSAGE_SHOW_FORMAT, task.getTaskID()));
			sb.append(task.toDetail());
			return deleteLastChar(sb);
		}
	}
	
	public static String parseShowErrorResponse(String taskID){
		return String.format(MESSAGE_SHOW_ERROR_FORMAT, taskID);
	}
	
	public static String parseAddResponse(boolean success){
		return success?MESSAGE_ADD:MESSAGE_ADD_ERROR;
	}
	
	public static String parseDeleteResponse(String taskID, boolean success){
		return String.format(success?MESSAGE_DELETE_FORMAT:MESSAGE_DELETE_ERROR_FORMAT, taskID);
	}
	
	public static String parseUpdateResponse(String taskID, boolean success){
		return String.format(success?MESSAGE_UPDATE_FORMAT:MESSAGE_UPDATE_ERROR_FORMAT, taskID);
	}
	
	public static String parseUndoResponse(int count){
		return String.format(MESSAGE_UNDO_FORMAT, count);
	}
	
	public static String parseRedoResponse(int count){
		return String.format(MESSAGE_REDO_FORMAT, count);
	}
	
	public static String parseUpdateTimeFromRecurResponse(int count){
		return String.format(MESSAGE_UPDATE_RECUR_TIME_FORMAT, count);
	}
	

	public static String parseSearchErrorResponse() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static String deleteLastChar(StringBuffer sb){
		if (sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} else {
			return null;
		}
	}
}
