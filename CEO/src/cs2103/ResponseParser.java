package cs2103;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.Recur;

public class ResponseParser {
	public static final String HELP_DEFAULT = "The most common commands are:	\n" +
			                                  "    add --title <taskTitle>                add task with title only\n" +
			                                  "											  subfields are compulsory for add command. Type help --add for more" +
			                                  "    list all                               list all existing tasks\n" +
			                                  "    delete <taskID>                        delete task with corresponding taskID" +
			                                  "    update -<taskID> --title <newTitle>    update title of existing task with new title.         " +
			                                  "											  subfields for compulsory for update command. Type help --update for more";
	public static final String HELP_ADD = "Add options:\n" +
										  "    --title <taskTitle>                             title for task\n" +
										  "    --description <description>                     description for task\n" +
										  "    --location <location>                           location for task. Can be anywhere\n" +
										  "    --time <YYYY/MM/DD/HH:MM> OR \n" +
										  "           <YYYY/MM/DD/HH:MM TO YYYY/MM/DD/HH:MM> OR \n" +
										  "           <HH:MM>\n" +
										  "    --recurring <numberOfDays>d<numberOfTimes>\n\n" +
										  "E.g add --title taskTitle --description desc --time 23:59";
	public static final String HELP_DELETE = "Delete has no extra options, and deletes task with specified id\n" +
											 "E.g delete 1";
	public static final String HELP_UPDATE = "Update options:\n" +
											  "    --title <taskTitle>                         title for task\n" +
											  "    --description <description>                 description for task\n" +
											  "    --location <location>                       location for task. Can be anywhere\n" +
											  "    --time <YYYY/MM/DD/HH:MM> OR \n" +
											  "           <YYYY/MM/DD/HH:MM TO YYYY/MM/DD/HH:MM> OR \n" +
											  "           <HH:MM>\n" +
											  "    --recurring <numberOfDays>d<numberOfTimes>\n\n" +
											  "E.g update -1 --recurring 2d5 --location random location";;
	public static final String HELP_LIST = "List options:\n" +
											"    floating                list tasks with no dates set\n" +
											"    periodic                list tasks that are recurring\n" +
											"    deadline                list tasks that have a single deadline\n\n" +
											"    all                     list all tasks" +
											"E.g list deadline";
	public static final String HELP_SHOW = "Delete has no extra options, and displays task details with specified id\n" +
			                               "E.g delete 1";
	public static final String HELP_REDO = "Redo has no extra options, and redo the number of instructions executed as indicated\n" +
											"E.g redo 2";
	public static final String HELP_UNDO = "Undo has no extra options, and undo the number of instructions executed as indicated\n" +
			                               "E.g undo 2";
	private static final String MESSAGE_SHOWDETAIL_FORMAT = "The details for Task %1$d:\n";
	private static final String MESSAGE_EMPTY_LIST = "The task list is empty";
	private static final String MESSAGE_SHOWDETAIL_ERROR_FOMRAT = "Unable to show detail for task %1$d";
	private static final String MESSAGE_TASKS_DUE = "Tasks due within one day:\n";
	private static final String MESSAGE_TASKS_STARTING = "Tasks start within one day:\n";
	private static final String TYPE_FLOATING = "Floating";
	private static final String TYPE_DEADLINE = "Deadline";
	private static final String TYPE_PERIODIC = "Periodic";
	private static final String TYPE_RECURRING = "Recurring";
	private static final String STRING_TYPE = "Type: ";
	private static final String STRING_LOCATION = "Location: ";
	private static final String STRING_DESCRIPTION = "Description: ";
	private static final String STRING_RECUR = "Recurrence: ";
	private static final long DAY_IN_MILLIS = 86400000L;
	
	public static String parseAllListResponse(ArrayList<Task> taskList) throws CEOException{
		if (taskList == null || taskList.size() == 0){
			return MESSAGE_EMPTY_LIST;
		} else {
			StringBuffer sb = new StringBuffer();
			for (Task task:taskList){
				if (task instanceof FloatingTask){
					sb.append(floatingToSummary((FloatingTask) task));
				} else if (task instanceof DeadlineTask){
					sb.append(deadlineToSummary((DeadlineTask) task));
				} else if (task instanceof PeriodicTask){
					sb.append(periodicToSummary((PeriodicTask) task));
				} else {
					throw new CEOException(CEOException.INVALID_TASKID);
				}
			}
			return deleteLastChar(sb);
		}
	}
	
	public static String parseFloatingListResponse(ArrayList<FloatingTask> taskList){
		if (taskList == null || taskList.size() == 0){
			return MESSAGE_EMPTY_LIST;
		} else {
			StringBuffer sb = new StringBuffer();
			for (FloatingTask task:taskList){
				sb.append(floatingToSummary(task));
			}
			return deleteLastChar(sb);
		}
	}
	
	public static String parseDeadlineListResponse(ArrayList<DeadlineTask> taskList){
		if (taskList == null || taskList.size() == 0){
			return MESSAGE_EMPTY_LIST;
		} else {
			StringBuffer sb = new StringBuffer();
			for (DeadlineTask task:taskList){
				sb.append(deadlineToSummary(task));
			}
			return deleteLastChar(sb);
		}
	}
	
	public static String parsePeriodicListResponse(ArrayList<PeriodicTask> taskList){
		if (taskList == null || taskList.size() == 0){
			return MESSAGE_EMPTY_LIST;
		} else {
			StringBuffer sb = new StringBuffer();
			for (PeriodicTask task:taskList){
				sb.append(periodicToSummary(task));
			}
			return deleteLastChar(sb);
		}
	}
	
