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
	private final Logger logger;
	private static final String LOG_SHOW = "Show executed for Task %1$s";
	private static final String LOG_SHOW_RESULTS = "Results: \n %1$s";
	
	/**
	 * Creates an instance of Show from user input
	 * @param command
	 * @throws HandledException
	 */
	public Show(String command) throws HandledException{
		this.logger = Logger.getInstance();
		this.parameterList.addParameter(TaskID.parse(command));
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		this.logger.writeLog(LOG_SHOW);
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		this.logger.writeLog(String.format(LOG_SHOW_RESULTS, formatReturnString(TaskList.getInstance().getTaskByID(parameterList.getTaskID().getValue()))));
		return formatReturnString(TaskList.getInstance().getTaskByID(parameterList.getTaskID().getValue()));
	}
	
	/**
	 * @param task
	 * @return Ansi formatted string result
	 * @throws HandledException
	 */
	private Ansi formatReturnString(Task task) throws HandledException{
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Ansi returnString = ansi().a(String.format(MESSAGE_SHOW_FORMAT, task.getTaskID()));
		returnString.a(task.toDetail());
		return returnString;
	}
}
