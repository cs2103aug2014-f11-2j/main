package cs2103;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ResponseParser {
	//All the methods here should be static
	//Taking in various response type and convert to Strings and to be displayed in CommandLineUI
	

	private static final String MESSAGE_SHOWDETAIL_FORMAT = "The details for Task %1$d:\n";
	private static final String MESSAGE_UPDATE = "%1$s has been updated.";
	private static final String MESSAGE_EMPTY_LIST = "The task list is empty";
	private static final String MESSAGE_SHOWDETAIL_ERROR_FOMRAT = "Unable to show detail for task %1$d";
	private static final String MESSAGE_LIST_FORMAT = "Here shows task list for type %1$s\n";
	private static final String STRING_TYPE_FLOATING = "Floating";
	private static final String STRING_TYPE_DEADLINE = "Deadline";
	private static final String STRING_TYPE_PERIODIC = "Periodic";
	private static final String STRING_TYPE = "Type: ";
	private static final String STRING_LOCATION = "Location: ";
	private static final String STRING_DESCRIPTION = "Description: ";
	
	public static String parseAddResponse(boolean success) {
		return null;
	}
	
	public static String parseListResponse(ArrayList<Task> taskList, String taskType){
		if (taskList==null || taskList.size()==0){
			return MESSAGE_EMPTY_LIST;
		}else{
			StringBuffer sb = new StringBuffer();
			sb.append(String.format(MESSAGE_LIST_FORMAT, taskType));
			for (Task task:taskList){
				sb.append(taskToString(task));
			}
			if (sb.length()>0){
				sb.deleteCharAt(sb.length()-1);
				return sb.toString();
			}else{
				return null;
			}
		}
	}
	
	public static String parseShowDetailResponse(Task task, int taskID){
		if (task==null){
			return String.format(MESSAGE_SHOWDETAIL_ERROR_FOMRAT,taskID);
		}else{
			StringBuffer sb = new StringBuffer();
			sb.append(String.format(MESSAGE_SHOWDETAIL_FORMAT, taskID));
			sb.append(taskDetailToString(task));
			sb.deleteCharAt(sb.length()-1);
			return sb.toString();
		}
	}
	
	private static String taskToString(Task task){
		if (task==null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(task.getTaskID()).append(". ").append(task.getTitle()).append("\n");
		sb.append(STRING_TYPE);
		if (task instanceof FloatingTask){
			sb.append(STRING_TYPE_FLOATING);
			sb.append("\t\t\t\t\tStatus: ");
			sb.append(completeToString(((FloatingTask) task).getComplete()));
		}else if (task instanceof DeadlineTask){
			sb.append(STRING_TYPE_DEADLINE);
			sb.append("\t\t\t\tAt: ");
			sb.append(dateToString(((DeadlineTask) task).getDueTime()));
		}else if (task instanceof PeriodicTask){
			sb.append(STRING_TYPE_PERIODIC);
			sb.append("\tFrom: ");
			sb.append(dateToString(((PeriodicTask) task).getStartTime()));
			sb.append(" To ");
			sb.append(dateToString(((PeriodicTask) task).getEndTime()));
		}else{
			return null;
		}
		return sb.append("\n").toString();
	}
	private static String taskDetailToString(Task task){
		if (task==null){
			return null;
		}
		String taskSummary = taskToString(task);
		if (taskSummary==null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(taskSummary);
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
	
	public static String completeToString(boolean complete){
		return complete?"Completed":"Needs Action";
	}
}
