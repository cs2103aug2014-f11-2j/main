//@author A0112673L
package cs2103.command;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.storage.TaskList;
import cs2103.task.Task;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

public class Show extends QueryCommand {
	private static final String MESSAGE_SHOW_FORMAT = "The details for Task %1$d:\n";
	private static final String LOG_SHOW = "Executing Show: Parameters: TaskID: %1$d";
	
	/**
	 * Creates an instance of Show from user input
	 * @param command
	 * @throws HandledException
	 */
	public Show(String command) throws HandledException {
		this.parameterList.addParameter(TaskID.parse(command));
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		Logger.getInstance().writeLog(this.formatLogString());
		return formatReturnString(TaskList.getInstance().getTaskByID(parameterList.getTaskID().getValue()));
	}
	
	/**
	 * @param task
	 * @return Ansi formatted string result
	 * @throws HandledException
	 */
	private Ansi formatReturnString(Task task) throws HandledException {
		assert(task != null);
		Ansi returnString = ansi().a(String.format(MESSAGE_SHOW_FORMAT, task.getTaskID()));
		returnString.a(task.toDetail());
		return returnString;
	}
	
	private String formatLogString() throws HandledException {
		assert(this.parameterList.getTaskID() != null);
		return String.format(LOG_SHOW, this.parameterList.getTaskID().getValue());
	}
}