	public static String alertDeadline(ArrayList<DeadlineTask> taskList){
		if (taskList == null || taskList.size() == 0){
			return null;
		} else {
			ArrayList<DeadlineTask> alertList = getAlertDeadlineList(taskList);
			if (alertList == null || alertList.size() == 0){
				return null;
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append(MESSAGE_TASKS_DUE);
				for (DeadlineTask task:alertList){
					sb.append(deadlineToSummary(task));
				}
				return deleteLastChar(sb);
			}
		}
	}
	
	
	public static String alertPeriodic(ArrayList<PeriodicTask> taskList){
		if (taskList == null || taskList.size() == 0){
			return null;
		} else {
			ArrayList<PeriodicTask> alertList = getAlertPeriodicList(taskList);
			if (alertList == null || alertList.size() == 0){
				return null;
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append(MESSAGE_TASKS_STARTING);
				for (PeriodicTask task:alertList){
					sb.append(periodicToSummary(task));
				}
				return deleteLastChar(sb);
			}
		}
	}
	
	private static ArrayList<DeadlineTask> getAlertDeadlineList(ArrayList<DeadlineTask> taskList){
		ArrayList<DeadlineTask> alertList = new ArrayList<DeadlineTask>();
		long timeNow = System.currentTimeMillis();
		for (DeadlineTask task:taskList){
			long timeDifference = task.getDueTime().getTime() - timeNow;
			if (timeDifference >= 0 && timeDifference < DAY_IN_MILLIS){
				alertList.add(task);
			}
		}
		return alertList;
	}

	private static ArrayList<PeriodicTask> getAlertPeriodicList(ArrayList<PeriodicTask> taskList){
		ArrayList<PeriodicTask> alertList = new ArrayList<PeriodicTask>();
		long timeNow = System.currentTimeMillis();
		for (PeriodicTask task:taskList){
			long timeDifference = task.getStartTime().getTime() - timeNow;
			if (timeDifference >= 0 && timeDifference < DAY_IN_MILLIS){
				alertList.add(task);
			}
		}
		return alertList;
	}
	
	public static String parseShowDetailResponse(Task task, int taskID) throws CEOException{
		if (task == null){
			return String.format(MESSAGE_SHOWDETAIL_ERROR_FOMRAT,taskID);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format(MESSAGE_SHOWDETAIL_FORMAT, taskID));
			if (task instanceof FloatingTask){
				sb.append(floatingToDetail((FloatingTask) task));
			} else if (task instanceof DeadlineTask){
				sb.append(deadlineToDetail((DeadlineTask) task));
			} else if (task instanceof PeriodicTask){
				sb.append(periodicToDetail((PeriodicTask) task));
			} else{
				throw new CEOException(CEOException.INVALID_TASKID);
			}
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
	}
	
	private static String floatingToSummary(FloatingTask task){
		if (task == null) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(task.getTaskID()).append(". ").append(task.getTitle()).append("\n");
		sb.append(STRING_TYPE);
		sb.append(TYPE_FLOATING);
		sb.append("\tStatus: ");
		sb.append(completeToString(task.getComplete()));
		return sb.append("\n").toString();
	}
	
	private static String deadlineToSummary(DeadlineTask task){
		if (task == null) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(task.getTaskID()).append(". ").append(task.getTitle()).append("\n");
		sb.append(STRING_TYPE);
		sb.append(TYPE_DEADLINE);
		sb.append("\tStatus: ");
		sb.append(completeToString(task.getComplete()));
		sb.append("\tDue At: ");
		sb.append(dateToString(task.getDueTime()));
		return sb.append("\n").toString();
	}
	
	private static String periodicToSummary(PeriodicTask task){
		if (task == null) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(task.getTaskID()).append(". ").append(task.getTitle()).append("\n");
		sb.append(STRING_TYPE);
		if (task.getRecurrence() == null){
			sb.append(TYPE_PERIODIC);
		} else {
			sb.append(TYPE_RECURRING);
		}
		sb.append("\tFrom: ");
		sb.append(dateToString(task.getStartTime()));
		sb.append(" To ");
		sb.append(dateToString(task.getEndTime()));
		return sb.append("\n").toString();
	}
	
	private static String floatingToDetail(FloatingTask task){
		if (task == null) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(floatingToSummary(task));
		sb.append(STRING_DESCRIPTION);
		sb.append(task.getDescription());
		return sb.append("\n").toString();
	}
	
	private static String deadlineToDetail(DeadlineTask task){
		if (task == null) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(deadlineToSummary(task));
		sb.append(STRING_DESCRIPTION);
		sb.append(task.getDescription());
		return sb.append("\n").toString();
	}
	
	private static String periodicToDetail(PeriodicTask task){
		if (task == null) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(periodicToSummary(task));
		if (task.getRecurrence() != null){
			sb.append(STRING_RECUR);
			sb.append(recurToString(task.getRecurrence()));
		}
		sb.append(STRING_LOCATION);
		sb.append(task.getLocation()).append("\n");
		sb.append(STRING_DESCRIPTION);
		sb.append(task.getDescription()).append("\n");
		return sb.toString();
	}
	
	private static String dateToString(Date date){
		DateFormat dateFormat;
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
		return dateFormat.format(date);
	}
	
	private static String completeToString(boolean complete){
		return complete?"Completed":"Needs Action";
	}
	
	private static String recurToString(Recur recur){
		StringBuffer sb = new StringBuffer();
		sb.append(recur.getInterval()).append(" ");
		sb.append(recur.getFrequency()).append("\n");
		return sb.toString();
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
