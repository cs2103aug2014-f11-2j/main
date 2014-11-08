package cs2103.command;

import org.fusesource.jansi.Ansi;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskType;
import cs2103.storage.TaskList;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

public class List extends QueryCommand {
	private final Logger logger;
	private static final String LOG_INITIALIZE = "List Executed";
	/**
	 * Creates an instance of List from user input
	 * @param command
	 */
	public List(String command){
		this.logger = Logger.getInstance();
		this.parameterList.addParameter(TaskType.parse(command));
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		this.logger.writeLog(LOG_INITIALIZE);
		CommonUtil.checkNull(this.parameterList.getTaskType(), HandledException.ExceptionType.INVALID_CMD);
		switch (this.parameterList.getTaskType().getValue()){
		case FLOATING:
			return parseListResponse(TaskList.getInstance().getFloatingList());
		case DEADLINE:
			return parseListResponse(TaskList.getInstance().getDeadlineList());
		case PERIODIC:
			return parseListResponse(TaskList.getInstance().getPeriodicList());
		case ALL:
			return parseListResponse(TaskList.getInstance().getAllList());
		case TRASH:
			return parseListResponse(TaskList.getInstance().getTrashList());
		case DEFAULT:
		case INVALID:
		default:
			return parseListResponse(TaskList.getInstance().getDefaultList());
		}
	}

}
