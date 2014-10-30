package cs2103.command;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskType;
import cs2103.storage.TaskList;
import cs2103.util.CommonUtil;

public class List extends QueryCommand {
	
	public List(String command){
		this.parameterList.addParameter(TaskType.parse(command));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
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
