package cs2103.command;

import cs2103.CommonUtil;
import cs2103.FatalException;
import cs2103.HandledException;
import cs2103.parameters.TaskType;

public class List extends ReadCommand {
	
	public List(String command){
		this.parameterList.addParameter(TaskType.parse(command));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNullParameter(this.parameterList.getTaskType(), HandledException.ExceptionType.INVALID_CMD);
		switch (this.parameterList.getTaskType().getValue()){
		case FLOATING:
			return parseListResponse(getFloatingList());
		case DEADLINE:
			return parseListResponse(getDeadlineList());
		case PERIODIC:
			return parseListResponse(getPeriodicList());
		case ALL:
		case INVALID:
		default:
			return parseListResponse(getAllList());
		}
	}

}
