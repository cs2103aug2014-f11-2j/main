package cs2103.command;

import cs2103.CommonUtil;
import cs2103.FatalException;
import cs2103.HandledException;
import cs2103.Task;
import cs2103.parameters.TaskID;

public class Show extends QueryCommand {
	private static final String MESSAGE_SHOW_FORMAT = "The details for Task %1$d:\n";
	
	public Show(String command) throws HandledException{
		this.parameterList.addParameter(TaskID.parse(command));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNullParameter(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		return formatShowDetail(getTaskByID(parameterList.getTaskID().getValue()));
	}
	
	private static String formatShowDetail(Task task) throws HandledException{
		if (task == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASK_OBJ);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format(MESSAGE_SHOW_FORMAT, task.getTaskID()));
			sb.append(task.toDetail());
			return CommonUtil.deleteLastChar(sb);
		}
	}
}
