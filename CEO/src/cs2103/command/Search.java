package cs2103.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import cs2103.CommonUtil;
import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Complete;
import cs2103.parameters.Keyword;
import cs2103.parameters.TaskType;
import cs2103.parameters.Time;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;

public class Search extends QueryCommand {
	
	public Search(String command) throws HandledException{
		CommonUtil.checkNullString(command, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> parameterQueue = separateCommand(command);
		if (!command.startsWith("-")){
			this.parameterList.addParameter(TaskType.parse(parameterQueue.poll()));
		}
		Map<String, String> parameterMap = separateParameters(parameterQueue);
		this.parameterList.addParameter(Keyword.parse(getParameterString(parameterMap, Keyword.allowedLiteral)));
		this.parameterList.addParameter(Time.parse(getParameterString(parameterMap, Time.allowedLiteral)));
		this.parameterList.addParameter(Complete.parse(getParameterString(parameterMap, Complete.allowedLiteral)));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		ArrayList<Task> searchList = getInitialList(this.parameterList.getTaskType());
		if (this.parameterList.getKeyword() != null){
			searchList = filterKeyword(searchList, parameterList.getKeyword().getValue());
		}
		if (this.parameterList.getTime() != null){
			searchList = filterTime(searchList, parameterList.getTime().getValue());
		}
		if (this.parameterList.getComplete() != null){
			searchList = filterComplete(searchList, parameterList.getComplete().getValue());
		}
		return parseListResponse(searchList);
	}
	
	private static ArrayList<Task> getInitialList(TaskType taskType) throws HandledException, FatalException{
		ArrayList<Task> searchList = TaskList.getInstance().getAllList();
		if (taskType == null){
			return searchList;
		} else {
			switch (taskType.getValue()){
			case FLOATING:
				return filterType(searchList, FloatingTask.class);
			case DEADLINE:
				return filterType(searchList, DeadlineTask.class);
			case PERIODIC:
				return filterType(searchList, PeriodicTask.class);
			case ALL:
			case INVALID:
			default:
				return searchList;
			}
		}
	}
	
	private static <T extends Task> ArrayList<Task> filterType(ArrayList<Task> searchList, Class<T> type){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null){
			for (Task task:searchList){
				if (task.getClass() == type){
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
	
	private static ArrayList<Task> filterTime(ArrayList<Task> searchList, Date[] time){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null){
			for (Task task:searchList){
				if (task.checkPeriod(time)){
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
	
	private static ArrayList<Task> filterComplete(ArrayList<Task> searchList, boolean complete){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null){
			for (Task task:searchList) {
				if( task instanceof FloatingTask) {
					if ((((FloatingTask)task).getComplete())==complete) {
						returnList.add(task);
					}
				}
				if (task instanceof DeadlineTask) {
					if ((((DeadlineTask)task).getComplete())==complete) {
						returnList.add(task);
					}
				}
			}
		}
		return returnList;
	}
	
	private static ArrayList<Task> filterKeyword(ArrayList<Task> searchList, String keywordString) {
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null){
			for (Task task:searchList){
				if (task.matches(keywordString)){
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
}
