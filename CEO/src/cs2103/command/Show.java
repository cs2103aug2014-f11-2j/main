package cs2103.command;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.storage.TaskList;
import cs2103.task.Task;
import cs2103.util.CommonUtil;

public class Show extends QueryCommand {
	private static final String MESSAGE_SHOW_FORMAT = "The details for Task %1$d:\n";
	
	public Show(String command) throws HandledException{
		this.parameterList.addParameter(TaskID.parse(command));
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		return formatShowDetail(TaskList.getInstance().getTaskByID(parameterList.getTaskID().getValue()));
	}
	
	private Ansi formatShowDetail(Task task) throws HandledException{
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Ansi returnString = ansi().a(String.format(MESSAGE_SHOW_FORMAT, task.getTaskID()));
		returnString.a(task.toDetail());
		return returnString;
	}
}
