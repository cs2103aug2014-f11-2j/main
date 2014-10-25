package cs2103.command;

import java.util.ArrayList;

import cs2103.task.Task;
import cs2103.util.CommonUtil;

public abstract class QueryCommand extends Command {
	private static final String MESSAGE_EMPTY_LIST = "The task list is empty";
	
	protected static <T extends Task> String parseListResponse(ArrayList<T> taskList){
		if (taskList == null || taskList.size() == 0){
			return MESSAGE_EMPTY_LIST;
		} else {
			StringBuffer sb = new StringBuffer();
			for (Task task:taskList){
				sb.append(task.toSummary());
			}
			return CommonUtil.deleteLastChar(sb);
		}
	}
}
