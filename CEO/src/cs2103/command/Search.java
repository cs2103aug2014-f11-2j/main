package cs2103.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import org.fusesource.jansi.Ansi;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Complete;
import cs2103.parameters.Keyword;
import cs2103.parameters.TaskType;
import cs2103.parameters.Time;
import cs2103.storage.TaskList;
import cs2103.task.Task;
import cs2103.util.CommonUtil;

public class Search extends QueryCommand {
	
	/**
	 * @param command
	 * @throws HandledException
	 */
	public Search(String command) throws HandledException{
		CommonUtil.checkNull(command, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> parameterQueue = separateCommand(command);
		if (!command.startsWith("-")){
			this.parameterList.addParameter(Keyword.parse(parameterQueue.poll()));
		}
		Map<String, String> parameterMap = separateParameters(parameterQueue);
		this.parameterList.addParameter(TaskType.parse(getParameterString(parameterMap, TaskType.allowedLiteral)));
		this.parameterList.addParameter(Time.parse(getParameterString(parameterMap, Time.allowedLiteral)));
		this.parameterList.addParameter(Complete.parse(getParameterString(parameterMap, Complete.allowedLiteral)));
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
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
	
	/**
	 * @param taskType
	 * @return ArrayList of Task objects filtered by taskType
	 * @throws HandledException
	 * @throws FatalException
	 */
	private ArrayList<Task> getInitialList(TaskType taskType) throws HandledException, FatalException{
		if (taskType == null){
			return TaskList.getInstance().getDefaultList();
		} else {
			switch (taskType.getValue()){
			case FLOATING:
				return toTaskList(TaskList.getInstance().getFloatingList());
			case DEADLINE:
				return toTaskList(TaskList.getInstance().getDeadlineList());
			case PERIODIC:
				return toTaskList(TaskList.getInstance().getPeriodicList());
			case TRASH:
				return TaskList.getInstance().getTrashList();
			case ALL:
			case INVALID:
			default:
				return TaskList.getInstance().getAllList();
			}
		}
	}
	
	/**
	 * @param tasks
	 * @return ArrayList of Tasks from ArrayList of child classes of Task
	 */
	private static <T extends Task> ArrayList<Task> toTaskList(ArrayList<T> tasks){
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:tasks){
			returnList.add(task);
		}
		return returnList;
	}
	
	/**
	 * @param searchList
	 * @param time
	 * @return ArrayList of tasks that are not floating
	 */
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
	
	/**
	 * @param searchList
	 * @param complete
	 * @return ArrayList of Task that are completed
	 */
	private static ArrayList<Task> filterComplete(ArrayList<Task> searchList, boolean complete){
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null){
			for (Task task:searchList) {
				if ((task.getCompleted() != null) == complete){
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
	
	/**
	 * @param searchList
	 * @param keywordString
	 * @return ArrayList of Task that contain keywordString
	 */
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
