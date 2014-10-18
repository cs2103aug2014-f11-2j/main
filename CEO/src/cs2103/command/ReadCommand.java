package cs2103.command;

import java.util.ArrayList;

import cs2103.CommonUtil;
import cs2103.Task;

public abstract class ReadCommand extends Command {
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